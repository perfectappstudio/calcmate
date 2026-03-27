package com.perfectappstudio.scientificcalc.util

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.content.getSystemService

/**
 * Utility for performing haptic feedback with fallbacks for older APIs.
 */
object HapticUtil {

    /**
     * Light tap — use for regular button presses.
     */
    fun performClick(view: View) {
        val performed = view.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
        )
        if (!performed) {
            vibrateFallback(view, 20L)
        }
    }

    /**
     * Medium confirmation — use for equals / solve actions.
     */
    fun performConfirm(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val performed = view.performHapticFeedback(
                HapticFeedbackConstants.CONFIRM,
            )
            if (!performed) vibrateFallback(view, 40L)
        } else {
            vibrateFallback(view, 40L)
        }
    }

    /**
     * Error buzz — use for invalid input or parse errors.
     */
    fun performReject(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val performed = view.performHapticFeedback(
                HapticFeedbackConstants.REJECT,
            )
            if (!performed) vibrateErrorPattern(view)
        } else {
            vibrateErrorPattern(view)
        }
    }

    // --- Fallbacks using Vibrator service ---

    @Suppress("DEPRECATION")
    private fun vibrateFallback(view: View, durationMs: Long) {
        val vibrator = view.context.getSystemService<Vibrator>() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE),
            )
        } else {
            vibrator.vibrate(durationMs)
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrateErrorPattern(view: View) {
        val vibrator = view.context.getSystemService<Vibrator>() ?: return
        // Short-pause-short pattern to signal rejection
        val pattern = longArrayOf(0, 30, 50, 30)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, -1),
            )
        } else {
            vibrator.vibrate(pattern, -1)
        }
    }
}
