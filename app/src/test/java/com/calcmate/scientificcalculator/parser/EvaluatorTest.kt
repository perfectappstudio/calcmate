package com.calcmate.scientificcalculator.parser

import com.calcmate.scientificcalculator.core.model.AngleUnit
import com.calcmate.scientificcalculator.core.parser.Evaluator
import com.calcmate.scientificcalculator.core.parser.Lexer
import com.calcmate.scientificcalculator.core.parser.Parser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluatorTest {

    private val DELTA = 1e-9

    private fun eval(input: String, angleUnit: AngleUnit = AngleUnit.RADIAN): Double {
        val tokens = Lexer(input).tokenize()
        val ast = Parser(tokens).parse()
        return Evaluator(angleUnit).evaluate(ast).toDouble()
    }

    // ---------------------------------------------------------------
    // Basic arithmetic
    // ---------------------------------------------------------------

    @Test
    fun `addition 2+3 equals 5`() {
        assertEquals(5.0, eval("2+3"), DELTA)
    }

    @Test
    fun `subtraction 10-3 equals 7`() {
        assertEquals(7.0, eval("10-3"), DELTA)
    }

    @Test
    fun `multiplication 6 times 7 equals 42`() {
        assertEquals(42.0, eval("6*7"), DELTA)
    }

    @Test
    fun `division 15 div 4 equals 3_75`() {
        assertEquals(3.75, eval("15/4"), DELTA)
    }

    // ---------------------------------------------------------------
    // Precedence
    // ---------------------------------------------------------------

    @Test
    fun `precedence 2+3 times 4 equals 14`() {
        assertEquals(14.0, eval("2+3*4"), DELTA)
    }

    @Test
    fun `parentheses (2+3) times 4 equals 20`() {
        assertEquals(20.0, eval("(2+3)*4"), DELTA)
    }

    // ---------------------------------------------------------------
    // Powers
    // ---------------------------------------------------------------

    @Test
    fun `power 2^10 equals 1024`() {
        assertEquals(1024.0, eval("2^10"), DELTA)
    }

    @Test
    fun `right associative power 2^3^2 equals 512`() {
        // 2^(3^2) = 2^9 = 512
        assertEquals(512.0, eval("2^3^2"), DELTA)
    }

    // ---------------------------------------------------------------
    // Trig (radians)
    // ---------------------------------------------------------------

    @Test
    fun `sin(0) equals 0 in radians`() {
        assertEquals(0.0, eval("sin(0)"), DELTA)
    }

    @Test
    fun `cos(0) equals 1 in radians`() {
        assertEquals(1.0, eval("cos(0)"), DELTA)
    }

    @Test
    fun `tan(0) equals 0 in radians`() {
        assertEquals(0.0, eval("tan(0)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Trig (degrees)
    // ---------------------------------------------------------------

    @Test
    fun `sin(90) equals 1 in degrees`() {
        assertEquals(1.0, eval("sin(90)", AngleUnit.DEGREE), DELTA)
    }

    @Test
    fun `cos(180) equals -1 in degrees`() {
        assertEquals(-1.0, eval("cos(180)", AngleUnit.DEGREE), DELTA)
    }

    // ---------------------------------------------------------------
    // Logarithms
    // ---------------------------------------------------------------

    @Test
    fun `ln(1) equals 0`() {
        assertEquals(0.0, eval("ln(1)"), DELTA)
    }

    @Test
    fun `log(100) equals 2`() {
        assertEquals(2.0, eval("log(100)"), DELTA)
    }

    @Test
    fun `log base -- log(2,8) equals 3`() {
        assertEquals(3.0, eval("log(2,8)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Sqrt / Cbrt
    // ---------------------------------------------------------------

    @Test
    fun `sqrt(144) equals 12`() {
        assertEquals(12.0, eval("sqrt(144)"), DELTA)
    }

    @Test
    fun `cbrt(27) equals 3`() {
        assertEquals(3.0, eval("cbrt(27)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------

    @Test
    fun `pi approximately 3_14159`() {
        assertEquals(Math.PI, eval("pi"), DELTA)
    }

    @Test
    fun `e approximately 2_71828`() {
        assertEquals(Math.E, eval("e"), DELTA)
    }

    // ---------------------------------------------------------------
    // Factorial
    // ---------------------------------------------------------------

    @Test
    fun `5 factorial equals 120`() {
        assertEquals(120.0, eval("5!"), DELTA)
    }

    @Test
    fun `0 factorial equals 1`() {
        assertEquals(1.0, eval("0!"), DELTA)
    }

    // ---------------------------------------------------------------
    // Combinatorics
    // ---------------------------------------------------------------

    @Test
    fun `nPr(5,2) equals 20`() {
        assertEquals(20.0, eval("nPr(5,2)"), DELTA)
    }

    @Test
    fun `nCr(5,2) equals 10`() {
        assertEquals(10.0, eval("nCr(5,2)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Implicit multiplication
    // ---------------------------------------------------------------

    @Test
    fun `implicit multiply 2pi approximately 6_28318`() {
        assertEquals(2.0 * Math.PI, eval("2pi"), DELTA)
    }

    // ---------------------------------------------------------------
    // Abs
    // ---------------------------------------------------------------

    @Test
    fun `abs(-5) equals 5`() {
        assertEquals(5.0, eval("abs(-5)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Percent
    // ---------------------------------------------------------------

    @Test
    fun `50 percent equals 0_5`() {
        assertEquals(0.5, eval("50%"), DELTA)
    }

    @Test
    fun `10 percent times 3 equals 0_3`() {
        assertEquals(0.3, eval("10%3"), DELTA)
    }

    // ---------------------------------------------------------------
    // Edge cases
    // ---------------------------------------------------------------

    @Test
    fun `division by zero returns Infinity`() {
        val result = eval("1/0")
        assertTrue(result.isInfinite())
    }

    @Test
    fun `sqrt of negative number returns NaN`() {
        val result = eval("sqrt(-1)")
        assertTrue(result.isNaN())
    }

    @Test
    fun `log of negative number returns NaN`() {
        val result = eval("ln(-1)")
        assertTrue(result.isNaN())
    }

    // ---------------------------------------------------------------
    // Complex expression
    // ---------------------------------------------------------------

    @Test
    fun `complex expression 2 times sin(pi div 6) + sqrt(9) equals 4`() {
        // sin(pi/6) = sin(30 deg) = 0.5
        // 2 * 0.5 + sqrt(9) = 1 + 3 = 4
        assertEquals(4.0, eval("2*sin(pi/6)+sqrt(9)"), DELTA)
    }

    // ---------------------------------------------------------------
    // Unary minus
    // ---------------------------------------------------------------

    @Test
    fun `unary minus evaluates -5`() {
        assertEquals(-5.0, eval("-5"), DELTA)
    }

    @Test
    fun `unary minus on group -(3+2) equals -5`() {
        assertEquals(-5.0, eval("-(3+2)"), DELTA)
    }
}
