package com.perfectappstudio.scientificcalc.parser

import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import com.perfectappstudio.scientificcalc.core.parser.CalcResult
import com.perfectappstudio.scientificcalc.core.parser.Evaluator
import com.perfectappstudio.scientificcalc.core.parser.Lexer
import com.perfectappstudio.scientificcalc.core.parser.Parser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EvaluatorMemoryTest {

    private val DELTA = 1e-9

    @Before
    fun setUp() {
        MemoryManager.clearAll()
    }

    private fun eval(input: String, angleUnit: AngleUnit = AngleUnit.RADIAN): Double {
        val tokens = Lexer(input).tokenize()
        val ast = Parser(tokens).parse()
        return Evaluator(angleUnit).evaluate(ast).toDouble()
    }

    // ---------------------------------------------------------------
    // Ans token
    // ---------------------------------------------------------------

    @Test
    fun `Ans evaluates to MemoryManager ans value`() {
        MemoryManager.ans = CalcResult.RealResult(7.0)
        assertEquals(7.0, eval("Ans"), DELTA)
    }

    @Test
    fun `Ans in expression adds to stored ans`() {
        MemoryManager.ans = CalcResult.RealResult(10.0)
        assertEquals(15.0, eval("Ans+5"), DELTA)
    }

    // ---------------------------------------------------------------
    // Variable tokens
    // ---------------------------------------------------------------

    @Test
    fun `variable A evaluates to stored value`() {
        MemoryManager.storeVariable('A', CalcResult.RealResult(3.0))
        assertEquals(3.0, eval("A"), DELTA)
    }

    @Test
    fun `variable B evaluates to stored value`() {
        MemoryManager.storeVariable('B', CalcResult.RealResult(12.5))
        assertEquals(12.5, eval("B"), DELTA)
    }

    @Test
    fun `variable X evaluates to stored value`() {
        MemoryManager.storeVariable('X', CalcResult.RealResult(-4.0))
        assertEquals(-4.0, eval("X"), DELTA)
    }

    @Test
    fun `expression A+3 with A=5 equals 8`() {
        MemoryManager.storeVariable('A', CalcResult.RealResult(5.0))
        assertEquals(8.0, eval("A+3"), DELTA)
    }

    @Test
    fun `expression 2*A+B with A=3 B=4 equals 10`() {
        MemoryManager.storeVariable('A', CalcResult.RealResult(3.0))
        MemoryManager.storeVariable('B', CalcResult.RealResult(4.0))
        assertEquals(10.0, eval("2*A+B"), DELTA)
    }

    // ---------------------------------------------------------------
    // Percent operator
    // ---------------------------------------------------------------

    @Test
    fun `200 percent equals 2`() {
        assertEquals(2.0, eval("200%"), DELTA)
    }

    @Test
    fun `50 percent equals 0_5`() {
        assertEquals(0.5, eval("50%"), DELTA)
    }

    @Test
    fun `12 percent equals 0_12`() {
        assertEquals(0.12, eval("12%"), DELTA)
    }

    @Test
    fun `15 percent equals 0_15`() {
        assertEquals(0.15, eval("15%"), DELTA)
    }

    @Test
    fun `500 times 10 percent equals 50`() {
        // 500 * (10/100) = 50
        assertEquals(50.0, eval("500*10%"), DELTA)
    }

    @Test
    fun `500 plus 500 times 10 percent equals 550`() {
        // 500 + 500*(10/100) = 500 + 50 = 550
        assertEquals(550.0, eval("500+500*10%"), DELTA)
    }

    @Test
    fun `Casio percentage 1500 times 12 percent equals 180`() {
        assertEquals(180.0, eval("1500*12%"), DELTA)
    }

    @Test
    fun `Casio ratio 660 div 880 percent equals 75`() {
        // 660 / (880/100) = 660 / 8.8 = 75
        assertEquals(75.0, eval("660/880%"), DELTA)
    }

    // ---------------------------------------------------------------
    // Gradian trig
    // ---------------------------------------------------------------

    @Test
    fun `sin 100 gradian equals 1`() {
        // 100 gradian = 90 degrees = pi/2 radians -> sin = 1
        assertEquals(1.0, eval("sin(100)", AngleUnit.GRADIAN), DELTA)
    }

    @Test
    fun `cos 200 gradian equals negative 1`() {
        // 200 gradian = 180 degrees -> cos = -1
        assertEquals(-1.0, eval("cos(200)", AngleUnit.GRADIAN), DELTA)
    }

    // ---------------------------------------------------------------
    // Ran# (random)
    // ---------------------------------------------------------------

    @Test
    fun `Ran# produces value between 0 and 1`() {
        val result = eval("Ran#")
        assertTrue("Ran# should be >= 0", result >= 0.0)
        assertTrue("Ran# should be < 1", result < 1.0)
    }
}
