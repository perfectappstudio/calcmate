package com.perfectappstudio.scientificcalc.parser

import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import com.perfectappstudio.scientificcalc.core.parser.MultiStatementEvaluator
import com.perfectappstudio.scientificcalc.core.parser.MultiStatementException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MultiStatementEvaluatorTest {

    private val DELTA = 1e-9

    @Before
    fun setUp() {
        MemoryManager.clearAll()
    }

    // ---------------------------------------------------------------
    // Single statement
    // ---------------------------------------------------------------

    @Test
    fun `single statement 2+3 equals 5`() {
        val result = MultiStatementEvaluator().evaluate("2+3")
        assertEquals(5.0, result.toDouble(), DELTA)
    }

    // ---------------------------------------------------------------
    // Two statements with Ans chaining
    // ---------------------------------------------------------------

    @Test
    fun `two statements 2+3 then Ans*4 equals 20`() {
        val result = MultiStatementEvaluator().evaluate("2+3:Ans*4")
        assertEquals(20.0, result.toDouble(), DELTA)
    }

    // ---------------------------------------------------------------
    // Three statements with Ans chaining
    // ---------------------------------------------------------------

    @Test
    fun `three statements 1+1 then Ans+1 then Ans+1 equals 4`() {
        val result = MultiStatementEvaluator().evaluate("1+1:Ans+1:Ans+1")
        assertEquals(4.0, result.toDouble(), DELTA)
    }

    // ---------------------------------------------------------------
    // Ans is updated after each statement
    // ---------------------------------------------------------------

    @Test
    fun `Ans is updated after each sub-expression`() {
        val result = MultiStatementEvaluator().evaluate("10:Ans+5:Ans*2")
        // 10 -> Ans=10, 10+5=15 -> Ans=15, 15*2=30
        assertEquals(30.0, result.toDouble(), DELTA)
    }

    // ---------------------------------------------------------------
    // Error in middle statement
    // ---------------------------------------------------------------

    @Test(expected = MultiStatementException::class)
    fun `error in middle statement throws MultiStatementException`() {
        // A malformed expression triggers a parse error
        MultiStatementEvaluator().evaluate("2+3:+*:4+5")
    }

    @Test
    fun `error in middle reports correct statement index`() {
        try {
            MultiStatementEvaluator().evaluate("2+3::4+5")
        } catch (e: MultiStatementException) {
            assertEquals(2, e.statementIndex)
        }
    }

    // ---------------------------------------------------------------
    // Empty expression between colons
    // ---------------------------------------------------------------

    @Test(expected = MultiStatementException::class)
    fun `empty sub-expression between colons throws`() {
        MultiStatementEvaluator().evaluate("2+3::4+5")
    }

    @Test(expected = MultiStatementException::class)
    fun `empty sub-expression at end throws`() {
        MultiStatementEvaluator().evaluate("2+3:")
    }
}
