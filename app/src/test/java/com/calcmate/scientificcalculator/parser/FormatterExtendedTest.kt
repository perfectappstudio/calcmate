package com.calcmate.scientificcalculator.parser

import com.calcmate.scientificcalculator.core.model.DisplayMode
import com.calcmate.scientificcalculator.core.model.DisplaySettings
import com.calcmate.scientificcalculator.core.parser.DmsUtil
import com.calcmate.scientificcalculator.core.parser.Formatter
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatterExtendedTest {

    private val formatter = Formatter()

    // ---------------------------------------------------------------
    // Fix mode
    // ---------------------------------------------------------------

    @Test
    fun `Fix 3 - 200 div 7 times 14 = 400_000`() {
        val settings = DisplaySettings(mode = DisplayMode.FIX, digits = 3)
        // 200/7*14 = 400.0
        val value = (200.0 / 7.0) * 14.0
        assertEquals("400.000", formatter.formatWithSettings(value, settings))
    }

    @Test
    fun `Fix 0 - rounds to integer`() {
        val settings = DisplaySettings(mode = DisplayMode.FIX, digits = 0)
        assertEquals("3", formatter.formatWithSettings(3.14159, settings))
    }

    @Test
    fun `Fix 5 - pi`() {
        val settings = DisplaySettings(mode = DisplayMode.FIX, digits = 5)
        assertEquals("3.14159", formatter.formatWithSettings(Math.PI, settings))
    }

    // ---------------------------------------------------------------
    // Sci mode
    // ---------------------------------------------------------------

    @Test
    fun `Sci 2 - 1 div 3 = 3_3 times 10 pow -01`() {
        val settings = DisplaySettings(mode = DisplayMode.SCI, digits = 2)
        val value = 1.0 / 3.0
        assertEquals("3.3 \u00D7 10^-01", formatter.formatWithSettings(value, settings))
    }

    @Test
    fun `Sci 4 - 12345`() {
        val settings = DisplaySettings(mode = DisplayMode.SCI, digits = 4)
        assertEquals("1.235 \u00D7 10^+04", formatter.formatWithSettings(12345.0, settings))
    }

    // ---------------------------------------------------------------
    // Norm 1 - scientific for |x| < 10^-2 or |x| >= 10^10
    // ---------------------------------------------------------------

    @Test
    fun `Norm 1 - 1 div 200 uses scientific`() {
        val settings = DisplaySettings(mode = DisplayMode.NORM_1)
        val value = 1.0 / 200.0 // 0.005, which is < 0.01
        assertEquals("5 \u00D7 10^-3", formatter.formatWithSettings(value, settings))
    }

    @Test
    fun `Norm 1 - 123 stays decimal`() {
        val settings = DisplaySettings(mode = DisplayMode.NORM_1)
        assertEquals("123", formatter.formatWithSettings(123.0, settings))
    }

    // ---------------------------------------------------------------
    // Norm 2 - scientific for |x| < 10^-9 or |x| >= 10^10
    // ---------------------------------------------------------------

    @Test
    fun `Norm 2 - 1 div 200 stays decimal`() {
        val settings = DisplaySettings(mode = DisplayMode.NORM_2)
        val value = 1.0 / 200.0 // 0.005, which is >= 1e-9
        assertEquals("0.005", formatter.formatWithSettings(value, settings))
    }

    @Test
    fun `Norm 2 - very small number uses scientific`() {
        val settings = DisplaySettings(mode = DisplayMode.NORM_2)
        assertEquals("1 \u00D7 10^-10", formatter.formatWithSettings(1e-10, settings))
    }

    // ---------------------------------------------------------------
    // Engineering notation
    // ---------------------------------------------------------------

    @Test
    fun `engineering - 56088 becomes 56_088 times 10 pow 03`() {
        assertEquals("56.088 \u00D7 10^+03", formatter.formatEngineering(56088.0))
    }

    @Test
    fun `engineering - 0_001234 becomes 1_234 times 10 pow -03`() {
        assertEquals("1.234 \u00D7 10^-03", formatter.formatEngineering(0.001234))
    }

    // ---------------------------------------------------------------
    // Engineering symbols
    // ---------------------------------------------------------------

    @Test
    fun `engineering symbol - 0_009 = 9m`() {
        assertEquals("9m", formatter.formatEngineeringSymbol(0.009))
    }

    @Test
    fun `engineering symbol - 4700 = 4_7k`() {
        assertEquals("4.7k", formatter.formatEngineeringSymbol(4700.0))
    }

    @Test
    fun `engineering symbol - 2_5e6 = 2_5M`() {
        assertEquals("2.5M", formatter.formatEngineeringSymbol(2.5e6))
    }

    // ---------------------------------------------------------------
    // Rnd (round to display)
    // ---------------------------------------------------------------

    @Test
    fun `Rnd with Fix 3 - 10 div 3 = 3_333 then times 3 = 9_999`() {
        val settings = DisplaySettings(mode = DisplayMode.FIX, digits = 3)
        val value = 10.0 / 3.0
        val rounded = formatter.roundToDisplay(value, settings)
        assertEquals(3.333, rounded, 1e-15)
        assertEquals(9.999, rounded * 3, 1e-12)
    }

    @Test
    fun `Rnd with Sci 2`() {
        val settings = DisplaySettings(mode = DisplayMode.SCI, digits = 2)
        val value = 1.0 / 3.0 // 0.33333...
        val rounded = formatter.roundToDisplay(value, settings)
        assertEquals(0.33, rounded, 1e-15)
    }

    // ---------------------------------------------------------------
    // DMS conversions
    // ---------------------------------------------------------------

    @Test
    fun `decimal 2_258 to DMS = 2 deg 15 min 28_8 sec`() {
        val (d, m, s) = DmsUtil.decimalToDms(2.258)
        assertEquals(2, d)
        assertEquals(15, m)
        assertEquals(28.8, s, 0.1)
        assertEquals("2\u00B015'28.8\"", DmsUtil.formatDms(d, m, s))
    }

    @Test
    fun `DMS addition - 2 deg 20 min 30 sec + 0 deg 39 min 30 sec = 3 deg 0 min 0 sec`() {
        val dms1 = Triple(2, 20, 30.0)
        val dms2 = Triple(0, 39, 30.0)
        val result = DmsUtil.addDms(dms1, dms2)
        assertEquals(3, result.first)
        assertEquals(0, result.second)
        assertEquals(0.0, result.third, 0.1)
    }

    @Test
    fun `DMS to decimal round-trip`() {
        val original = 45.5075 // 45°30'27"
        val (d, m, s) = DmsUtil.decimalToDms(original)
        val back = DmsUtil.dmsToDecimal(d, m, s)
        assertEquals(original, back, 1e-10)
    }

    @Test
    fun `DMS subtraction`() {
        val dms1 = Triple(5, 0, 0.0)
        val dms2 = Triple(2, 30, 0.0)
        val result = DmsUtil.subtractDms(dms1, dms2)
        assertEquals(2, result.first)
        assertEquals(30, result.second)
        assertEquals(0.0, result.third, 0.1)
    }

    @Test
    fun `DMS multiply by scalar`() {
        val dms = Triple(1, 30, 0.0) // 1.5 degrees
        val result = DmsUtil.multiplyDmsByScalar(dms, 2.0)
        assertEquals(3, result.first)
        assertEquals(0, result.second)
        assertEquals(0.0, result.third, 0.1)
    }
}
