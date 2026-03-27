package com.perfectappstudio.scientificcalc.core.math

import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import com.perfectappstudio.scientificcalc.core.parser.CalcResult
import com.perfectappstudio.scientificcalc.core.parser.Evaluator
import com.perfectappstudio.scientificcalc.core.parser.Lexer
import com.perfectappstudio.scientificcalc.core.parser.Parser

/**
 * Numerical integration (Simpson's rule) and differentiation (central difference).
 *
 * The expression string is parsed using the existing Lexer/Parser/Evaluator pipeline.
 * The variable **X** is used as the independent variable: its value is written into
 * [MemoryManager] before each evaluation.
 */
object NumericalCalculus {

    /**
     * Approximate the definite integral of [expression] from [a] to [b] using
     * composite Simpson's rule.
     *
     * @param n number of sub-intervals. When in 1..9 range it is treated as an
     *          exponent and 2^n partitions are used; when omitted the default
     *          is 64 partitions (2^6).
     * @param angleUnit angle unit forwarded to the evaluator.
     */
    fun integrate(
        expression: String,
        a: Double,
        b: Double,
        n: Int = 64,
        angleUnit: AngleUnit = AngleUnit.RADIAN
    ): Double {
        val partitions = if (n in 1..9) 1 shl n else n
        // Simpson's rule requires an even number of partitions
        val evenN = if (partitions % 2 != 0) partitions + 1 else partitions
        val h = (b - a) / evenN

        var sum = evalAt(expression, a, angleUnit) + evalAt(expression, b, angleUnit)

        for (i in 1 until evenN) {
            val x = a + i * h
            val coeff = if (i % 2 == 0) 2.0 else 4.0
            sum += coeff * evalAt(expression, x, angleUnit)
        }

        return sum * h / 3.0
    }

    /**
     * Approximate the derivative of [expression] at [a] using the central
     * difference method: (f(a+dx) - f(a-dx)) / (2*dx).
     */
    fun differentiate(
        expression: String,
        a: Double,
        dx: Double = 1e-10,
        angleUnit: AngleUnit = AngleUnit.RADIAN
    ): Double {
        val fPlus = evalAt(expression, a + dx, angleUnit)
        val fMinus = evalAt(expression, a - dx, angleUnit)
        return (fPlus - fMinus) / (2.0 * dx)
    }

    private fun evalAt(expression: String, x: Double, angleUnit: AngleUnit): Double {
        MemoryManager.storeVariable('X', CalcResult.RealResult(x))
        val tokens = Lexer(expression).tokenize()
        val ast = Parser(tokens).parse()
        return Evaluator(angleUnit).evaluate(ast).toDouble()
    }
}
