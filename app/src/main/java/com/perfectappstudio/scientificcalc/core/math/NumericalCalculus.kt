package com.perfectappstudio.scientificcalc.core.math

import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import com.perfectappstudio.scientificcalc.core.parser.*
import java.util.PriorityQueue
import kotlin.math.*

data class NumericalEstimate(val value: Double, val estimatedError: Double, val evaluations: Int)

object NumericalCalculus {
    fun integrate(
        expression: String,
        a: Double,
        b: Double,
        n: Int = 64,
        angleUnit: AngleUnit = AngleUnit.RADIAN,
    ): Double {
        require(a.isFinite() && b.isFinite()) { "Enter finite limits" }
        require(n in 1..65536) { "Partitions must be between 1 and 65536" }
        val partitions = if (n in 1..9) 1 shl n else n
        val evenN = if (partitions % 2 != 0) partitions + 1 else partitions
        val function = prepare(expression, angleUnit, MemoryManager.variables.toMap(), MemoryManager.ans) {}
        if (a == b) { function(a); return 0.0 }
        val h = (b - a) / evenN
        require(h.isFinite() && h != 0.0) { "Interval cannot be resolved at this precision" }
        var sum = function(a) + function(b)
        for (i in 1 until evenN) sum += (if (i % 2 == 0) 2.0 else 4.0) * function(a + i * h)
        val result = sum * h / 3
        require(result.isFinite()) { "Result out of range" }
        return result
    }

    fun estimateIntegral(
        expression: String,
        a: Double,
        b: Double,
        angleUnit: AngleUnit = AngleUnit.RADIAN,
        maxEvaluations: Int = 65536,
        variables: Map<Char, CalcResult> = MemoryManager.variables.toMap(),
        answer: CalcResult = MemoryManager.ans,
        checkCancelled: () -> Unit = {},
    ): NumericalEstimate {
        require(a.isFinite() && b.isFinite()) { "Enter finite limits" }
        require(maxEvaluations in 120..1_000_000) { "Invalid evaluation limit" }
        val function = prepare(expression, angleUnit, variables, answer, checkCancelled)
        if (a == b) { function(a); return NumericalEstimate(0.0, 0.0, 1) }
        val low = min(a, b)
        val high = max(a, b)
        require((high - low).isFinite()) { "Interval out of range" }
        val intervals = PriorityQueue<Interval>(compareByDescending { it.error })
        var value = 0.0
        var error = 0.0
        var evaluations = 0
        for (i in 0 until 8) {
            val start = low * (1 - i / 8.0) + high * (i / 8.0)
            val end = low * (1 - (i + 1) / 8.0) + high * ((i + 1) / 8.0)
            if (start == end) continue
            val interval = quadrature(function, start, end)
            intervals.add(interval)
            value += interval.value
            error += interval.error
            evaluations += 15
        }
        while (true) {
            require(value.isFinite() && error.isFinite()) { "Result out of range" }
            if (error <= 1e-8 + 1e-8 * abs(value)) {
                return NumericalEstimate(if (a > b) -value else value, max(0.0, error), evaluations)
            }
            require(evaluations + 30 <= maxEvaluations) { "Integral did not converge; check the function or narrow the interval" }
            val worst = intervals.remove()
            val middle = worst.a / 2 + worst.b / 2
            require(middle > worst.a && middle < worst.b) { "Integral cannot be resolved at this precision" }
            val left = quadrature(function, worst.a, middle)
            val right = quadrature(function, middle, worst.b)
            intervals.add(left)
            intervals.add(right)
            value += left.value + right.value - worst.value
            error = max(0.0, error + left.error + right.error - worst.error)
            evaluations += 30
        }
    }

    fun differentiate(
        expression: String,
        a: Double,
        dx: Double? = null,
        angleUnit: AngleUnit = AngleUnit.RADIAN,
    ): Double = estimateDerivative(expression, a, dx, angleUnit).value

    fun estimateDerivative(
        expression: String,
        a: Double,
        dx: Double? = null,
        angleUnit: AngleUnit = AngleUnit.RADIAN,
        variables: Map<Char, CalcResult> = MemoryManager.variables.toMap(),
        answer: CalcResult = MemoryManager.ans,
        checkCancelled: () -> Unit = {},
    ): NumericalEstimate {
        require(a.isFinite()) { "Enter a finite point" }
        require(dx == null || (dx.isFinite() && dx > 0)) { "Initial step must be finite and positive" }
        val function = prepare(expression, angleUnit, variables, answer, checkCancelled)
        val centerValue = function(a)
        var evaluations = 1
        var step = dx ?: Math.cbrt(Math.ulp(1.0)) * max(1.0, abs(a))
        var previous: Double? = null
        repeat(40) {
            val low = a - step
            val high = a + step
            require(low.isFinite() && high.isFinite() && low < a && high > a) { "Step cannot be resolved at this point; use a larger initial step" }
            val lowValue: Double
            val highValue: Double
            try {
                evaluations++
                lowValue = function(low)
                evaluations++
                highValue = function(high)
            } catch (_: NonFiniteFunction) {
                step = if (a != 0.0) min(step / 2, abs(a) / 2) else step / 2
                return@repeat
            }
            val left = (centerValue - lowValue) / (a - low)
            val right = (highValue - centerValue) / (high - a)
            val slope = left / 2 + right / 2
            require(slope.isFinite() && left.isFinite() && right.isFinite()) { "Derivative out of range" }
            val error = max(abs(right / 2 - left / 2), previous?.let { abs(slope - it) } ?: Double.POSITIVE_INFINITY)
            if (error <= 1e-8 + abs(slope) * 1e-6) return NumericalEstimate(slope, error, evaluations)
            previous = slope
            step /= 2
        }
        throw IllegalArgumentException("Derivative did not converge; the function may not be differentiable here")
    }

    private fun prepare(
        expression: String,
        angleUnit: AngleUnit,
        variables: Map<Char, CalcResult>,
        answer: CalcResult,
        checkCancelled: () -> Unit,
    ): (Double) -> Double {
        require(expression.length in 1..2048) { "Enter a function of x (up to 2048 characters)" }
        val tokens = Lexer(expression.replace('−', '-')).tokenize()
        require(tokens.none { it.type == TokenType.RANDOM }) { "Random values cannot be used as a calculus function" }
        val ast = Parser(tokens).parse()
        val context = variables.toMap()
        return { x ->
            checkCancelled()
            val value = Evaluator(angleUnit, context + ('X' to CalcResult.RealResult(x)), answer).evaluate(ast).toDouble()
            if (!value.isFinite()) throw NonFiniteFunction()
            value
        }
    }

    private data class Interval(val a: Double, val b: Double, val value: Double, val error: Double)
    private class NonFiniteFunction : IllegalArgumentException("Function is not finite in the sampled interval")

    private fun quadrature(function: (Double) -> Double, a: Double, b: Double): Interval {
        val middle = a / 2 + b / 2
        val radius = b / 2 - a / 2
        val width = b - a
        val center = function(middle)
        val lower = DoubleArray(7)
        val upper = DoubleArray(7)
        var kronrodMean = center * kronrodWeights[7] / 2
        var gaussMean = center * gaussWeights[3] / 2
        var absoluteMean = abs(center) * kronrodWeights[7] / 2
        for (i in 0..6) {
            lower[i] = function(middle - radius * nodes[i])
            upper[i] = function(middle + radius * nodes[i])
            val weight = kronrodWeights[i] / 2
            kronrodMean += lower[i] * weight + upper[i] * weight
            absoluteMean += abs(lower[i]) * weight + abs(upper[i]) * weight
            if (i % 2 == 1) {
                val gaussWeight = gaussWeights[i / 2] / 2
                gaussMean += lower[i] * gaussWeight + upper[i] * gaussWeight
            }
        }
        val value = kronrodMean * width
        val gaussValue = gaussMean * width
        var variation = abs(center * width - value) * kronrodWeights[7] / 2
        for (i in 0..6) {
            variation += (abs(lower[i] * width - value) + abs(upper[i] * width - value)) * kronrodWeights[i] / 2
        }
        var error = abs(value - gaussValue)
        if (variation > 0 && error > 0) error = variation * min(1.0, (200 * error / variation).pow(1.5))
        error = max(error, 50 * Math.ulp(1.0) * absoluteMean * width)
        require(value.isFinite() && error.isFinite()) { "Integral out of range" }
        return Interval(a, b, value, error)
    }

    // Gauss–Kronrod 7/15 coefficients: https://www.netlib.org/quadpack/dqk15.f
    private val nodes = doubleArrayOf(0.9914553711208126, 0.9491079123427585, 0.8648644233597691, 0.7415311855993945, 0.5860872354676911, 0.4058451513773972, 0.2077849550078985)
    private val kronrodWeights = doubleArrayOf(0.02293532201052922, 0.06309209262997855, 0.1047900103222502, 0.1406532597155259, 0.1690047266392679, 0.1903505780647854, 0.2044329400752989, 0.2094821410847278)
    private val gaussWeights = doubleArrayOf(0.1294849661688697, 0.2797053914892767, 0.3818300505051189, 0.4179591836734694)
}
