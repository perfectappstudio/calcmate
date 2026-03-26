package com.calcmate.scientificcalculator.core.parser

import com.calcmate.scientificcalculator.core.model.AngleUnit
import com.calcmate.scientificcalculator.core.model.MemoryManager

/**
 * Evaluates multi-statement expressions separated by `:`.
 *
 * Each sub-expression is evaluated left to right. The result of each
 * sub-expression is stored in Ans and is available to subsequent
 * sub-expressions. The final result is returned.
 */
class MultiStatementEvaluator(
    private val angleUnit: AngleUnit = AngleUnit.RADIAN,
) {

    /**
     * Evaluate an expression that may contain `:` separators.
     *
     * @return the result of the last sub-expression.
     * @throws MultiStatementException if any sub-expression fails, reporting which one.
     */
    fun evaluate(expression: String): CalcResult {
        val statements = expression.split(":")
        if (statements.isEmpty()) {
            throw MultiStatementException(1, "Empty expression")
        }

        var lastResult: CalcResult = CalcResult.RealResult(0.0)

        statements.forEachIndexed { index, raw ->
            val stmt = raw.trim()
            if (stmt.isEmpty()) {
                throw MultiStatementException(
                    index + 1,
                    "Empty sub-expression at position ${index + 1}",
                )
            }
            try {
                val tokens = Lexer(stmt).tokenize()
                val ast = Parser(tokens).parse()
                lastResult = Evaluator(angleUnit).evaluate(ast)
                MemoryManager.ans = lastResult
            } catch (e: Exception) {
                throw MultiStatementException(
                    index + 1,
                    "Error in statement ${index + 1}: ${e.message}",
                )
            }
        }

        return lastResult
    }
}

class MultiStatementException(
    val statementIndex: Int,
    message: String,
) : RuntimeException(message)
