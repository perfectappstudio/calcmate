package com.calcmate.scientificcalculator.math

import com.calcmate.scientificcalculator.core.math.ComplexMath
import com.calcmate.scientificcalculator.core.model.AngleUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class ComplexMathTest {

    private val DELTA = 1e-9

    @Test
    fun `(2+6i) divided by (2i) equals (3-1i)`() {
        // (2+6i) / (0+2i) = (2*0+6*2)/(0+4), (6*0-2*2)/(0+4) = 12/4, -4/4 = 3, -1
        val (r, i) = ComplexMath.divide(2.0, 6.0, 0.0, 2.0)
        assertEquals(3.0, r, DELTA)
        assertEquals(-1.0, i, DELTA)
    }

    @Test
    fun `conjugate of (2+3i) is (2-3i)`() {
        val (r, i) = ComplexMath.conjugate(2.0, 3.0)
        assertEquals(2.0, r, DELTA)
        assertEquals(-3.0, i, DELTA)
    }

    @Test
    fun `abs of (3+4i) is 5`() {
        assertEquals(5.0, ComplexMath.abs(3.0, 4.0), DELTA)
    }

    @Test
    fun `arg of (1+1i) is 45 degrees`() {
        assertEquals(45.0, ComplexMath.arg(1.0, 1.0, AngleUnit.DEGREE), DELTA)
    }

    @Test
    fun `add (1+2i) and (3+4i) equals (4+6i)`() {
        val (r, i) = ComplexMath.add(1.0, 2.0, 3.0, 4.0)
        assertEquals(4.0, r, DELTA)
        assertEquals(6.0, i, DELTA)
    }

    @Test
    fun `subtract (5+3i) - (2+1i) equals (3+2i)`() {
        val (r, i) = ComplexMath.subtract(5.0, 3.0, 2.0, 1.0)
        assertEquals(3.0, r, DELTA)
        assertEquals(2.0, i, DELTA)
    }

    @Test
    fun `multiply (1+2i) and (3+4i) equals (-5+10i)`() {
        val (r, i) = ComplexMath.multiply(1.0, 2.0, 3.0, 4.0)
        assertEquals(-5.0, r, DELTA)
        assertEquals(10.0, i, DELTA)
    }

    @Test
    fun `polarToRect converts correctly`() {
        val (x, y) = ComplexMath.polarToRect(2.0, 60.0, AngleUnit.DEGREE)
        assertEquals(1.0, x, 1e-6)
        assertEquals(1.732050808, y, 1e-6)
    }

    @Test
    fun `rectToPolar converts correctly`() {
        val (r, theta) = ComplexMath.rectToPolar(1.0, 1.0, AngleUnit.DEGREE)
        assertEquals(1.41421356, r, 1e-6)
        assertEquals(45.0, theta, 1e-6)
    }
}
