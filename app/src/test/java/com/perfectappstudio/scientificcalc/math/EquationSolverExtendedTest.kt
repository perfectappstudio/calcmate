package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.CubicResult
import com.perfectappstudio.scientificcalc.core.math.EquationSolver
import com.perfectappstudio.scientificcalc.core.math.QuadraticResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Extended equation solver tests covering additional cubic edge cases
 * from the Casio fx-991MS manual and related scenarios.
 */
class EquationSolverExtendedTest {

    private val DELTA = 1e-4

    // ---------------------------------------------------------------
    // Cubic with one real root + two complex conjugate roots
    // x^3 + 1 = 0  =>  a=1, b=0, c=0, d=1
    // Roots: x1 = -1, x2 = 0.5 + 0.8660i, x3 = 0.5 - 0.8660i
    // ---------------------------------------------------------------

    @Test
    fun `cubic x3+1 has one real root -1 and two complex conjugates`() {
        val result = EquationSolver.solveCubic(1.0, 0.0, 0.0, 1.0)
        assertTrue(
            "Expected OneRealTwoComplex but got ${result::class.simpleName}",
            result is CubicResult.OneRealTwoComplex
        )
        val roots = result as CubicResult.OneRealTwoComplex
        assertEquals(-1.0, roots.x1, DELTA)
        assertEquals(0.5, roots.realPart, DELTA)
        assertEquals(0.8660, roots.imagPart, DELTA)
    }

    // ---------------------------------------------------------------
    // Cubic with triple root
    // x^3 - 3x^2 + 3x - 1 = 0  =>  (x-1)^3 = 0  =>  x = 1 (triple)
    // ---------------------------------------------------------------

    @Test
    fun `cubic (x-1)^3 has triple root at 1`() {
        val result = EquationSolver.solveCubic(1.0, -3.0, 3.0, -1.0)
        assertTrue(
            "Expected ThreeRealRoots but got ${result::class.simpleName}",
            result is CubicResult.ThreeRealRoots
        )
        val roots = result as CubicResult.ThreeRealRoots
        assertEquals(1.0, roots.x1, DELTA)
        assertEquals(1.0, roots.x2, DELTA)
        assertEquals(1.0, roots.x3, DELTA)
    }

    // ---------------------------------------------------------------
    // Cubic degenerate to quadratic when a = 0
    // 0*x^3 + 1*x^2 - 3*x + 2 = 0  =>  x^2 - 3x + 2 = 0
    //   => (x-1)(x-2) = 0  =>  x = 2, x = 1
    // ---------------------------------------------------------------

    @Test
    fun `cubic with a=0 degenerates to quadratic x2-3x+2`() {
        val result = EquationSolver.solveCubic(0.0, 1.0, -3.0, 2.0)
        assertTrue(
            "Expected DegenerateQuadratic but got ${result::class.simpleName}",
            result is CubicResult.DegenerateQuadratic
        )
        val degenerate = result as CubicResult.DegenerateQuadratic
        val quadResult = degenerate.quadraticResult
        assertTrue(
            "Expected TwoRealRoots but got ${quadResult::class.simpleName}",
            quadResult is QuadraticResult.TwoRealRoots
        )
        val roots = quadResult as QuadraticResult.TwoRealRoots
        assertEquals(2.0, roots.x1, DELTA)
        assertEquals(1.0, roots.x2, DELTA)
    }
}
