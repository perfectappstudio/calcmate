package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.Combinatorics
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.parser.*
import org.junit.Test
import org.junit.Assert.*

class ScientificDomainTest {
    @Test fun largeFiniteCombinationDoesNotOverflowItsIntermediateProduct() {
        val value = Combinatorics.nCr(1024, 512)
        assertTrue(value.isFinite())
        assertEquals(1.0, value / 4.481254552098971e306, 1e-13)
    }

    @Test(timeout = 1000) fun hugeCombinationsAndPermutationsStopAtOverflow() {
        assertTrue(Combinatorics.nCr(1_000_000_000, 500_000_000).isInfinite())
        assertTrue(Combinatorics.nPr(1_000_000_000, 500_000_000).isInfinite())
        assertEquals(1.0, Combinatorics.nPr(Long.MAX_VALUE, 0), 0.0)
    }

    @Test fun tangentPoleIsInvalidButNearbyValueRemainsFinite() {
        fun evaluate(input: String) = Evaluator(AngleUnit.DEGREE).evaluate(Parser(Lexer(input).tokenize()).parse()).toDouble()
        assertTrue(evaluate("tan(90)").isNaN())
        assertTrue(evaluate("tan(89.9999)").isFinite())
        assertTrue(evaluate("nPr(1e20,1)").isNaN())
    }
}
