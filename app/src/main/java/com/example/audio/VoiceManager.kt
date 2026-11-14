package com.example.audio

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.BuildConfig
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
import java.util.Locale
import java.util.concurrent.TimeUnit

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("voice_prefs", Context.MODE_PRIVATE)
    private var systemTTS: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    @Volatile
    private var isTtsReady = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    companion object {
        const val VOICE_SYSTEM_TTS = "system_tts"
        const val VOICE_ELEVEN_ADIL = "zkCGJHfZ7LHDgElbSPyO"
        const val VOICE_ELEVEN_SANA = "mRdG9GYEjJmIzqbYTidv"
        private const val KEY_SELECTED_VOICE = "selected_voice"
        private const val KEY_SPEECH_SPEED = "speech_speed"
        private const val PLACEHOLDER_KEY = "YOUR_ELEVENLABS_API_KEY"
    }

    data class VoiceOption(
        val id: String,
        val name: String,
        val desc: String,
        val icon: String,
        val isOnline: Boolean
    )

    val allVoices = listOf(
        VoiceOption(VOICE_SYSTEM_TTS, "Phone Ki Awaz", "Offline • Har waqt available", "📳", false),
        VoiceOption(VOICE_ELEVEN_ADIL, "Adil", "Deep & Calm Arabic • Internet Zaruri", "🕌", true),
        VoiceOption(VOICE_ELEVEN_SANA, "Sana", "Soft & Honest Arabic • Internet Zaruri", "🌸", true)
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            try {
                val arResult = systemTTS?.setLanguage(Locale("ar"))
                if (arResult == TextToSpeech.LANG_MISSING_DATA || arResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    systemTTS?.setLanguage(Locale.ENGLISH)
                }
                systemTTS?.setPitch(1.0f)
                systemTTS?.setSpeechRate(getSpeed())
            } catch (_: Exception) {
                isTtsReady = false
            }
        }
    }

    fun speak(
        text: String,
        overrideVoiceId: String? = null,
        overrideSpeed: Float? = null,
        onComplete: (() -> Unit)? = null
    ) {
        val voiceId = overrideVoiceId ?: getSelectedVoiceId()
        val speed = (overrideSpeed ?: getSpeed()).coerceIn(0.5f, 2.0f)

        val isOnlineVoice = voiceId == VOICE_ELEVEN_ADIL || voiceId == VOICE_ELEVEN_SANA
        if (isOnlineVoice && hasConfiguredElevenLabsKey()) {
            speakWithElevenLabs(text, voiceId, speed, onComplete)
        } else {
            if (isOnlineVoice && !hasConfiguredElevenLabsKey()) {
                Toast.makeText(
                    appContext,
                    "ELEVENLABS_API_KEY Secrets panel mein set karein — Offline awaz chal rahi hai",
                    Toast.LENGTH_SHORT
                ).show()
            }
            speakOfflineTts(text, speed, onComplete)
        }
    }

    private fun speakOfflineTts(text: String, speed: Float, onComplete: (() -> Unit)?) {
        if (isTtsReady && systemTTS != null) {
            try {
                systemTTS?.setSpeechRate(speed)
                systemTTS?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tasbih_voice_${System.currentTimeMillis()}")
            } catch (_: Exception) {
                Toast.makeText(appContext, "❌ Awaz available nahi", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(appContext, "❌ Awaz available nahi", Toast.LENGTH_SHORT).show()
        }
        onComplete?.invoke()
    }

    private fun speakWithElevenLabs(
        text: String,
        voiceId: String,
        speed: Float,
        onComplete: (() -> Unit)?
    ) {
        scope.launch {
            val audioBytes = withContext(Dispatchers.IO) {
                fetchElevenLabsMp3(text, voiceId, BuildConfig.ELEVENLABS_API_KEY.trim())
            }
            if (audioBytes != null && audioBytes.isNotEmpty()) {
                playMp3Bytes(audioBytes, speed, onComplete)
            } else {
                Toast.makeText(appContext, "⚠️ Internet ya API key check karein — offline awaz chal rahi hai", Toast.LENGTH_SHORT).show()
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

    private fun playMp3Bytes(bytes: ByteArray, speed: Float, onComplete: (() -> Unit)?) {
        try {
            stopMediaPlayerOnly()
            val tempFile = File.createTempFile("eleven_tts_", ".mp3", appContext.cacheDir)
            tempFile.writeBytes(bytes)
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(tempFile.absolutePath)
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
                    tempFile.delete()
                    onComplete?.invoke()
                }
                setOnErrorListener { mp, _, _ ->
                    try {
                        mp.release()
                    } catch (_: Exception) {}
                    if (mediaPlayer === mp) mediaPlayer = null
                    tempFile.delete()
                    onComplete?.invoke()
                    true
                }
                start()
            }
            mediaPlayer = player
        } catch (_: Exception) {
            speakOfflineTts("SubhanAllah", speed, onComplete)
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
