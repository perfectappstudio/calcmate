package com.perfectappstudio.scientificcalc.core.parser

import com.perfectappstudio.scientificcalc.core.model.DisplayMode as SettingsDisplayMode
import com.perfectappstudio.scientificcalc.core.model.DisplaySettings
import com.perfectappstudio.scientificcalc.core.model.FractionFormat
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

enum class DisplayMode { DECIMAL, SCIENTIFIC, FRACTION }

class Formatter(private val mode: DisplayMode = DisplayMode.DECIMAL) {
    fun format(result: CalcResult): String = result.toDisplayString(this)

    fun format(value: Double): String {
        if (!value.isFinite()) return specialValue(value)
        return when (mode) {
            DisplayMode.DECIMAL -> formatDecimal(value)
            DisplayMode.SCIENTIFIC -> formatScientific(value)
            DisplayMode.FRACTION -> formatFraction(value, FractionFormat.MIXED)
        }
    }

    fun formatWithSettings(value: Double, settings: DisplaySettings): String {
        if (!value.isFinite()) return specialValue(value)
        if (settings.showFractions) return formatFraction(value, settings.fractionFormat)
        if (settings.engineeringOn) return formatEngineering(value)
        return when (settings.mode) {
            SettingsDisplayMode.FIX -> BigDecimal.valueOf(value)
                .setScale(settings.digits.coerceIn(0, 9), RoundingMode.HALF_UP).toPlainString()
            SettingsDisplayMode.SCI -> {
                if (value == 0.0) return "0 × 10^00"
                val digits = settings.digits.coerceIn(1, 10)
                val (mantissa, exponent) = scientificParts(value, digits)
                val text = mantissa.setScale(digits - 1, RoundingMode.HALF_UP).toPlainString()
                "$text × 10^${String.format(Locale.ROOT, "%+03d", exponent)}"
            }
            SettingsDisplayMode.NORM_1 -> formatNorm(value, 1e-2)
            SettingsDisplayMode.NORM_2 -> formatNorm(value, 1e-9)
        }
    }

    fun formatEngineering(value: Double): String {
        if (!value.isFinite()) return specialValue(value)
        if (value == 0.0) return "0 × 10^00"
        val rounded = BigDecimal.valueOf(value).round(MathContext(10, RoundingMode.HALF_UP))
        val exponent = Math.floorDiv(rounded.precision() - rounded.scale() - 1, 3) * 3
        val text = rounded.movePointLeft(exponent).stripTrailingZeros().toPlainString()
        return "$text × 10^${String.format(Locale.ROOT, "%+03d", exponent)}"
    }

    fun formatEngineeringSymbol(value: Double): String {
        if (!value.isFinite()) return specialValue(value)
        if (value == 0.0) return "0"
        val rounded = BigDecimal.valueOf(value).round(MathContext(10, RoundingMode.HALF_UP))
        val exponent = Math.floorDiv(rounded.precision() - rounded.scale() - 1, 3) * 3
        val symbol = engineeringSymbols[exponent] ?: return formatEngineering(value)
        return rounded.movePointLeft(exponent).stripTrailingZeros().toPlainString() + symbol
    }

    fun roundToDisplay(value: Double, settings: DisplaySettings): Double {
        if (!value.isFinite() || value == 0.0) return value
        val decimal = BigDecimal.valueOf(value)
        return when (settings.mode) {
            SettingsDisplayMode.FIX -> decimal.setScale(settings.digits.coerceIn(0, 9), RoundingMode.HALF_UP)
            SettingsDisplayMode.SCI -> decimal.round(MathContext(settings.digits.coerceIn(1, 10), RoundingMode.HALF_UP))
            SettingsDisplayMode.NORM_1, SettingsDisplayMode.NORM_2 -> decimal.round(MathContext(10, RoundingMode.HALF_UP))
        }.toDouble()
    }

    private fun formatNorm(value: Double, smallThreshold: Double): String {
        if (value == 0.0) return "0"
        return if (abs(value) < smallThreshold || abs(value) >= 1e10) {
            formatScientific(value)
        } else {
            decimalText(value)
        }
    }

    private fun formatDecimal(value: Double): String = formatNorm(value, 1e-6)

    private fun decimalText(value: Double): String = BigDecimal.valueOf(value)
        .round(MathContext(10, RoundingMode.HALF_UP)).stripTrailingZeros().toPlainString()

    private fun scientificParts(value: Double, digits: Int): Pair<BigDecimal, Int> {
        val rounded = BigDecimal.valueOf(value).round(MathContext(digits, RoundingMode.HALF_UP))
        val exponent = rounded.precision() - rounded.scale() - 1
        return rounded.movePointLeft(exponent) to exponent
    }

    private fun formatScientific(value: Double): String {
        if (value == 0.0) return "0 × 10^0"
        val (mantissa, exponent) = scientificParts(value, 10)
        return "${mantissa.stripTrailingZeros().toPlainString()} × 10^$exponent"
    }

    private fun formatFraction(value: Double, fractionFormat: FractionFormat): String {
        if (value == 0.0) return "0"
        if (abs(value) >= 1e10) return formatDecimal(value)
        val decimal = BigDecimal.valueOf(abs(value)).stripTrailingZeros()
        if (decimal.scale() <= 0) return decimalText(value)
        var numerator = decimal.unscaledValue()
        var denominator = BigInteger.TEN.pow(decimal.scale())
        var previousNumerator = BigInteger.ZERO
        var currentNumerator = BigInteger.ONE
        var previousDenominator = BigInteger.ONE
        var currentDenominator = BigInteger.ZERO
        val limit = BigInteger.valueOf(1_000_000)
        while (denominator != BigInteger.ZERO) {
            val (quotient, remainder) = numerator.divideAndRemainder(denominator)
            val nextNumerator = quotient * currentNumerator + previousNumerator
            val nextDenominator = quotient * currentDenominator + previousDenominator
            if (nextDenominator > limit) break
            previousNumerator = currentNumerator
            currentNumerator = nextNumerator
            previousDenominator = currentDenominator
            currentDenominator = nextDenominator
            numerator = denominator
            denominator = remainder
        }
        if (currentNumerator == BigInteger.ZERO || currentDenominator == BigInteger.ZERO) return formatDecimal(value)
        val approximation = currentNumerator.toDouble() / currentDenominator.toDouble()
        if (abs(approximation - abs(value)) > abs(value) * 1e-10) return formatDecimal(value)
        val prefix = (if (approximation != abs(value)) "≈" else "") + (if (value < 0) "-" else "")
        val (whole, remainder) = currentNumerator.divideAndRemainder(currentDenominator)
        return when {
            remainder == BigInteger.ZERO -> prefix + whole
            fractionFormat == FractionFormat.IMPROPER || whole == BigInteger.ZERO ->
                "$prefix$currentNumerator/$currentDenominator"
            else -> "$prefix$whole $remainder/$currentDenominator"
        }
    }

    private fun specialValue(value: Double): String = when {
        value.isNaN() -> "Error"
        value > 0 -> "∞"
        else -> "-∞"
    }

    companion object {
        val engineeringSymbols = mapOf(
            15 to "P", 12 to "T", 9 to "G", 6 to "M", 3 to "k",
            -3 to "m", -6 to "μ", -9 to "n", -12 to "p", -15 to "f",
        )
    }
}
