package com.perfectappstudio.scientificcalc.parser

import com.perfectappstudio.scientificcalc.core.parser.Evaluator
import com.perfectappstudio.scientificcalc.core.parser.Lexer
import com.perfectappstudio.scientificcalc.core.parser.Parser
import com.perfectappstudio.scientificcalc.core.parser.ParseException
import org.junit.Assert.*
import org.junit.Test

class ExpressionQualityTest {
    @Test
    fun oversizedExpressionIsRejectedBeforeBuildingTheSyntaxTree() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            Lexer("1+".repeat(1000) + "1").tokenize()
        }
    }
    private fun evaluate(input: String) = Evaluator().evaluate(Parser(Lexer(input).tokenize()).parse()).toDouble()

    @Test
    fun powersTakePrecedenceOverUnaryMinus() {
        assertEquals(-4.0, evaluate("-2^2"), 0.0)
        assertEquals(4.0, evaluate("(-2)^2"), 0.0)
        assertEquals(-36.0, evaluate("-3!^2"), 0.0)
        assertEquals(0.125, evaluate("2^-3"), 0.0)
        assertEquals(512.0, evaluate("2^3^2"), 0.0)
        assertEquals(-0.25, evaluate("-2^-2"), 0.0)
    }

    @Test
    fun eulerConstantAndScientificExponentRemainDistinct() {
        assertEquals(2 * Math.E, evaluate("2e"), 1e-12)
        assertEquals(2 * Math.E + 3, evaluate("2*e+3"), 1e-12)
        assertEquals(2000.0, evaluate("2e+3"), 0.0)
        assertEquals(0.002, evaluate("2e-3"), 0.0)
        assertEquals(2 * Math.PI, evaluate("2pi"), 1e-12)
    }

    @Test
    fun invalidNamesAndCharactersAreRejectedInsteadOfInventingZero() {
        for (input in listOf("hello", "foo(2)", "2@3", "1.2.3", "2$3", "abc")) {
            assertThrows(input, ParseException::class.java) { evaluate(input) }
        }
    }
}
