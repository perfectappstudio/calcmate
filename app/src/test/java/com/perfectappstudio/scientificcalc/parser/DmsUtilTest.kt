package com.perfectappstudio.scientificcalc.parser

import com.perfectappstudio.scientificcalc.core.parser.DmsUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class DmsUtilTest {

    private val DELTA = 1e-6

    // ---------------------------------------------------------------
    // decimalToDms
    // ---------------------------------------------------------------

    @Test
    fun `decimalToDms 2_258 returns 2 degrees 15 minutes 28_8 seconds`() {
        val (d, m, s) = DmsUtil.decimalToDms(2.258)
        assertEquals(2, d)
        assertEquals(15, m)
        assertEquals(28.8, s, DELTA)
    }

    @Test
    fun `decimalToDms zero returns 0 0 0`() {
        val (d, m, s) = DmsUtil.decimalToDms(0.0)
        assertEquals(0, d)
        assertEquals(0, m)
        assertEquals(0.0, s, DELTA)
    }

    @Test
    fun `decimalToDms negative value`() {
        val (d, m, s) = DmsUtil.decimalToDms(-2.258)
        assertEquals(-2, d)
        assertEquals(15, m)
        assertEquals(28.8, s, DELTA)
    }

    @Test
    fun `decimalToDms whole degree`() {
        val (d, m, s) = DmsUtil.decimalToDms(45.0)
        assertEquals(45, d)
        assertEquals(0, m)
        assertEquals(0.0, s, DELTA)
    }

    // ---------------------------------------------------------------
    // dmsToDecimal
    // ---------------------------------------------------------------

    @Test
    fun `dmsToDecimal 2 15 28_8 returns 2_258`() {
        val result = DmsUtil.dmsToDecimal(2, 15, 28.8)
        assertEquals(2.258, result, DELTA)
    }

    @Test
    fun `dmsToDecimal 0 0 0 returns 0`() {
        val result = DmsUtil.dmsToDecimal(0, 0, 0.0)
        assertEquals(0.0, result, DELTA)
    }

    @Test
    fun `dmsToDecimal negative degrees`() {
        val result = DmsUtil.dmsToDecimal(-2, 15, 28.8)
        assertEquals(-2.258, result, DELTA)
    }

    @Test
    fun `roundtrip decimalToDms then dmsToDecimal`() {
        val original = 123.456789
        val (d, m, s) = DmsUtil.decimalToDms(original)
        val result = DmsUtil.dmsToDecimal(d, m, s)
        assertEquals(original, result, DELTA)
    }

    // ---------------------------------------------------------------
    // formatDms
    // ---------------------------------------------------------------

    @Test
    fun `formatDms 12 34 56_0`() {
        val result = DmsUtil.formatDms(12, 34, 56.0)
        assertEquals("12°34'56\"", result)
    }

    @Test
    fun `formatDms with fractional seconds`() {
        val result = DmsUtil.formatDms(2, 15, 28.8)
        assertEquals("2°15'28.8\"", result)
    }

    @Test
    fun `formatDms zero`() {
        val result = DmsUtil.formatDms(0, 0, 0.0)
        assertEquals("0°0'0\"", result)
    }

    // ---------------------------------------------------------------
    // addDms
    // ---------------------------------------------------------------

    @Test
    fun `addDms 2d20m30s plus 0d39m30s equals 3d0m0s`() {
        val dms1 = Triple(2, 20, 30.0)
        val dms2 = Triple(0, 39, 30.0)
        val (d, m, s) = DmsUtil.addDms(dms1, dms2)
        assertEquals(3, d)
        assertEquals(0, m)
        assertEquals(0.0, s, DELTA)
    }

    // ---------------------------------------------------------------
    // subtractDms
    // ---------------------------------------------------------------

    @Test
    fun `subtractDms 3d0m0s minus 0d39m30s equals 2d20m30s`() {
        val dms1 = Triple(3, 0, 0.0)
        val dms2 = Triple(0, 39, 30.0)
        val (d, m, s) = DmsUtil.subtractDms(dms1, dms2)
        assertEquals(2, d)
        assertEquals(20, m)
        assertEquals(30.0, s, DELTA)
    }

    // ---------------------------------------------------------------
    // multiplyDmsByScalar
    // ---------------------------------------------------------------

    @Test
    fun `multiplyDmsByScalar 12d34m56s times 3_45`() {
        val dms = Triple(12, 34, 56.0)
        val (d, m, s) = DmsUtil.multiplyDmsByScalar(dms, 3.45)
        // 12 + 34/60 + 56/3600 = 12.58222... * 3.45 = 43.40888...
        // = 43d 24m 31.2s (approximately)
        assertEquals(43, d)
        assertEquals(24, m)
        assertEquals(31.2, s, 0.1)
    }

    // ---------------------------------------------------------------
    // Edge cases
    // ---------------------------------------------------------------

    @Test
    fun `seconds carry over 60 is handled via decimal conversion`() {
        // Adding two DMS values whose seconds sum > 60
        // 0d0m45s + 0d0m30s = 0d1m15s
        val dms1 = Triple(0, 0, 45.0)
        val dms2 = Triple(0, 0, 30.0)
        val (d, m, s) = DmsUtil.addDms(dms1, dms2)
        assertEquals(0, d)
        assertEquals(1, m)
        assertEquals(15.0, s, DELTA)
    }
}
