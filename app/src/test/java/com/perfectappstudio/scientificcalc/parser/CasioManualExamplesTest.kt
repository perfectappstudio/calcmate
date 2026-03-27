package com.perfectappstudio.scientificcalc.parser

import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.parser.Evaluator
import com.perfectappstudio.scientificcalc.core.parser.Lexer
import com.perfectappstudio.scientificcalc.core.parser.Parser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Comprehensive test class replicating calculation examples from the
 * Casio fx-991MS manual, verifying CalcMate produces matching results.
 */
class CasioManualExamplesTest {

    private val DELTA = 1e-4

    private fun eval(expr: String, angleUnit: AngleUnit = AngleUnit.DEGREE): Double {
        val tokens = Lexer(expr).tokenize()
        val ast = Parser(tokens).parse()
        return Evaluator(angleUnit).evaluate(ast).toDouble()
    }

    // ---------------------------------------------------------------
    // Basic Calculations (p.16-18)
    // ---------------------------------------------------------------

    @Test
    fun `basic - 4 times sin(30) times (30+10x3) equals 120 in DEG`() {
        // sin(30) = 0.5 in DEG, so 4*0.5*(30+30) = 2*60 = 120
        assertEquals(120.0, eval("4*sin(30)*(30+10*3)"), DELTA)
    }

    @Test
    fun `basic - 23 plus 4_5 minus 53 equals -25_5`() {
        assertEquals(-25.5, eval("23+4.5-53"), DELTA)
    }

    @Test
    fun `basic - 56 times (-12) divided by (-2_5) equals 268_8`() {
        assertEquals(268.8, eval("56*(-12)/(-2.5)"), DELTA)
    }

    @Test
    fun `basic - 7x8 minus 4x5 equals 36`() {
        assertEquals(36.0, eval("7*8-4*5"), DELTA)
    }

    // ---------------------------------------------------------------
    // Trigonometric Functions (p.31)
    // ---------------------------------------------------------------

    @Test
    fun `trig - sin(30) equals 0_5 in DEG`() {
        assertEquals(0.5, eval("sin(30)", AngleUnit.DEGREE), DELTA)
    }

    @Test
    fun `trig - cos(pi div 3) equals 0_5 in RAD`() {
        assertEquals(0.5, eval("cos(pi/3)", AngleUnit.RADIAN), DELTA)
    }

    @Test
    fun `trig - tan(-35) in GRA equals -0_6128`() {
        // 35 gradians -> tan(-35 grad) = -0.612800788...
        assertEquals(-0.6128, eval("tan(-35)", AngleUnit.GRADIAN), DELTA)
    }

    @Test
    fun `trig - asin(0_5) equals 30 in DEG`() {
        assertEquals(30.0, eval("asin(0.5)", AngleUnit.DEGREE), DELTA)
    }

    @Test
    fun `trig - atan(0_741) equals 36_5384 in DEG`() {
        assertEquals(36.5384, eval("atan(0.741)", AngleUnit.DEGREE), DELTA)
    }

    // ---------------------------------------------------------------
    // Hyperbolic Functions (p.32)
    // ---------------------------------------------------------------

    @Test
    fun `hyp - sinh(3_6) equals 18_28545536`() {
        assertEquals(18.28545536, eval("sinh(3.6)"), DELTA)
    }

    @Test
    fun `hyp - asinh(30) equals 4_094622224`() {
        assertEquals(4.094622224, eval("asinh(30)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Exponential and Logarithmic Functions (p.32-33)
    // ---------------------------------------------------------------

    @Test
    fun `exp - e to the 10 equals 22026_46579`() {
        assertEquals(22026.46579, eval("e^10"), DELTA)
    }

    @Test
    fun `exp - 10 to the 1_5 equals 31_6227766`() {
        assertEquals(31.6227766, eval("10^1.5"), DELTA)
    }

    @Test
    fun `exp - 2 to the -3 equals 0_125`() {
        assertEquals(0.125, eval("2^(-3)"), DELTA)
    }

    @Test
    fun `log - log(1_23) equals 0_08990511`() {
        assertEquals(0.08990511, eval("log(1.23)"), DELTA)
    }

    @Test
    fun `log - ln(90) equals 4_49980967`() {
        assertEquals(4.49980967, eval("ln(90)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Power and Root Functions (p.33-34)
    // ---------------------------------------------------------------

    @Test
    fun `power - sqrt(2) plus sqrt(3) times sqrt(5) equals 5_2872`() {
        // sqrt(2) + sqrt(3)*sqrt(5) = 1.41421... + 3.87298... = 5.28719...
        assertEquals(5.2872, eval("sqrt(2)+sqrt(3)*sqrt(5)"), DELTA)
    }

    @Test
    fun `power - 123 plus 30 squared equals 1023`() {
        assertEquals(1023.0, eval("123+30^2"), DELTA)
    }

    @Test
    fun `power - 12 cubed equals 1728`() {
        assertEquals(1728.0, eval("12^3"), DELTA)
    }

    // ---------------------------------------------------------------
    // Factorial and Combinatorics (p.36-37)
    // ---------------------------------------------------------------

    @Test
    fun `factorial - (5+3)! equals 40320`() {
        assertEquals(40320.0, eval("(5+3)!"), DELTA)
    }

    @Test
    fun `combinatorics - 7nPr4 equals 840`() {
        assertEquals(840.0, eval("nPr(7,4)"), DELTA)
    }

    @Test
    fun `combinatorics - 10nCr4 equals 210`() {
        assertEquals(210.0, eval("nCr(10,4)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Equation Solver (p.58-61) — tested separately in
    // EquationSolverExtendedTest for detailed root verification
    // ---------------------------------------------------------------

    // Cubic: x^3 - 2x^2 - x + 2 = 0 => roots: 2, 1, -1
    @Test
    fun `equation - cubic x3-2x2-x+2 roots are 2, 1, -1`() {
        val result = com.perfectappstudio.scientificcalc.core.math.EquationSolver
            .solveCubic(1.0, -2.0, -1.0, 2.0)
        assert(result is com.perfectappstudio.scientificcalc.core.math.CubicResult.ThreeRealRoots)
        val roots = result as com.perfectappstudio.scientificcalc.core.math.CubicResult.ThreeRealRoots
        assertEquals(2.0, roots.x1, DELTA)
        assertEquals(1.0, roots.x2, DELTA)
        assertEquals(-1.0, roots.x3, DELTA)
    }

    // Quadratic: 8x^2 - 4x + 5 = 0 => complex: 0.25 +/- 0.75i
    @Test
    fun `equation - quadratic 8x2-4x+5 complex roots 0_25 pm 0_75i`() {
        val result = com.perfectappstudio.scientificcalc.core.math.EquationSolver
            .solveQuadratic(8.0, -4.0, 5.0)
        assert(result is com.perfectappstudio.scientificcalc.core.math.QuadraticResult.ComplexRoots)
        val roots = result as com.perfectappstudio.scientificcalc.core.math.QuadraticResult.ComplexRoots
        assertEquals(0.25, roots.realPart, DELTA)
        assertEquals(0.75, roots.imaginaryPart, DELTA)
    }

    // Simultaneous 3x3: 2x+3y-z=15, 3x-2y+2z=4, 5x+3y-4z=9 => x=2, y=5, z=4
    @Test
    fun `equation - simultaneous 3x3 gives x=2 y=5 z=4`() {
        val coefficients = arrayOf(
            doubleArrayOf(2.0, 3.0, -1.0, 15.0),
            doubleArrayOf(3.0, -2.0, 2.0, 4.0),
            doubleArrayOf(5.0, 3.0, -4.0, 9.0),
        )
        val result = com.perfectappstudio.scientificcalc.core.math.EquationSolver
            .solveSystem3x3(coefficients)
        assert(result is com.perfectappstudio.scientificcalc.core.math.SystemResult3x3.Solution)
        val sol = result as com.perfectappstudio.scientificcalc.core.math.SystemResult3x3.Solution
        assertEquals(2.0, sol.x, DELTA)
        assertEquals(5.0, sol.y, DELTA)
        assertEquals(4.0, sol.z, DELTA)
    }
}
