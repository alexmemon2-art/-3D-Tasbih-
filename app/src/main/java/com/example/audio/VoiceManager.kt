package com.example.audio

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.BuildConfig
import com.example.data.NetworkStatusHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.TimeUnit

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("voice_prefs", Context.MODE_PRIVATE)
    private val cacheDirRoot: File = File(appContext.filesDir, "voice_cache").apply {
        if (!exists()) mkdirs()
    }
    private var systemTTS: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    @Volatile
    private var isTtsReady = false
    private var pendingOfflineSpeak: Pair<String, Float>? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .build()
    }

    companion object {
        const val VOICE_SYSTEM_TTS = "system_tts"
        const val VOICE_ELEVEN_ADIL = "zkCGJHfZ7LHDgElbSPyO"
        const val VOICE_ELEVEN_SANA = "mRdG9GYEjJmIzqbYTidv"
        private const val KEY_SELECTED_VOICE = "selected_voice"
        private const val KEY_SPEECH_SPEED = "speech_speed"
        private const val PLACEHOLDER_KEY = "YOUR_ELEVENLABS_API_KEY"

        fun buildCacheFileName(voiceId: String, text: String): String {
            val normalized = "${voiceId}_${text.trim().lowercase(Locale.US)}"
            val digest = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray())
            val hex = digest.joinToString("") { "%02x".format(it) }.take(24)
            return "tts_$hex.mp3"
        }
    }

    data class VoiceOption(
        val id: String,
        val name: String,
        val desc: String,
        val icon: String,
        val isOnline: Boolean
    )

    val allVoices = listOf(
        VoiceOption(
            VOICE_SYSTEM_TTS,
            "Phone Ki Awaz",
            "Offline • Har waqt available (No Internet)",
            "📳",
            false
        ),
        VoiceOption(
            VOICE_ELEVEN_ADIL,
            "Adil",
            "Online + Offline Cache • Deep Calm Arabic",
            "🕌",
            true
        ),
        VoiceOption(
            VOICE_ELEVEN_SANA,
            "Sana",
            "Online + Offline Cache • Soft Clear Arabic",
            "🌸",
            true
        )
    )

    init {
        try {
            systemTTS = TextToSpeech(appContext, this)
        } catch (_: Exception) {
            isTtsReady = false
        }
    }

    fun getSelectedVoiceId(): String =
        prefs.getString(KEY_SELECTED_VOICE, VOICE_SYSTEM_TTS) ?: VOICE_SYSTEM_TTS

    fun setSelectedVoice(voiceId: String) {
        val valid = if (allVoices.any { it.id == voiceId }) voiceId else VOICE_SYSTEM_TTS
        prefs.edit().putString(KEY_SELECTED_VOICE, valid).apply()
    }

    fun getCurrentVoiceInfo(): VoiceOption =
        allVoices.find { it.id == getSelectedVoiceId() } ?: allVoices.first()

    fun getSpeed(): Float =
        prefs.getFloat(KEY_SPEECH_SPEED, 1.0f).coerceIn(0.5f, 2.0f)

    fun setSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.5f, 2.0f)
        try {
            systemTTS?.setSpeechRate(clamped)
        } catch (_: Exception) {}
        prefs.edit().putFloat(KEY_SPEECH_SPEED, clamped).apply()
    }

    fun hasConfiguredElevenLabsKey(): Boolean {
        val key = BuildConfig.ELEVENLABS_API_KEY.trim()
        return key.isNotEmpty() && key != PLACEHOLDER_KEY && key != "YOUR_API_KEY_HERE"
    }

    fun getCachedClipCount(): Int {
        return cacheDirRoot.listFiles { file ->
            file.isFile && file.name.endsWith(".mp3") && file.length() > 0L
        }?.size ?: 0
    }

    private fun getCachedAudioFile(voiceId: String, text: String): File {
        return File(cacheDirRoot, buildCacheFileName(voiceId, text))
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            try {
                val arResult = systemTTS?.setLanguage(Locale.forLanguageTag("ar"))
                if (arResult == TextToSpeech.LANG_MISSING_DATA || arResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    systemTTS?.setLanguage(Locale.ENGLISH)
                }
                systemTTS?.setPitch(1.0f)
                systemTTS?.setSpeechRate(getSpeed())
                pendingOfflineSpeak?.let { (pendingText, pendingSpeed) ->
                    pendingOfflineSpeak = null
                    speakOfflineTts(pendingText, pendingSpeed, null)
                }
            } catch (_: Exception) {
                isTtsReady = false
            }
        }
    }

    /**
     * Plays voice seamlessly both Online and Offline:
     * 1. If offline phone voice is selected -> plays offline TTS directly.
     * 2. If Adil/Sana is selected -> checks persistent offline MP3 cache first (works offline!).
     * 3. If not cached and device is Online with ElevenLabs key -> fetches, caches to disk, and plays.
     * 4. If Offline or API fails/missing -> falls back automatically to offline TTS without blocking.
     */
    fun speak(
        text: String,
        overrideVoiceId: String? = null,
        overrideSpeed: Float? = null,
        onComplete: (() -> Unit)? = null
    ) {
        val voiceId = overrideVoiceId ?: getSelectedVoiceId()
        val speed = (overrideSpeed ?: getSpeed()).coerceIn(0.5f, 2.0f)
        val cleanText = text.ifBlank { "SubhanAllah" }

        val isOnlineVoice = voiceId == VOICE_ELEVEN_ADIL || voiceId == VOICE_ELEVEN_SANA
        if (!isOnlineVoice) {
            speakOfflineTts(cleanText, speed, onComplete)
            return
        }

        // 1. Check persistent offline cache first so previously heard phrases work offline too
        val cachedFile = getCachedAudioFile(voiceId, cleanText)
        if (cachedFile.exists() && cachedFile.length() > 0L) {
            playMp3File(cachedFile, cleanText, speed, onComplete)
            return
        }

        // 2. If online and API key configured -> fetch & cache permanently
        val isOnline = NetworkStatusHelper.isOnline(appContext)
        if (isOnline && hasConfiguredElevenLabsKey()) {
            speakWithElevenLabs(cleanText, voiceId, speed, cachedFile, onComplete)
        } else {
            // 3. Offline fallback: play immediately with device offline TTS
            speakOfflineTts(cleanText, speed, onComplete)
        }
    }

    suspend fun preloadPhrasesForOffline(
        voiceId: String,
        phrases: List<String>
    ): Int = withContext(Dispatchers.IO) {
        if (!hasConfiguredElevenLabsKey() || !NetworkStatusHelper.isOnline(appContext)) {
            return@withContext 0
        }
        val apiKey = BuildConfig.ELEVENLABS_API_KEY.trim()
        var newlyCached = 0
        for (phrase in phrases) {
            val file = getCachedAudioFile(voiceId, phrase)
            if (file.exists() && file.length() > 0L) continue
            val bytes = fetchElevenLabsMp3(phrase, voiceId, apiKey)
            if (bytes != null && bytes.isNotEmpty()) {
                try {
                    file.writeBytes(bytes)
                    newlyCached++
                } catch (_: Exception) {}
            }
        }
        newlyCached
    }

    private fun speakOfflineTts(text: String, speed: Float, onComplete: (() -> Unit)?) {
        stopMediaPlayerOnly()
        if (isTtsReady && systemTTS != null) {
            try {
                systemTTS?.setSpeechRate(speed)
                systemTTS?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tasbih_voice_${System.currentTimeMillis()}")
            } catch (_: Exception) {
                Toast.makeText(appContext, "❌ Awaz available nahi", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Queue once while TextToSpeech finishes async engine initialization
            pendingOfflineSpeak = text to speed
        }
        onComplete?.invoke()
    }

    private fun speakWithElevenLabs(
        text: String,
        voiceId: String,
        speed: Float,
        targetCacheFile: File,
        onComplete: (() -> Unit)?
    ) {
        scope.launch {
            val audioBytes = withContext(Dispatchers.IO) {
                fetchElevenLabsMp3(text, voiceId, BuildConfig.ELEVENLABS_API_KEY.trim())
            }
            if (audioBytes != null && audioBytes.isNotEmpty()) {
                val cachedOk = withContext(Dispatchers.IO) {
                    try {
                        targetCacheFile.writeBytes(audioBytes)
                        true
                    } catch (_: Exception) {
                        false
                    }
                }
                if (cachedOk) {
                    playMp3File(targetCacheFile, text, speed, onComplete)
                } else {
                    playMp3BytesTemp(audioBytes, text, speed, onComplete)
                }
            } else {
                // Seamless fallback to offline TTS on network hiccup
                speakOfflineTts(text, speed, onComplete)
            }
        }
    }

    private fun fetchElevenLabsMp3(text: String, voiceId: String, apiKey: String): ByteArray? {
        return try {
            val payload = JSONObject().apply {
                put("text", text)
                put("model_id", "eleven_multilingual_v2")
            }.toString()
            val body = payload.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://api.elevenlabs.io/v1/text-to-speech/$voiceId")
                .addHeader("xi-api-key", apiKey)
                .addHeader("Accept", "audio/mpeg")
                .post(body)
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) response.body?.bytes() else null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun playMp3File(
        file: File,
        fallbackText: String,
        speed: Float,
        onComplete: (() -> Unit)?
    ) {
        try {
            stopMediaPlayerOnly()
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(file.absolutePath)
                prepare()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        playbackParams = playbackParams.setSpeed(speed.coerceIn(0.5f, 2.0f))
                    } catch (_: Exception) {}
                }
                setOnCompletionListener { mp ->
                    try {
                        mp.release()
                    } catch (_: Exception) {}
                    if (mediaPlayer === mp) mediaPlayer = null
                    onComplete?.invoke()
                }
                setOnErrorListener { mp, _, _ ->
                    try {
                        mp.release()
                    } catch (_: Exception) {}
                    if (mediaPlayer === mp) mediaPlayer = null
                    speakOfflineTts(fallbackText, speed, onComplete)
                    true
                }
                start()
            }
            mediaPlayer = player
        } catch (_: Exception) {
            speakOfflineTts(fallbackText, speed, onComplete)
        }
    }

    private fun playMp3BytesTemp(
        bytes: ByteArray,
        fallbackText: String,
        speed: Float,
        onComplete: (() -> Unit)?
    ) {
        try {
            val tempFile = File.createTempFile("eleven_tts_", ".mp3", appContext.cacheDir)
            tempFile.writeBytes(bytes)
            playMp3File(tempFile, fallbackText, speed) {
                tempFile.delete()
                onComplete?.invoke()
            }
        } catch (_: Exception) {
            speakOfflineTts(fallbackText, speed, onComplete)
        }
    }

    private fun stopMediaPlayerOnly() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    fun stop() {
        pendingOfflineSpeak = null
        try {
            systemTTS?.stop()
        } catch (_: Exception) {}
        stopMediaPlayerOnly()
    }

    fun destroy() {
        stop()
        try {
            systemTTS?.shutdown()
        } catch (_: Exception) {}
        systemTTS = null
        scope.cancel()
    }
}
