package com.calcmate.scientificcalculator.core.parser

import com.calcmate.scientificcalculator.core.math.Combinatorics
import com.calcmate.scientificcalculator.core.model.AngleUnit
import kotlin.math.*

class Evaluator(private val angleUnit: AngleUnit = AngleUnit.RADIAN) {

    fun evaluate(node: ASTNode): Double = when (node) {
        is ASTNode.NumberNode -> node.value
        is ASTNode.ConstantNode -> evaluateConstant(node)
        is ASTNode.NegationNode -> -evaluate(node.operand)
        is ASTNode.BinaryOpNode -> evaluateBinaryOp(node)
        is ASTNode.UnaryFuncNode -> evaluateUnaryFunc(node)
        is ASTNode.LogBaseNode -> evaluateLogBase(node)
        is ASTNode.PermCombNode -> evaluatePermComb(node)
        is ASTNode.FactorialNode -> evaluateFactorial(node)
    }

    private fun evaluateConstant(node: ASTNode.ConstantNode): Double = when (node.type) {
        TokenType.PI -> PI
        TokenType.E -> E
        else -> Double.NaN
    }

    private fun evaluateBinaryOp(node: ASTNode.BinaryOpNode): Double {
        val left = evaluate(node.left)
        val right = evaluate(node.right)
        return when (node.op) {
            TokenType.PLUS -> left + right
            TokenType.MINUS -> left - right
            TokenType.MULTIPLY -> left * right
            TokenType.DIVIDE -> left / right
            TokenType.MOD -> left % right
            TokenType.POWER -> left.pow(right)
            else -> Double.NaN
        }
    }

    private fun evaluateUnaryFunc(node: ASTNode.UnaryFuncNode): Double {
        val arg = evaluate(node.argument)
        return when (node.func) {
            TokenType.SIN -> sin(toRadians(arg))
            TokenType.COS -> cos(toRadians(arg))
            TokenType.TAN -> tan(toRadians(arg))

            TokenType.ASIN -> fromRadians(asin(arg))
            TokenType.ACOS -> fromRadians(acos(arg))
            TokenType.ATAN -> fromRadians(atan(arg))

            TokenType.SINH -> sinh(arg)
            TokenType.COSH -> cosh(arg)
            TokenType.TANH -> tanh(arg)

            TokenType.ASINH -> asinh(arg)
            TokenType.ACOSH -> acosh(arg)
            TokenType.ATANH -> atanh(arg)

            TokenType.LN -> ln(arg)
            TokenType.LOG -> log10(arg)
            TokenType.SQRT -> sqrt(arg)
            TokenType.CBRT -> cbrt(arg)
            TokenType.ABS -> abs(arg)

            else -> Double.NaN
        }
    }

    private fun evaluateLogBase(node: ASTNode.LogBaseNode): Double {
        val base = evaluate(node.base)
        val arg = evaluate(node.argument)
        return log(arg, base)
    }

    private fun evaluatePermComb(node: ASTNode.PermCombNode): Double {
        val n = evaluate(node.n)
        val r = evaluate(node.r)
        if (n != floor(n) || r != floor(r) || n < 0 || r < 0) return Double.NaN
        return when (node.type) {
            TokenType.NPR -> Combinatorics.nPr(n.toLong(), r.toLong())
            TokenType.NCR -> Combinatorics.nCr(n.toLong(), r.toLong())
            else -> Double.NaN
        }
    }

    private fun evaluateFactorial(node: ASTNode.FactorialNode): Double {
        val value = evaluate(node.operand)
        if (value < 0 || value != floor(value)) return Double.NaN
        return Combinatorics.factorial(value.toLong())
    }

    private fun toRadians(value: Double): Double = when (angleUnit) {
        AngleUnit.DEGREE -> Math.toRadians(value)
        AngleUnit.RADIAN -> value
    }

    private fun fromRadians(value: Double): Double = when (angleUnit) {
        AngleUnit.DEGREE -> Math.toDegrees(value)
        AngleUnit.RADIAN -> value
    }
}
