package com.calcmate.scientificcalculator.core.parser

import kotlin.math.abs
import kotlin.math.floor

/**
 * Utility for Degree-Minute-Second (DMS) conversions and arithmetic.
 */
object DmsUtil {

    /**
     * Convert a decimal-degree value to (degrees, minutes, seconds).
     */
    fun decimalToDms(decimal: Double): Triple<Int, Int, Double> {
        val negative = decimal < 0
        val abs = abs(decimal)
        val degrees = floor(abs).toInt()
        val remaining = (abs - degrees) * 60.0
        val minutes = floor(remaining).toInt()
        val seconds = (remaining - minutes) * 60.0

        return if (negative) {
            Triple(-degrees, minutes, seconds)
        } else {
            Triple(degrees, minutes, seconds)
        }
    }

    /**
     * Convert (degrees, minutes, seconds) to a decimal-degree value.
     */
    fun dmsToDecimal(degrees: Int, minutes: Int, seconds: Double): Double {
        val sign = if (degrees < 0) -1.0 else 1.0
        return sign * (abs(degrees.toDouble()) + minutes / 60.0 + seconds / 3600.0)
    }

    /**
     * Format a DMS triple as "12°34'56.7"".
     */
    fun formatDms(degrees: Int, minutes: Int, seconds: Double): String {
        // Round seconds to 1 decimal place for display
        val roundedSeconds = Math.round(seconds * 10.0) / 10.0
        val secStr = if (roundedSeconds == floor(roundedSeconds)) {
            roundedSeconds.toInt().toString()
        } else {
            "%.1f".format(roundedSeconds)
        }
        return "$degrees°$minutes'$secStr\""
    }

    /**
     * Add two DMS triples by converting to decimal, adding, and converting back.
     */
    fun addDms(
        dms1: Triple<Int, Int, Double>,
        dms2: Triple<Int, Int, Double>,
    ): Triple<Int, Int, Double> {
        val dec1 = dmsToDecimal(dms1.first, dms1.second, dms1.third)
        val dec2 = dmsToDecimal(dms2.first, dms2.second, dms2.third)
        return decimalToDms(dec1 + dec2)
    }

    /**
     * Subtract dms2 from dms1.
     */
    fun subtractDms(
        dms1: Triple<Int, Int, Double>,
        dms2: Triple<Int, Int, Double>,
    ): Triple<Int, Int, Double> {
        val dec1 = dmsToDecimal(dms1.first, dms1.second, dms1.third)
        val dec2 = dmsToDecimal(dms2.first, dms2.second, dms2.third)
        return decimalToDms(dec1 - dec2)
    }

    /**
     * Multiply a DMS triple by a scalar value.
     */
    fun multiplyDmsByScalar(
        dms: Triple<Int, Int, Double>,
        scalar: Double,
    ): Triple<Int, Int, Double> {
        val decimal = dmsToDecimal(dms.first, dms.second, dms.third)
        return decimalToDms(decimal * scalar)
    }
}
