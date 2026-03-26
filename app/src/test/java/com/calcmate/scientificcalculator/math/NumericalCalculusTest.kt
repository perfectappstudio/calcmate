package com.calcmate.scientificcalculator.math

import com.calcmate.scientificcalculator.core.math.NumericalCalculus
import com.calcmate.scientificcalculator.core.model.AngleUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class NumericalCalculusTest {

    @Test
    fun `integrate 2X^2+3X+8 from 1 to 5 is approximately 150_6667`() {
        // Exact: [2x^3/3 + 3x^2/2 + 8x] from 1 to 5
        // = (250/3 + 75/2 + 40) - (2/3 + 3/2 + 8)
        // = 83.333 + 37.5 + 40 - 0.667 - 1.5 - 8 = 150.6667
        val result = NumericalCalculus.integrate(
            expression = "2*X^2+3*X+8",
            a = 1.0,
            b = 5.0,
            n = 64,
            angleUnit = AngleUnit.RADIAN
        )
        assertEquals(150.6667, result, 0.001)
    }

    @Test
    fun `differentiate 3X^2-5X+2 at x=2 is approximately 7`() {
        // d/dx(3x^2-5x+2) = 6x - 5, at x=2 => 12-5 = 7
        val result = NumericalCalculus.differentiate(
            expression = "3*X^2-5*X+2",
            a = 2.0,
            angleUnit = AngleUnit.RADIAN
        )
        assertEquals(7.0, result, 0.001)
    }

    @Test
    fun `integrate sin(X) from 0 to pi is approximately 2`() {
        val result = NumericalCalculus.integrate(
            expression = "sin(X)",
            a = 0.0,
            b = Math.PI,
            n = 64,
            angleUnit = AngleUnit.RADIAN
        )
        assertEquals(2.0, result, 0.001)
    }

    @Test
    fun `differentiate sin(X) at 0 is approximately 1`() {
        val result = NumericalCalculus.differentiate(
            expression = "sin(X)",
            a = 0.0,
            angleUnit = AngleUnit.RADIAN
        )
        assertEquals(1.0, result, 0.001)
    }
}
