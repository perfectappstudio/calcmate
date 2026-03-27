package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.AngleConversion
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

class AngleConversionTest {

    private val DELTA = 0.001

    @Test
    fun `90 degrees to radians is pi over 2`() {
        assertEquals(PI / 2, AngleConversion.convert(90.0, AngleUnit.DEGREE, AngleUnit.RADIAN), DELTA)
    }

    @Test
    fun `90 degrees to gradians is 100`() {
        assertEquals(100.0, AngleConversion.convert(90.0, AngleUnit.DEGREE, AngleUnit.GRADIAN), DELTA)
    }

    @Test
    fun `pi over 2 radians to degrees is 90`() {
        assertEquals(90.0, AngleConversion.convert(PI / 2, AngleUnit.RADIAN, AngleUnit.DEGREE), DELTA)
    }

    @Test
    fun `pi over 2 radians to gradians is 100`() {
        assertEquals(100.0, AngleConversion.convert(PI / 2, AngleUnit.RADIAN, AngleUnit.GRADIAN), DELTA)
    }

    @Test
    fun `4_25 radians to degrees is 243_507`() {
        assertEquals(243.507, AngleConversion.convert(4.25, AngleUnit.RADIAN, AngleUnit.DEGREE), DELTA)
    }

    @Test
    fun `100 gradians to degrees is 90`() {
        assertEquals(90.0, AngleConversion.convert(100.0, AngleUnit.GRADIAN, AngleUnit.DEGREE), DELTA)
    }

    @Test
    fun `100 gradians to radians is pi over 2`() {
        assertEquals(PI / 2, AngleConversion.convert(100.0, AngleUnit.GRADIAN, AngleUnit.RADIAN), DELTA)
    }

    @Test
    fun `same unit returns same value`() {
        assertEquals(42.0, AngleConversion.convert(42.0, AngleUnit.DEGREE, AngleUnit.DEGREE), DELTA)
        assertEquals(1.5, AngleConversion.convert(1.5, AngleUnit.RADIAN, AngleUnit.RADIAN), DELTA)
        assertEquals(75.0, AngleConversion.convert(75.0, AngleUnit.GRADIAN, AngleUnit.GRADIAN), DELTA)
    }
}
