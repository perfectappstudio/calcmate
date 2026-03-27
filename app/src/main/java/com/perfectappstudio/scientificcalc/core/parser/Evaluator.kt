package com.perfectappstudio.scientificcalc.core.parser

import com.perfectappstudio.scientificcalc.core.math.Combinatorics
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import kotlin.math.*

class Evaluator(private val angleUnit: AngleUnit = AngleUnit.RADIAN) {

    fun evaluate(node: ASTNode): CalcResult = when (node) {
        is ASTNode.NumberNode -> CalcResult.RealResult(node.value)
        is ASTNode.ConstantNode -> CalcResult.RealResult(evaluateConstant(node))
        is ASTNode.NegationNode -> CalcResult.RealResult(-evaluate(node.operand).toDouble())
        is ASTNode.BinaryOpNode -> CalcResult.RealResult(evaluateBinaryOp(node))
        is ASTNode.UnaryFuncNode -> CalcResult.RealResult(evaluateUnaryFunc(node))
        is ASTNode.LogBaseNode -> CalcResult.RealResult(evaluateLogBase(node))
        is ASTNode.PermCombNode -> CalcResult.RealResult(evaluatePermComb(node))
        is ASTNode.FactorialNode -> CalcResult.RealResult(evaluateFactorial(node))
        is ASTNode.VariableNode -> evaluateVariable(node)
        is ASTNode.AnsNode -> evaluateAns()
        is ASTNode.PercentNode -> CalcResult.RealResult(evaluate(node.operand).toDouble() / 100.0)
        is ASTNode.RandomNode -> CalcResult.RealResult(com.perfectappstudio.scientificcalc.core.math.RandomUtil.randomNumber())
    }

    private fun evaluateConstant(node: ASTNode.ConstantNode): Double = when (node.type) {
        TokenType.PI -> PI
        TokenType.E -> E
        else -> Double.NaN
    }

    private fun evaluateBinaryOp(node: ASTNode.BinaryOpNode): Double {
        val left = evaluate(node.left).toDouble()
        val right = evaluate(node.right).toDouble()
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
        val arg = evaluate(node.argument).toDouble()
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
        val base = evaluate(node.base).toDouble()
        val arg = evaluate(node.argument).toDouble()
        return log(arg, base)
    }

    private fun evaluatePermComb(node: ASTNode.PermCombNode): Double {
        val n = evaluate(node.n).toDouble()
        val r = evaluate(node.r).toDouble()
        if (n != floor(n) || r != floor(r) || n < 0 || r < 0) return Double.NaN
        return when (node.type) {
            TokenType.NPR -> Combinatorics.nPr(n.toLong(), r.toLong())
            TokenType.NCR -> Combinatorics.nCr(n.toLong(), r.toLong())
            else -> Double.NaN
        }
    }

    private fun evaluateFactorial(node: ASTNode.FactorialNode): Double {
        val value = evaluate(node.operand).toDouble()
        if (value < 0 || value != floor(value)) return Double.NaN
        return Combinatorics.factorial(value.toLong())
    }

    private fun evaluateVariable(node: ASTNode.VariableNode): CalcResult {
        return com.perfectappstudio.scientificcalc.core.model.MemoryManager.recallVariable(node.name)
    }

    private fun evaluateAns(): CalcResult {
        return com.perfectappstudio.scientificcalc.core.model.MemoryManager.ans
    }

    private fun toRadians(value: Double): Double = when (angleUnit) {
        AngleUnit.DEGREE -> Math.toRadians(value)
        AngleUnit.RADIAN -> value
        AngleUnit.GRADIAN -> value * PI / 200.0
    }

    private fun fromRadians(value: Double): Double = when (angleUnit) {
        AngleUnit.DEGREE -> Math.toDegrees(value)
        AngleUnit.RADIAN -> value
        AngleUnit.GRADIAN -> value * 200.0 / PI
    }
}
