package com.example.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticHelper(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Subtle, tactile micro-vibration pattern for each bead count increment.
     * Feels like the crisp mechanical tap of a physical wooden or stone bead.
     */
    fun vibrateClick() {
        vibrator?.let { v ->
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // EFFECT_TICK or calibrated gentle one-shot
                try {
                    v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                } catch (_: Exception) {
                    v.vibrate(VibrationEffect.createOneShot(14, 110))
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(14, 110))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(15)
            }
        }
    }

    /**
     * Distinct subtle double-tap vibration when passing gold separator beads (every 11 beads).
     */
    fun vibrateSeparator() {
        vibrator?.let { v ->
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 12, 35, 16)
                val amplitudes = intArrayOf(0, 120, 0, 140)
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 12, 35, 16), -1)
            }
        }
    }

    /**
     * Harmonic celebratory vibration pattern upon reaching the full Dhikr target.
     */
    fun vibrateTargetReached() {
        vibrator?.let { v ->
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 45, 45, 45, 80)
                val amplitudes = intArrayOf(0, 160, 0, 180, 0, 240)
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 40, 45, 45, 45, 80), -1)
            }
        }
    }
}
