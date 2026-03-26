package com.calcmate.scientificcalculator.core.parser

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
}
