package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.NewtonResult
import com.perfectappstudio.scientificcalc.core.math.NewtonSolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class NewtonSolverTest {

    private val delta = 1e-6

    @Test
    fun newton_sqrtTwo_positiveGuess() {
        // X^2 - 2 = 0, guess = 1 -> x ~ 1.414213562
        val result = NewtonSolver.solve("X^2 - 2", initialGuess = 1.0)
        assertTrue(result is NewtonResult.Solution)
        val r = result as NewtonResult.Solution
        assertEquals(1.414213562, r.x, delta)
    }

    @Test
    fun newton_sqrtTwo_negativeGuess() {
        // X^2 - 2 = 0, guess = -1 -> x ~ -1.414213562
        val result = NewtonSolver.solve("X^2 - 2", initialGuess = -1.0)
        assertTrue(result is NewtonResult.Solution)
        val r = result as NewtonResult.Solution
        assertEquals(-1.414213562, r.x, delta)
    }

    @Test
    fun newton_sinX_nearPi() {
        // sin(X) = 0, guess = 3 -> x ~ pi
        val result = NewtonSolver.solve("sin(X)", initialGuess = 3.0)
        assertTrue(result is NewtonResult.Solution)
        val r = result as NewtonResult.Solution
        assertEquals(PI, r.x, delta)
    }

    @Test
    fun newton_cubicRoot() {
        // X^3 - 8 = 0, guess = 1 -> x = 2
        val result = NewtonSolver.solve("X^3 - 8", initialGuess = 1.0)
        assertTrue(result is NewtonResult.Solution)
        val r = result as NewtonResult.Solution
        assertEquals(2.0, r.x, delta)
    }

    @Test
    fun newton_invalidExpression() {
        val result = NewtonSolver.solve("???", initialGuess = 1.0)
        assertTrue(result is NewtonResult.CannotSolve)
    }
}
