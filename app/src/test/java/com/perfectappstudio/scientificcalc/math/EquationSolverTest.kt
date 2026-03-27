package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.EquationSolver
import com.perfectappstudio.scientificcalc.core.math.LinearResult
import com.perfectappstudio.scientificcalc.core.math.QuadraticResult
import com.perfectappstudio.scientificcalc.core.math.SystemResult
import com.perfectappstudio.scientificcalc.core.math.SystemResult3x3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EquationSolverTest {

    private val delta = 1e-6

    // ---------------------------------------------------------------
    // Linear: ax + b = 0
    // ---------------------------------------------------------------

    @Test
    fun linear_normalCase() {
        // 2x + 6 = 0  =>  x = -3
        val result = EquationSolver.solveLinear(2.0, 6.0)
        assertTrue(result is LinearResult.Solution)
        assertEquals(-3.0, (result as LinearResult.Solution).x, delta)
    }

    @Test
    fun linear_noSolution() {
        // 0x + 5 = 0  =>  no solution
        val result = EquationSolver.solveLinear(0.0, 5.0)
        assertTrue(result is LinearResult.NoSolution)
    }

    @Test
    fun linear_infiniteSolutions() {
        // 0x + 0 = 0  =>  infinite solutions
        val result = EquationSolver.solveLinear(0.0, 0.0)
        assertTrue(result is LinearResult.InfiniteSolutions)
    }

    // ---------------------------------------------------------------
    // Quadratic: ax^2 + bx + c = 0
    // ---------------------------------------------------------------

    @Test
    fun quadratic_twoRealRoots() {
        // x^2 - 5x + 6 = 0  =>  x=3, x=2
        val result = EquationSolver.solveQuadratic(1.0, -5.0, 6.0)
        assertTrue(result is QuadraticResult.TwoRealRoots)
        val r = result as QuadraticResult.TwoRealRoots
        assertEquals(3.0, r.x1, delta)
        assertEquals(2.0, r.x2, delta)
        assertTrue(r.discriminant > 0)
        assertTrue(r.steps.isNotEmpty())
    }

    @Test
    fun quadratic_oneRepeatedRoot() {
        // x^2 - 4x + 4 = 0  =>  x=2 (repeated)
        val result = EquationSolver.solveQuadratic(1.0, -4.0, 4.0)
        assertTrue(result is QuadraticResult.OneRepeatedRoot)
        val r = result as QuadraticResult.OneRepeatedRoot
        assertEquals(2.0, r.x, delta)
        assertEquals(0.0, r.discriminant, delta)
    }

    @Test
    fun quadratic_complexRoots() {
        // x^2 + 2x + 5 = 0  =>  discriminant = 4 - 20 = -16
        //   x = -1 +/- 2i
        val result = EquationSolver.solveQuadratic(1.0, 2.0, 5.0)
        assertTrue(result is QuadraticResult.ComplexRoots)
        val r = result as QuadraticResult.ComplexRoots
        assertEquals(-1.0, r.realPart, delta)
        assertEquals(2.0, r.imaginaryPart, delta)
        assertTrue(r.discriminant < 0)
    }

    @Test
    fun quadratic_degenerateToLinear() {
        // 0x^2 + 3x + 6 = 0  =>  linear: x = -2
        val result = EquationSolver.solveQuadratic(0.0, 3.0, 6.0)
        assertTrue(result is QuadraticResult.DegenerateLinear)
        val inner = (result as QuadraticResult.DegenerateLinear).linearResult
        assertTrue(inner is LinearResult.Solution)
        assertEquals(-2.0, (inner as LinearResult.Solution).x, delta)
    }

    // ---------------------------------------------------------------
    // System 2x2
    // ---------------------------------------------------------------

    @Test
    fun system2x2_normalCase() {
        // 2x + 3y = 8
        // x -  y = 1
        // => x = 2.2, y = 1.2
        val result = EquationSolver.solveSystem2x2(2.0, 3.0, 8.0, 1.0, -1.0, 1.0)
        assertTrue(result is SystemResult.Solution)
        val r = result as SystemResult.Solution
        assertEquals(2.2, r.x, delta)
        assertEquals(1.2, r.y, delta)
    }

    @Test
    fun system2x2_noSolution_parallelLines() {
        // 2x + 4y = 6
        // 1x + 2y = 5   (parallel, inconsistent)
        val result = EquationSolver.solveSystem2x2(2.0, 4.0, 6.0, 1.0, 2.0, 5.0)
        assertTrue(result is SystemResult.NoSolution)
    }

    @Test
    fun system2x2_infiniteSolutions_identicalLines() {
        // 2x + 4y = 6
        // 4x + 8y = 12  (same line scaled by 2)
        val result = EquationSolver.solveSystem2x2(2.0, 4.0, 6.0, 4.0, 8.0, 12.0)
        assertTrue(result is SystemResult.InfiniteSolutions)
    }

    // ---------------------------------------------------------------
    // System 3x3
    // ---------------------------------------------------------------

    @Test
    fun system3x3_normalCase() {
        // x +  y +  z = 6
        // 2x - y +  z = 3
        // x + 2y - z  = 2
        // Solution: x=1, y=2, z=3
        val matrix = arrayOf(
            doubleArrayOf(1.0, 1.0, 1.0, 6.0),
            doubleArrayOf(2.0, -1.0, 1.0, 3.0),
            doubleArrayOf(1.0, 2.0, -1.0, 2.0),
        )
        val result = EquationSolver.solveSystem3x3(matrix)
        assertTrue(result is SystemResult3x3.Solution)
        val r = result as SystemResult3x3.Solution
        assertEquals(1.0, r.x, delta)
        assertEquals(2.0, r.y, delta)
        assertEquals(3.0, r.z, delta)
    }

    @Test
    fun system3x3_degenerateCase() {
        // All rows identical => infinite solutions
        val matrix = arrayOf(
            doubleArrayOf(1.0, 2.0, 3.0, 4.0),
            doubleArrayOf(2.0, 4.0, 6.0, 8.0),
            doubleArrayOf(3.0, 6.0, 9.0, 12.0),
        )
        val result = EquationSolver.solveSystem3x3(matrix)
        assertTrue(result is SystemResult3x3.InfiniteSolutions)
    }

    @Test
    fun system3x3_noSolution() {
        // Inconsistent system:
        //  x + y + z = 1
        //  x + y + z = 2
        //  x + y + z = 3
        val matrix = arrayOf(
            doubleArrayOf(1.0, 1.0, 1.0, 1.0),
            doubleArrayOf(1.0, 1.0, 1.0, 2.0),
            doubleArrayOf(1.0, 1.0, 1.0, 3.0),
        )
        val result = EquationSolver.solveSystem3x3(matrix)
        assertTrue(result is SystemResult3x3.NoSolution)
    }
}
