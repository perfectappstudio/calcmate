package com.calcmate.scientificcalculator.math

import com.calcmate.scientificcalculator.core.math.CubicResult
import com.calcmate.scientificcalculator.core.math.EquationSolver
import com.calcmate.scientificcalculator.core.math.QuadraticResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CubicSolverTest {

    private val delta = 1e-6

    @Test
    fun cubic_threeDistinctRealRoots_case1() {
        // x^3 - 2x^2 - x + 2 = 0 -> roots: 2, 1, -1
        val result = EquationSolver.solveCubic(1.0, -2.0, -1.0, 2.0)
        assertTrue(result is CubicResult.ThreeRealRoots)
        val r = result as CubicResult.ThreeRealRoots
        // Roots are sorted descending
        assertEquals(2.0, r.x1, delta)
        assertEquals(1.0, r.x2, delta)
        assertEquals(-1.0, r.x3, delta)
        assertTrue(r.steps.isNotEmpty())
    }

    @Test
    fun cubic_threeDistinctRealRoots_case2() {
        // x^3 - 6x^2 + 11x - 6 = 0 -> roots: 3, 2, 1
        val result = EquationSolver.solveCubic(1.0, -6.0, 11.0, -6.0)
        assertTrue(result is CubicResult.ThreeRealRoots)
        val r = result as CubicResult.ThreeRealRoots
        assertEquals(3.0, r.x1, delta)
        assertEquals(2.0, r.x2, delta)
        assertEquals(1.0, r.x3, delta)
    }

    @Test
    fun cubic_oneRealTwoComplex() {
        // x^3 + 1 = 0 -> x = -1, and complex pair 0.5 +/- 0.866i
        val result = EquationSolver.solveCubic(1.0, 0.0, 0.0, 1.0)
        assertTrue(result is CubicResult.OneRealTwoComplex)
        val r = result as CubicResult.OneRealTwoComplex
        assertEquals(-1.0, r.x1, delta)
        assertEquals(0.5, r.realPart, delta)
        assertEquals(0.866025, r.imagPart, delta)
    }

    @Test
    fun cubic_degenerateToQuadratic() {
        // a=0: 8x^2 - 4x + 5 = 0 (complex roots)
        val result = EquationSolver.solveCubic(0.0, 8.0, -4.0, 5.0)
        assertTrue(result is CubicResult.DegenerateQuadratic)
        val qr = (result as CubicResult.DegenerateQuadratic).quadraticResult
        assertTrue(qr is QuadraticResult.ComplexRoots)
    }

    @Test
    fun cubic_tripleRoot() {
        // (x-1)^3 = x^3 - 3x^2 + 3x - 1 = 0 -> triple root x=1
        val result = EquationSolver.solveCubic(1.0, -3.0, 3.0, -1.0)
        assertTrue(result is CubicResult.ThreeRealRoots)
        val r = result as CubicResult.ThreeRealRoots
        assertEquals(1.0, r.x1, delta)
        assertEquals(1.0, r.x2, delta)
        assertEquals(1.0, r.x3, delta)
    }

    @Test
    fun cubic_repeatedRootAndDistinct() {
        // (x-1)^2 * (x+2) = x^3 - 3x + 2 = 0 -> x=1 (double), x=-2
        // expanded: x^3 + 0x^2 - 3x + 2
        val result = EquationSolver.solveCubic(1.0, 0.0, -3.0, 2.0)
        assertTrue(result is CubicResult.ThreeRealRoots)
        val r = result as CubicResult.ThreeRealRoots
        // sorted descending: 1, 1, -2
        assertEquals(1.0, r.x1, delta)
        assertEquals(1.0, r.x2, delta)
        assertEquals(-2.0, r.x3, delta)
    }
}
