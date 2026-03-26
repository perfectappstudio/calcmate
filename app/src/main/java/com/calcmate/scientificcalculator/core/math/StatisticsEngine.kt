package com.calcmate.scientificcalculator.core.math

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

// --- Data classes ---

data class DataPoint(val value: Double, val frequency: Int = 1)
data class DataPair(val x: Double, val y: Double, val frequency: Int = 1)
data class RegressionResult(val a: Double, val b: Double, val r: Double)
data class QuadraticRegressionResult(val a: Double, val b: Double, val c: Double)

// --- Statistics engine ---

object StatisticsEngine {

    // ---------------------------------------------------------------
    // Single-variable (SD mode)
    // ---------------------------------------------------------------

    fun count(data: List<DataPoint>): Int =
        data.sumOf { it.frequency }

    fun sumX(data: List<DataPoint>): Double =
        data.sumOf { it.value * it.frequency }

    fun sumX2(data: List<DataPoint>): Double =
        data.sumOf { it.value * it.value * it.frequency }

    fun meanX(data: List<DataPoint>): Double {
        val n = count(data)
        require(n > 0) { "Data set must not be empty" }
        return sumX(data) / n
    }

    fun populationStdDevX(data: List<DataPoint>): Double {
        val n = count(data)
        require(n > 0) { "Data set must not be empty" }
        val mean = meanX(data)
        val variance = data.sumOf { (it.value - mean).pow(2) * it.frequency } / n
        return sqrt(variance)
    }

    fun sampleStdDevX(data: List<DataPoint>): Double {
        val n = count(data)
        require(n > 1) { "Sample standard deviation requires at least 2 data points" }
        val mean = meanX(data)
        val variance = data.sumOf { (it.value - mean).pow(2) * it.frequency } / (n - 1)
        return sqrt(variance)
    }

    // ---------------------------------------------------------------
    // Paired-variable (REG mode) — single-variable stats on x and y
    // ---------------------------------------------------------------

    fun countPairs(data: List<DataPair>): Int =
        data.sumOf { it.frequency }

    fun sumXPair(data: List<DataPair>): Double =
        data.sumOf { it.x * it.frequency }

    fun sumX2Pair(data: List<DataPair>): Double =
        data.sumOf { it.x * it.x * it.frequency }

    fun sumY(data: List<DataPair>): Double =
        data.sumOf { it.y * it.frequency }

    fun sumY2(data: List<DataPair>): Double =
        data.sumOf { it.y * it.y * it.frequency }

    fun sumXY(data: List<DataPair>): Double =
        data.sumOf { it.x * it.y * it.frequency }

    fun meanXPair(data: List<DataPair>): Double {
        val n = countPairs(data)
        require(n > 0) { "Data set must not be empty" }
        return sumXPair(data) / n
    }

    fun meanY(data: List<DataPair>): Double {
        val n = countPairs(data)
        require(n > 0) { "Data set must not be empty" }
        return sumY(data) / n
    }

    fun populationStdDevXPair(data: List<DataPair>): Double {
        val n = countPairs(data)
        require(n > 0) { "Data set must not be empty" }
        val mean = meanXPair(data)
        val variance = data.sumOf { (it.x - mean).pow(2) * it.frequency } / n
        return sqrt(variance)
    }

    fun sampleStdDevXPair(data: List<DataPair>): Double {
        val n = countPairs(data)
        require(n > 1) { "Sample standard deviation requires at least 2 data points" }
        val mean = meanXPair(data)
        val variance = data.sumOf { (it.x - mean).pow(2) * it.frequency } / (n - 1)
        return sqrt(variance)
    }

    fun populationStdDevY(data: List<DataPair>): Double {
        val n = countPairs(data)
        require(n > 0) { "Data set must not be empty" }
        val mean = meanY(data)
        val variance = data.sumOf { (it.y - mean).pow(2) * it.frequency } / n
        return sqrt(variance)
    }

    fun sampleStdDevY(data: List<DataPair>): Double {
        val n = countPairs(data)
        require(n > 1) { "Sample standard deviation requires at least 2 data points" }
        val mean = meanY(data)
        val variance = data.sumOf { (it.y - mean).pow(2) * it.frequency } / (n - 1)
        return sqrt(variance)
    }

    // ---------------------------------------------------------------
    // Regression
    // ---------------------------------------------------------------

    /**
     * Linear regression: y = A + Bx
     * Returns coefficients A, B and correlation coefficient r.
     */
    fun linearRegression(data: List<DataPair>): RegressionResult {
        val n = countPairs(data).toDouble()
        require(n >= 2) { "At least 2 data pairs required for regression" }
        val sx = sumXPair(data)
        val sy = sumY(data)
        val sxy = sumXY(data)
        val sx2 = sumX2Pair(data)
        val sy2 = sumY2(data)

        val b = (n * sxy - sx * sy) / (n * sx2 - sx * sx)
        val a = (sy - b * sx) / n
        val r = (n * sxy - sx * sy) /
            sqrt((n * sx2 - sx * sx) * (n * sy2 - sy * sy))

        return RegressionResult(a, b, r)
    }

    /**
     * Logarithmic regression: y = A + B*ln(x)
     * Transform x -> ln(x), then linear regression.
     */
    fun logRegression(data: List<DataPair>): RegressionResult {
        val transformed = data.map { it.copy(x = ln(it.x)) }
        return linearRegression(transformed)
    }

    /**
     * Exponential regression: y = A * e^(Bx)
     * Transform y -> ln(y), then linear regression: ln(y) = ln(A) + Bx.
     */
    fun expRegression(data: List<DataPair>): RegressionResult {
        val transformed = data.map { it.copy(y = ln(it.y)) }
        val result = linearRegression(transformed)
        return RegressionResult(a = exp(result.a), b = result.b, r = result.r)
    }

    /**
     * Power regression: y = A * x^B
     * Transform both: ln(y) = ln(A) + B*ln(x).
     */
    fun powerRegression(data: List<DataPair>): RegressionResult {
        val transformed = data.map { it.copy(x = ln(it.x), y = ln(it.y)) }
        val result = linearRegression(transformed)
        return RegressionResult(a = exp(result.a), b = result.b, r = result.r)
    }

    /**
     * Inverse regression: y = A + B/x
     * Transform x -> 1/x, then linear regression.
     */
    fun inverseRegression(data: List<DataPair>): RegressionResult {
        val transformed = data.map { it.copy(x = 1.0 / it.x) }
        return linearRegression(transformed)
    }

    /**
     * Quadratic regression: y = A + Bx + Cx^2
     * Uses least squares with a 3x3 normal equation system.
     */
    fun quadraticRegression(data: List<DataPair>): QuadraticRegressionResult {
        val n = countPairs(data).toDouble()
        require(n >= 3) { "At least 3 data pairs required for quadratic regression" }

        // Build sums
        var sx = 0.0; var sx2 = 0.0; var sx3 = 0.0; var sx4 = 0.0
        var sy = 0.0; var sxy = 0.0; var sx2y = 0.0

        for (dp in data) {
            val f = dp.frequency.toDouble()
            val x = dp.x; val y = dp.y
            val x2 = x * x; val x3 = x2 * x; val x4 = x3 * x
            sx += x * f
            sx2 += x2 * f
            sx3 += x3 * f
            sx4 += x4 * f
            sy += y * f
            sxy += x * y * f
            sx2y += x2 * y * f
        }

        // Normal equations: [n, Σx, Σx²; Σx, Σx², Σx³; Σx², Σx³, Σx⁴] * [A; B; C] = [Σy; Σxy; Σx²y]
        val matrix = arrayOf(
            doubleArrayOf(n, sx, sx2, sy),
            doubleArrayOf(sx, sx2, sx3, sxy),
            doubleArrayOf(sx2, sx3, sx4, sx2y),
        )

        val result = solveSystem3x3(matrix)
        return QuadraticRegressionResult(a = result[0], b = result[1], c = result[2])
    }

    // ---------------------------------------------------------------
    // Normal distribution
    // ---------------------------------------------------------------

    /**
     * P(t) = integral from -∞ to t of the standard normal distribution.
     * Uses the error function: P(t) = 0.5 * (1 + erf(t / √2))
     */
    fun normalP(t: Double): Double =
        0.5 * (1.0 + erf(t / sqrt(2.0)))

    /**
     * Q(t) = integral from 0 to t = P(t) - 0.5
     */
    fun normalQ(t: Double): Double =
        normalP(t) - 0.5

    /**
     * R(t) = integral from t to ∞ = 1 - P(t)
     */
    fun normalR(t: Double): Double =
        1.0 - normalP(t)

    /**
     * Standardize: t = (x - mean) / stdDev
     */
    fun standardize(x: Double, mean: Double, stdDev: Double): Double {
        require(stdDev > 0) { "Standard deviation must be positive" }
        return (x - mean) / stdDev
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    /**
     * Error function approximation (Abramowitz and Stegun, formula 7.1.26).
     * Maximum error: 1.5e-7.
     */
    private fun erf(x: Double): Double {
        val a1 = 0.254829592
        val a2 = -0.284496736
        val a3 = 1.421413741
        val a4 = -1.453152027
        val a5 = 1.061405429
        val p = 0.3275911

        val sign = if (x >= 0) 1.0 else -1.0
        val absX = abs(x)

        val t = 1.0 / (1.0 + p * absX)
        val y = 1.0 - ((((a5 * t + a4) * t + a3) * t + a2) * t + a1) * t * exp(-absX * absX)

        return sign * y
    }

    /**
     * Solve 3x3 augmented matrix via Gaussian elimination.
     * Returns array of 3 solutions.
     */
    private fun solveSystem3x3(coefficients: Array<DoubleArray>): DoubleArray {
        val m = Array(3) { coefficients[it].copyOf() }

        for (col in 0 until 3) {
            // Partial pivoting
            var maxRow = col
            var maxVal = abs(m[col][col])
            for (row in col + 1 until 3) {
                if (abs(m[row][col]) > maxVal) {
                    maxVal = abs(m[row][col])
                    maxRow = row
                }
            }
            if (maxRow != col) {
                val tmp = m[col]; m[col] = m[maxRow]; m[maxRow] = tmp
            }

            // Eliminate below
            for (row in col + 1 until 3) {
                val factor = m[row][col] / m[col][col]
                for (j in col until 4) {
                    m[row][j] -= factor * m[col][j]
                }
            }
        }

        // Back substitution
        val result = DoubleArray(3)
        for (i in 2 downTo 0) {
            var sum = m[i][3]
            for (j in i + 1 until 3) {
                sum -= m[i][j] * result[j]
            }
            result[i] = sum / m[i][i]
        }
        return result
    }
}
