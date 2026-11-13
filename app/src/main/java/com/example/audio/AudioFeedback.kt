package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import com.example.model.ClickSoundStyle
import com.example.model.CompletionSoundStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class AudioFeedback(context: Context? = null) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100

    private val woodenClickPcm: ByteArray = generateBeadClickPcm(sampleRate, durationMs = 38, baseFreq = 820.0, decayRate = 90.0)
    private val softBeepPcm: ByteArray = generateSoftBeepPcm(sampleRate, durationMs = 45, freq = 660.0)
    private val crystalTapPcm: ByteArray = generateBeadClickPcm(sampleRate, durationMs = 32, baseFreq = 1480.0, decayRate = 110.0)

    private val completeChimePcm: ByteArray = generateChimePcm(sampleRate, durationMs = 320)
    private val gentleAlertBeepPcm: ByteArray = generateDoubleBeepPcm(sampleRate, durationMs = 240)

    private var tts: TextToSpeech? = null
    @Volatile
    private var ttsReady = false

    init {
        if (context != null) {
            try {
                tts = TextToSpeech(context.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val arabicRes = tts?.setLanguage(Locale("ar"))
                        if (arabicRes == TextToSpeech.LANG_MISSING_DATA || arabicRes == TextToSpeech.LANG_NOT_SUPPORTED) {
                            tts?.setLanguage(Locale.US)
                        }
                        tts?.setSpeechRate(0.9f)
                        ttsReady = true
                    }
                }
            } catch (_: Exception) {
                ttsReady = false
            }
        }
    }

    fun playBeadClick(style: ClickSoundStyle = ClickSoundStyle.WOODEN_BEAD) {
        val pcm = when (style) {
            ClickSoundStyle.WOODEN_BEAD -> woodenClickPcm
            ClickSoundStyle.SOFT_BEEP -> softBeepPcm
            ClickSoundStyle.CRYSTAL_TAP -> crystalTapPcm
            ClickSoundStyle.SILENT -> return
        }
        scope.launch {
            playRawPcm(pcm)
        }
    }

    fun playCompletionChime(style: CompletionSoundStyle = CompletionSoundStyle.HARMONIC_CHIME) {
        when (style) {
            CompletionSoundStyle.HARMONIC_CHIME -> {
                scope.launch { playRawPcm(completeChimePcm) }
            }
            CompletionSoundStyle.GENTLE_BEEP -> {
                scope.launch { playRawPcm(gentleAlertBeepPcm) }
            }
            CompletionSoundStyle.ALLAHU_AKBAR_VOICE -> {
                if (ttsReady && tts != null) {
                    try {
                        tts?.speak("Allahu Akbar", TextToSpeech.QUEUE_FLUSH, null, "tasbih_complete_takbir")
                    } catch (_: Exception) {
                        scope.launch { playRawPcm(completeChimePcm) }
                    }
                } else {
                    scope.launch { playRawPcm(completeChimePcm) }
                }
            }
            CompletionSoundStyle.SILENT -> Unit
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        ttsReady = false
    }

    private fun playRawPcm(pcm: ByteArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcm, 0, pcm.size)
            audioTrack.play()
            scope.launch {
                kotlinx.coroutines.delay(450)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // AudioTrack unavailable or muted
        }
    }

    companion object {
        private fun generateBeadClickPcm(
            sampleRate: Int,
            durationMs: Int,
            baseFreq: Double,
            decayRate: Double
        ): ByteArray {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val pcm = ByteArray(numSamples * 2)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = baseFreq * (1.0 - (t / (durationMs / 1000.0)) * 0.5)
                val decay = exp(-t * decayRate)
                val sampleValue = (sin(2 * PI * freq * t) * decay * 0.4 * 32767).toInt()
                val clamped = sampleValue.coerceIn(-32767, 32767).toShort()

                pcm[i * 2] = (clamped.toInt() and 0xFF).toByte()
                pcm[i * 2 + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
            }
            return pcm
        }

        private fun generateSoftBeepPcm(sampleRate: Int, durationMs: Int, freq: Double): ByteArray {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val pcm = ByteArray(numSamples * 2)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val fadeIn = (i.toDouble() / (numSamples * 0.15)).coerceAtMost(1.0)
                val fadeOut = exp(-t * 45.0)
                val sampleValue = (sin(2 * PI * freq * t) * fadeIn * fadeOut * 0.32 * 32767).toInt()
                val clamped = sampleValue.coerceIn(-32767, 32767).toShort()
                pcm[i * 2] = (clamped.toInt() and 0xFF).toByte()
                pcm[i * 2 + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
            }
            return pcm
        }

        private fun generateChimePcm(sampleRate: Int, durationMs: Int): ByteArray {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val pcm = ByteArray(numSamples * 2)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val decay = exp(-t * 10.0)
                val wave1 = sin(2 * PI * 523.25 * t)
                val wave2 = sin(2 * PI * 783.99 * t) * 0.6
                val wave3 = sin(2 * PI * 1046.50 * t) * 0.35
                val sampleValue = ((wave1 + wave2 + wave3) * decay * 0.30 * 32767).toInt()
                val clamped = sampleValue.coerceIn(-32767, 32767).toShort()

                pcm[i * 2] = (clamped.toInt() and 0xFF).toByte()
                pcm[i * 2 + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
            }
            return pcm
        }

        private fun generateDoubleBeepPcm(sampleRate: Int, durationMs: Int): ByteArray {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val pcm = ByteArray(numSamples * 2)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val pulse = if (t < 0.09 || (t > 0.13 && t < 0.22)) 1.0 else 0.0
                val sampleValue = (sin(2 * PI * 880.0 * t) * pulse * 0.30 * 32767).toInt()
                val clamped = sampleValue.coerceIn(-32767, 32767).toShort()
                pcm[i * 2] = (clamped.toInt() and 0xFF).toByte()
                pcm[i * 2 + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
            }
            return pcm
        }
    }
}
