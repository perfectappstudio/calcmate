package com.calcmate.scientificcalculator.core.math

import com.calcmate.scientificcalculator.core.model.MemoryManager
import com.calcmate.scientificcalculator.core.parser.CalcResult
import com.calcmate.scientificcalculator.core.parser.Evaluator
import com.calcmate.scientificcalculator.core.parser.Lexer
import com.calcmate.scientificcalculator.core.parser.Parser
import kotlin.math.abs

sealed class NewtonResult {
    data class Solution(val x: Double, val iterations: Int) : NewtonResult()
    data class CannotSolve(val reason: String) : NewtonResult()
}

object NewtonSolver {

    private const val H = 1e-8
    private const val DIVERGENCE_LIMIT = 1e15

    fun solve(
        expression: String,
        initialGuess: Double = 0.0,
        maxIterations: Int = 200,
        tolerance: Double = 1e-12,
    ): NewtonResult {
        // Validate that the expression can be parsed
        try {
            evaluateAt(expression, initialGuess)
        } catch (e: Exception) {
            return NewtonResult.CannotSolve("Invalid expression: ${e.message}")
        }

        var x = initialGuess

        for (i in 1..maxIterations) {
            val fx = try {
                evaluateAt(expression, x)
            } catch (_: Exception) {
                return NewtonResult.CannotSolve("Cannot evaluate f($x)")
            }

            // Check if already converged
            if (abs(fx) < tolerance) {
                return NewtonResult.Solution(x, i)
            }

            // Central difference for derivative
            val fxPlusH = try {
                evaluateAt(expression, x + H)
            } catch (_: Exception) {
                return NewtonResult.CannotSolve("Cannot evaluate derivative near x = $x")
            }
            val fxMinusH = try {
                evaluateAt(expression, x - H)
            } catch (_: Exception) {
                return NewtonResult.CannotSolve("Cannot evaluate derivative near x = $x")
            }

            val fpx = (fxPlusH - fxMinusH) / (2.0 * H)

            if (abs(fpx) < 1e-15) {
                return NewtonResult.CannotSolve("Derivative is near zero at x = ${formatX(x)}")
            }

            val xNew = x - fx / fpx

            if (abs(xNew) > DIVERGENCE_LIMIT) {
                return NewtonResult.CannotSolve("Solution diverged (|x| > $DIVERGENCE_LIMIT)")
            }

            if (abs(xNew - x) < tolerance) {
                return NewtonResult.Solution(xNew, i)
            }

            x = xNew
        }

        return NewtonResult.CannotSolve("Did not converge in $maxIterations iterations")
    }

    /**
     * Evaluates [expression] as f(X) at the given [xValue].
     * Sets MemoryManager variable 'X' to the current value, then parses and evaluates.
     */
    private fun evaluateAt(expression: String, xValue: Double): Double {
        // Store X in MemoryManager so the parser/evaluator can resolve it
        MemoryManager.storeVariable('X', CalcResult.RealResult(xValue))
        val tokens = Lexer(expression).tokenize()
        val ast = Parser(tokens).parse()
        return Evaluator().evaluate(ast).toDouble()
    }

    private fun formatX(value: Double): String {
        val s = "%.6f".format(value)
        return s.trimEnd('0').trimEnd('.')
    }
}
