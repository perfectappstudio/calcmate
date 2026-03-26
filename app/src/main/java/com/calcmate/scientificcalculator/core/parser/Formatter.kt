package com.calcmate.scientificcalculator.core.parser

import com.calcmate.scientificcalculator.core.model.DisplayMode as SettingsDisplayMode
import com.calcmate.scientificcalculator.core.model.DisplaySettings
import com.calcmate.scientificcalculator.core.model.FractionFormat
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToLong

enum class DisplayMode {
    DECIMAL,
    SCIENTIFIC,
    FRACTION
}

class Formatter(private val mode: DisplayMode = DisplayMode.DECIMAL) {

    fun format(result: CalcResult): String = result.toDisplayString(this)

    fun format(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value == Double.POSITIVE_INFINITY) return "∞"
        if (value == Double.NEGATIVE_INFINITY) return "-∞"

        return when (mode) {
            DisplayMode.DECIMAL -> formatDecimal(value)
            DisplayMode.SCIENTIFIC -> formatScientific(value)
            DisplayMode.FRACTION -> formatFraction(value)
        }
    }

    // ─────────────────────────────────────────────────────────
    // Phase 2: DisplaySettings-aware formatting
    // ─────────────────────────────────────────────────────────

    /**
     * Format a value according to the given [DisplaySettings].
     */
    fun formatWithSettings(value: Double, settings: DisplaySettings): String {
        if (value.isNaN()) return "Error"
        if (value == Double.POSITIVE_INFINITY) return "∞"
        if (value == Double.NEGATIVE_INFINITY) return "-∞"

        val base = when (settings.mode) {
            SettingsDisplayMode.FIX -> formatFix(value, settings.digits)
            SettingsDisplayMode.SCI -> formatSci(value, settings.digits)
            SettingsDisplayMode.NORM_1 -> formatNorm1(value)
            SettingsDisplayMode.NORM_2 -> formatNorm2(value)
        }

        return if (settings.engineeringOn) {
            // If the base result contains scientific notation, shift to engineering
            formatEngineering(value)
        } else {
            base
        }
    }

    /**
     * FIX(n): Round to n decimal places, always show exactly n places.
     * E.g., Fix 3: 400.000
     */
    private fun formatFix(value: Double, digits: Int): String {
        if (value == 0.0) return "%.${digits}f".format(0.0)
        return "%.${digits}f".format(value)
    }

    /**
     * SCI(n): n significant digits in scientific notation.
     * E.g., Sci 2: 1/3 = 3.3×10^-01
     */
    private fun formatSci(value: Double, digits: Int): String {
        if (value == 0.0) return "0 × 10^00"

        val exponent = floor(log10(abs(value))).toInt()
        val mantissa = value / 10.0.pow(exponent)

        // digits = total significant digits, so decimal places = digits - 1
        val decimalPlaces = (digits - 1).coerceAtLeast(0)
        val mantissaStr = "%.${decimalPlaces}f".format(mantissa)
        val expStr = "%+03d".format(exponent) // e.g., -01, +02
        return "$mantissaStr × 10^$expStr"
    }

    /**
     * NORM 1: Use scientific notation for |x| < 10^-2 or |x| >= 10^10.
     */
    private fun formatNorm1(value: Double): String {
        if (value == 0.0) return "0"
        val absValue = abs(value)
        return if (absValue < 1e-2 || absValue >= 1e10) {
            formatScientific(value)
        } else {
            formatDecimal(value)
        }
    }

    /**
     * NORM 2: Use scientific notation for |x| < 10^-9 or |x| >= 10^10.
     */
    private fun formatNorm2(value: Double): String {
        if (value == 0.0) return "0"
        val absValue = abs(value)
        return if (absValue < 1e-9 || absValue >= 1e10) {
            formatScientific(value)
        } else {
            formatDecimal(value)
        }
    }

    /**
     * Engineering notation: shift exponent to the nearest multiple of 3.
     * E.g., 56088 -> 56.088×10^03
     */
    fun formatEngineering(value: Double): String {
        if (value == 0.0) return "0 × 10^00"

        val exponent = floor(log10(abs(value))).toInt()
        // Round exponent DOWN to nearest multiple of 3
        val engExponent = exponent - ((exponent % 3) + 3) % 3
        val mantissa = value / 10.0.pow(engExponent)

        val mantissaStr = stripTrailingZeros("%.9f".format(mantissa))
        val expStr = "%+03d".format(engExponent)
        return "$mantissaStr × 10^$expStr"
    }

    /**
     * Engineering notation with SI symbols:
     * 10^15->P, 10^12->T, 10^9->G, 10^6->M, 10^3->k,
     * 10^-3->m, 10^-6->μ, 10^-9->n, 10^-12->p, 10^-15->f
     */
    fun formatEngineeringSymbol(value: Double): String {
        if (value == 0.0) return "0"

        val exponent = floor(log10(abs(value))).toInt()
        val engExponent = exponent - ((exponent % 3) + 3) % 3

        val symbol = engineeringSymbols[engExponent]
        val mantissa = value / 10.0.pow(engExponent)
        val mantissaStr = stripTrailingZeros("%.9f".format(mantissa))

        return if (symbol != null) {
            "$mantissaStr$symbol"
        } else {
            // Fall back to numeric engineering notation
            val expStr = "%+03d".format(engExponent)
            "$mantissaStr × 10^$expStr"
        }
    }

    /**
     * Round the value according to the current display settings (Rnd function).
     * Returns a Double that, when displayed with the same settings, shows the same digits.
     */
    fun roundToDisplay(value: Double, settings: DisplaySettings): Double {
        return when (settings.mode) {
            SettingsDisplayMode.FIX -> {
                val factor = 10.0.pow(settings.digits)
                round(value * factor) / factor
            }
            SettingsDisplayMode.SCI -> {
                if (value == 0.0) return 0.0
                val exponent = floor(log10(abs(value))).toInt()
                val factor = 10.0.pow(settings.digits - 1 - exponent)
                round(value * factor) / factor
            }
            SettingsDisplayMode.NORM_1, SettingsDisplayMode.NORM_2 -> {
                // Norm modes use 10 significant digits
                if (value == 0.0) return 0.0
                val exponent = floor(log10(abs(value))).toInt()
                val factor = 10.0.pow(9 - exponent) // 10 sig digits
                round(value * factor) / factor
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // Original formatting methods
    // ─────────────────────────────────────────────────────────

    private fun formatDecimal(value: Double): String {
        if (value == 0.0) return "0"

        val absValue = abs(value)

        if (absValue >= 1e10 || absValue < 1e-6) {
            return formatScientific(value)
        }

        val significantDigits = 10
        val magnitude = floor(log10(absValue)).toInt()
        val decimalPlaces = (significantDigits - magnitude - 1).coerceIn(0, 15)

        val formatted = "%.${decimalPlaces}f".format(value)
        return stripTrailingZeros(formatted)
    }

    private fun formatScientific(value: Double): String {
        if (value == 0.0) return "0 × 10^0"

        val exponent = floor(log10(abs(value))).toInt()
        val mantissa = value / 10.0.pow(exponent)

        val mantissaStr = stripTrailingZeros("%.9f".format(mantissa))
        return "$mantissaStr × 10^$exponent"
    }

    private fun formatFraction(value: Double): String {
        if (value == 0.0) return "0"

        val negative = value < 0
        val absValue = abs(value)

        val wholePart = floor(absValue).toLong()
        val fractionalPart = absValue - wholePart

        if (fractionalPart < 1e-10) {
            return if (negative) "-$wholePart" else "$wholePart"
        }

        val (num, den) = toFraction(fractionalPart)

        if (den == 0L) {
            return formatDecimal(value)
        }

        val sign = if (negative) "-" else ""

        return if (wholePart == 0L) {
            "$sign$num/$den"
        } else {
            "$sign$wholePart $num/$den"
        }
    }

    // Continued fraction algorithm to find best rational approximation
    private fun toFraction(value: Double, maxIterations: Int = 20, tolerance: Double = 1e-10): Pair<Long, Long> {
        var x = value
        var a0 = floor(x).toLong()
        var num1 = a0
        var den1 = 1L
        var num2 = 1L
        var den2 = 0L

        for (i in 0 until maxIterations) {
            val remainder = x - floor(x)
            if (abs(remainder) < tolerance) break
            x = 1.0 / remainder
            val a = floor(x).toLong()

            val nextNum = a * num1 + num2
            val nextDen = a * den1 + den2

            if (nextDen > 1_000_000) break

            num2 = num1
            den2 = den1
            num1 = nextNum
            den1 = nextDen

            val approx = num1.toDouble() / den1.toDouble()
            if (abs(approx - value) < tolerance) break
        }

        return simplify(num1, den1)
    }

    private fun simplify(numerator: Long, denominator: Long): Pair<Long, Long> {
        if (denominator == 0L) return Pair(numerator, denominator)
        val g = gcd(abs(numerator), abs(denominator))
        return Pair(numerator / g, denominator / g)
    }

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    private fun stripTrailingZeros(str: String): String {
        if ('.' !in str) return str
        val stripped = str.trimEnd('0').trimEnd('.')
        return stripped.ifEmpty { "0" }
    }

    companion object {
        val engineeringSymbols = mapOf(
            15 to "P",
            12 to "T",
            9 to "G",
            6 to "M",
            3 to "k",
            -3 to "m",
            -6 to "μ",
            -9 to "n",
            -12 to "p",
            -15 to "f",
        )
    }
}
