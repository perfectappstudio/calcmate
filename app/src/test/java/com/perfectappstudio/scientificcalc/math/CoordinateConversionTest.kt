package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.CoordinateConversion
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CoordinateConversionTest {

    @Before
    fun setup() {
        MemoryManager.clearAll()
    }

    @Test
    fun `pol(1, sqrt3) with RAD gives r approx 2 and theta approx 1_047`() {
        val (r, theta) = CoordinateConversion.pol(1.0, Math.sqrt(3.0), AngleUnit.RADIAN)
        assertEquals(2.0, r, 0.001)
        assertEquals(1.047, theta, 0.001)
    }

    @Test
    fun `pol stores r in E and theta in F`() {
        CoordinateConversion.pol(1.0, Math.sqrt(3.0), AngleUnit.RADIAN)
        assertEquals(2.0, MemoryManager.recallVariable('E').toDouble(), 0.001)
        assertEquals(1.047, MemoryManager.recallVariable('F').toDouble(), 0.001)
    }

    @Test
    fun `rec(2, 60 deg) gives x approx 1 and y approx 1_732`() {
        val (x, y) = CoordinateConversion.rec(2.0, 60.0, AngleUnit.DEGREE)
        assertEquals(1.0, x, 0.001)
        assertEquals(1.732, y, 0.001)
    }

    @Test
    fun `rec stores x in E and y in F`() {
        CoordinateConversion.rec(2.0, 60.0, AngleUnit.DEGREE)
        assertEquals(1.0, MemoryManager.recallVariable('E').toDouble(), 0.001)
        assertEquals(1.732, MemoryManager.recallVariable('F').toDouble(), 0.001)
    }

    @Test
    fun `pol with degree mode`() {
        val (r, theta) = CoordinateConversion.pol(3.0, 4.0, AngleUnit.DEGREE)
        assertEquals(5.0, r, 0.001)
        assertEquals(53.130, theta, 0.001)
    }
}
