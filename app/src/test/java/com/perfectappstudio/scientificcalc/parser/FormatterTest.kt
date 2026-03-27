package com.perfectappstudio.scientificcalc.parser

import com.perfectappstudio.scientificcalc.core.parser.DisplayMode
import com.perfectappstudio.scientificcalc.core.parser.Formatter
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatterTest {

    // ---------------------------------------------------------------
    // Decimal formatting
    // ---------------------------------------------------------------

    @Test
    fun `decimal format of pi`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        assertEquals("3.141592654", formatter.format(Math.PI))
    }

    @Test
    fun `decimal format of integer-valued double`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        assertEquals("42", formatter.format(42.0))
    }

    @Test
    fun `decimal format of 0_1 + 0_2 is 0_3`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        // 0.1+0.2 == 0.30000000000000004 in IEEE 754
        val result = formatter.format(0.1 + 0.2)
        assertEquals("0.3", result)
    }

    @Test
    fun `decimal format of zero`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        assertEquals("0", formatter.format(0.0))
    }

    // ---------------------------------------------------------------
    // Scientific notation
    // ---------------------------------------------------------------

    @Test
    fun `scientific format of 123456789`() {
        val formatter = Formatter(DisplayMode.SCIENTIFIC)
        val result = formatter.format(123456789.0)
        // 1.23456789 x 10^8
        assertEquals("1.23456789 \u00D7 10^8", result)
    }

    @Test
    fun `scientific format of 1000`() {
        val formatter = Formatter(DisplayMode.SCIENTIFIC)
        val result = formatter.format(1000.0)
        assertEquals("1 \u00D7 10^3", result)
    }

    @Test
    fun `decimal auto-switches to scientific for large numbers`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        val result = formatter.format(1e15)
        // Should switch to scientific since >= 1e10
        assertEquals("1 \u00D7 10^15", result)
    }

    @Test
    fun `decimal auto-switches to scientific for very small numbers`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        val result = formatter.format(1e-8)
        // Should switch to scientific since < 1e-6
        assertEquals("1 \u00D7 10^-8", result)
    }

    // ---------------------------------------------------------------
    // Fraction formatting
    // ---------------------------------------------------------------

    @Test
    fun `fraction format of 0_5 is 1 div 2`() {
        val formatter = Formatter(DisplayMode.FRACTION)
        assertEquals("1/2", formatter.format(0.5))
    }

    @Test
    fun `fraction format of 0_333 repeating is 1 div 3`() {
        val formatter = Formatter(DisplayMode.FRACTION)
        assertEquals("1/3", formatter.format(1.0 / 3.0))
    }

    @Test
    fun `fraction format of 1_5 is 1 1 div 2`() {
        val formatter = Formatter(DisplayMode.FRACTION)
        assertEquals("1 1/2", formatter.format(1.5))
    }

    @Test
    fun `fraction format of integer`() {
        val formatter = Formatter(DisplayMode.FRACTION)
        assertEquals("7", formatter.format(7.0))
    }

    @Test
    fun `fraction format of zero`() {
        val formatter = Formatter(DisplayMode.FRACTION)
        assertEquals("0", formatter.format(0.0))
    }

    @Test
    fun `fraction format of 0_25 is 1 div 4`() {
        val formatter = Formatter(DisplayMode.FRACTION)
        assertEquals("1/4", formatter.format(0.25))
    }

    // ---------------------------------------------------------------
    // Special values
    // ---------------------------------------------------------------

    @Test
    fun `NaN formats as Error`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        assertEquals("Error", formatter.format(Double.NaN))
    }

    @Test
    fun `positive infinity formats as infinity symbol`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        assertEquals("\u221E", formatter.format(Double.POSITIVE_INFINITY))
    }

    @Test
    fun `negative infinity formats as negative infinity symbol`() {
        val formatter = Formatter(DisplayMode.DECIMAL)
        assertEquals("-\u221E", formatter.format(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun `NaN in scientific mode also returns Error`() {
        val formatter = Formatter(DisplayMode.SCIENTIFIC)
        assertEquals("Error", formatter.format(Double.NaN))
    }

    @Test
    fun `NaN in fraction mode also returns Error`() {
        val formatter = Formatter(DisplayMode.FRACTION)
        assertEquals("Error", formatter.format(Double.NaN))
    }
}
