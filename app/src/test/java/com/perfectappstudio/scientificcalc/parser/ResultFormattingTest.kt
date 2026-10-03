package com.perfectappstudio.scientificcalc.parser

import com.perfectappstudio.scientificcalc.core.model.DisplayMode
import com.perfectappstudio.scientificcalc.core.model.DisplaySettings
import com.perfectappstudio.scientificcalc.core.model.FractionFormat
import com.perfectappstudio.scientificcalc.core.parser.Formatter
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class ResultFormattingTest {
    private val formatter = Formatter()

    @Test
    fun fractionPreferenceSupportsBothFormatsAndSigns() {
        val mixed = DisplaySettings(showFractions = true)
        val improper = mixed.copy(fractionFormat = FractionFormat.IMPROPER)
        assertEquals("3 1/2", formatter.formatWithSettings(3.5, mixed))
        assertEquals("7/2", formatter.formatWithSettings(3.5, improper))
        assertEquals("-3 1/2", formatter.formatWithSettings(-3.5, mixed))
        assertEquals("-7/2", formatter.formatWithSettings(-3.5, improper))
        assertEquals("1/3", formatter.formatWithSettings(1.0 / 3, mixed))
        assertEquals("3.5", formatter.formatWithSettings(3.5, mixed.copy(showFractions = false)))
    }

    @Test
    fun fractionsNeverTurnTinyOrHugeValuesIntoZeroOrOverflow() {
        val settings = DisplaySettings(showFractions = true)
        assertEquals("1 × 10^-8", formatter.formatWithSettings(1e-8, settings))
        assertEquals("4.9 × 10^-324", formatter.formatWithSettings(Double.MIN_VALUE, settings))
        assertEquals("1.797693135 × 10^308", formatter.formatWithSettings(Double.MAX_VALUE, settings))
        assertEquals("0", formatter.formatWithSettings(0.0, settings))
    }

    @Test
    fun irrationalFractionApproximationIsClearlyMarked() {
        val result = formatter.formatWithSettings(Math.PI, DisplaySettings(showFractions = true, fractionFormat = FractionFormat.IMPROPER))
        assertTrue(result.startsWith("≈"))
        val parts = result.removePrefix("≈").split('/')
        assertEquals(Math.PI, parts[0].toDouble() / parts[1].toDouble(), 1e-10)
    }

    @Test
    fun normTwoKeepsSmallValuesInDecimalNotation() {
        assertEquals("0.00000001", formatter.formatWithSettings(1e-8, DisplaySettings(mode = DisplayMode.NORM_2)))
        assertEquals("1 × 10^-8", formatter.formatWithSettings(1e-8, DisplaySettings(mode = DisplayMode.NORM_1)))
    }

    @Test
    fun scientificRoundingCarriesIntoExponent() {
        val settings = DisplaySettings(mode = DisplayMode.SCI, digits = 2)
        assertEquals("1.0 × 10^+01", formatter.formatWithSettings(9.99, settings))
        assertEquals("-1.0 × 10^-01", formatter.formatWithSettings(-0.0999, settings))
        assertEquals("4.9 × 10^-324", formatter.formatWithSettings(Double.MIN_VALUE, settings))
    }

    @Test
    fun formattingAndDisplayRoundingHandleExtremeFiniteValues() {
        assertEquals("4.9 × 10^-324", formatter.format(Double.MIN_VALUE))
        assertEquals("1.797693135 × 10^308", formatter.format(Double.MAX_VALUE))
        assertEquals("4.9 × 10^-324", formatter.formatEngineering(Double.MIN_VALUE))
        assertEquals(1e-320, formatter.roundToDisplay(1e-320, DisplaySettings(mode = DisplayMode.SCI, digits = 2)), 0.0)
        assertEquals("1 × 10^+06", formatter.formatEngineering(999999.99996))
    }

    @Test
    fun deviceLocaleDoesNotAlterExpressionNotation() {
        val previous = Locale.getDefault()
        try {
            for (locale in listOf(Locale.FRANCE, Locale.GERMANY, Locale.forLanguageTag("ar"))) {
                Locale.setDefault(locale)
                assertEquals("0.5", formatter.format(0.5))
                assertEquals("1.500", formatter.formatWithSettings(1.5, DisplaySettings(mode = DisplayMode.FIX, digits = 3)))
                assertEquals("1.5 × 10^+03", formatter.formatWithSettings(1500.0, DisplaySettings(mode = DisplayMode.SCI, digits = 2)))
                assertEquals("4.7k", formatter.formatEngineeringSymbol(4700.0))
                assertEquals("1/2", formatter.formatWithSettings(0.5, DisplaySettings(showFractions = true)))
            }
        } finally { Locale.setDefault(previous) }
    }
}
