package com.perfectappstudio.scientificcalc.core.math

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

// --- Linear ---

sealed class LinearResult {
    data class Solution(val x: Double) : LinearResult()
    object NoSolution : LinearResult()
    object InfiniteSolutions : LinearResult()
}

// --- Quadratic ---

sealed class QuadraticResult {
    data class TwoRealRoots(
        val x1: Double,
        val x2: Double,
        val discriminant: Double,
        val steps: List<String>,
    ) : QuadraticResult()

    data class OneRepeatedRoot(
        val x: Double,
        val discriminant: Double,
        val steps: List<String>,
    ) : QuadraticResult()

    data class ComplexRoots(
        val realPart: Double,
        val imaginaryPart: Double,
        val discriminant: Double,
        val steps: List<String>,
    ) : QuadraticResult()

    /** Degenerates to a linear equation when a == 0. */
    data class DegenerateLinear(val linearResult: LinearResult) : QuadraticResult()
}

// --- Cubic ---

sealed class CubicResult {
    data class ThreeRealRoots(
        val x1: Double,
        val x2: Double,
        val x3: Double,
        val steps: List<String>,
    ) : CubicResult()

    data class OneRealTwoComplex(
        val x1: Double,
        val realPart: Double,
        val imagPart: Double,
        val steps: List<String>,
    ) : CubicResult()

    /** Degenerates to a quadratic equation when a == 0. */
    data class DegenerateQuadratic(val quadraticResult: QuadraticResult) : CubicResult()
}

// --- System 2x2 ---

sealed class SystemResult {
    data class Solution(val x: Double, val y: Double) : SystemResult()
    object NoSolution : SystemResult()
    object InfiniteSolutions : SystemResult()
}

// --- System 3x3 ---

sealed class SystemResult3x3 {
    data class Solution(val x: Double, val y: Double, val z: Double) : SystemResult3x3()
    object NoSolution : SystemResult3x3()
    object InfiniteSolutions : SystemResult3x3()
}

// --- Solver object ---

object EquationSolver {

    private const val EPS = 1e-12

    /**
     * Solve linear equation: ax + b = 0
     */
    fun solveLinear(a: Double, b: Double): LinearResult {
        return when {
            abs(a) < EPS && abs(b) < EPS -> LinearResult.InfiniteSolutions
            abs(a) < EPS -> LinearResult.NoSolution
            else -> LinearResult.Solution(x = -b / a)
        }
    }

    /**
     * Solve quadratic equation: ax^2 + bx + c = 0
     */
    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticResult {
        // Degenerate case: a == 0 => linear
        if (abs(a) < EPS) {
            return QuadraticResult.DegenerateLinear(solveLinear(b, c))
        }

        val discriminant = b * b - 4.0 * a * c

        val steps = mutableListOf<String>()
        steps += "Equation: ${fmt(a)}x² + ${fmt(b)}x + ${fmt(c)} = 0"
        steps += "\u0394 = b\u00B2 - 4ac = ${fmt(b * b)} - ${fmt(4.0 * a * c)} = ${fmt(discriminant)}"
        steps += "x = (-b \u00B1 \u221A\u0394) / 2a"

        return when {
            discriminant > EPS -> {
                val sqrtD = sqrt(discriminant)
                val x1 = (-b + sqrtD) / (2.0 * a)
                val x2 = (-b - sqrtD) / (2.0 * a)
                steps += "x\u2081 = (${fmt(-b)} + ${fmt(sqrtD)}) / ${fmt(2.0 * a)} = ${fmt(x1)}"
                steps += "x\u2082 = (${fmt(-b)} - ${fmt(sqrtD)}) / ${fmt(2.0 * a)} = ${fmt(x2)}"
                QuadraticResult.TwoRealRoots(x1, x2, discriminant, steps)
            }
            abs(discriminant) <= EPS -> {
                val x = -b / (2.0 * a)
                steps += "x = ${fmt(-b)} / ${fmt(2.0 * a)} = ${fmt(x)}"
                QuadraticResult.OneRepeatedRoot(x, 0.0, steps)
            }
            else -> {
                val realPart = -b / (2.0 * a)
                val imaginaryPart = sqrt(-discriminant) / (2.0 * a)
                steps += "x = ${fmt(realPart)} \u00B1 ${fmt(abs(imaginaryPart))}i"
                QuadraticResult.ComplexRoots(realPart, abs(imaginaryPart), discriminant, steps)
            }
        }
    }

    /**
     * Solve cubic equation: ax^3 + bx^2 + cx + d = 0
     * Uses Cardano's formula via depressed cubic substitution.
     */
    fun solveCubic(a: Double, b: Double, c: Double, d: Double): CubicResult {
        // Degenerate case: a == 0 => quadratic
        if (abs(a) < EPS) {
            return CubicResult.DegenerateQuadratic(solveQuadratic(b, c, d))
        }

        val steps = mutableListOf<String>()
        steps += "Equation: ${fmt(a)}x\u00B3 + ${fmt(b)}x\u00B2 + ${fmt(c)}x + ${fmt(d)} = 0"

        // Normalize to x^3 + px^2 + qx + r = 0
        val p = b / a
        val q = c / a
        val r = d / a
        steps += "Divide by a: x\u00B3 + ${fmt(p)}x\u00B2 + ${fmt(q)}x + ${fmt(r)} = 0"

        // Depressed cubic via substitution x = t - p/3
        // t^3 + pt + qDep = 0 where:
        val pDep = q - p * p / 3.0
        val qDep = r - p * q / 3.0 + 2.0 * p * p * p / 27.0
        steps += "Depressed cubic (x = t - ${fmt(p / 3.0)}):"
        steps += "  t\u00B3 + ${fmt(pDep)}t + ${fmt(qDep)} = 0"

        // Discriminant: D = (qDep/2)^2 + (pDep/3)^3
        val disc = (qDep / 2.0).pow(2) + (pDep / 3.0).pow(3)
        steps += "\u0394 = (q/2)\u00B2 + (p/3)\u00B3 = ${fmt(disc)}"

        val shift = -p / 3.0

        return when {
            disc > EPS -> {
                // One real root, two complex conjugates
                steps += "\u0394 > 0: one real root, two complex conjugate roots"
                val sqrtDisc = sqrt(disc)
                val u = cbrt(-qDep / 2.0 + sqrtDisc)
                val v = cbrt(-qDep / 2.0 - sqrtDisc)
                val x1 = u + v + shift
                steps += "u = \u00B3\u221A(${fmt(-qDep / 2.0 + sqrtDisc)}) = ${fmt(u)}"
                steps += "v = \u00B3\u221A(${fmt(-qDep / 2.0 - sqrtDisc)}) = ${fmt(v)}"
                steps += "x\u2081 = u + v - p/3 = ${fmt(x1)}"

                val realPart = -(u + v) / 2.0 + shift
                val imagPart = abs((u - v) * sqrt(3.0) / 2.0)
                steps += "x\u2082,\u2083 = ${fmt(realPart)} \u00B1 ${fmt(imagPart)}i"

                CubicResult.OneRealTwoComplex(x1, realPart, imagPart, steps)
            }
            abs(disc) <= EPS -> {
                // All roots real, at least two equal
                steps += "\u0394 = 0: repeated roots"
                if (abs(pDep) < EPS && abs(qDep) < EPS) {
                    // Triple root
                    val x1 = shift
                    steps += "Triple root: x = ${fmt(x1)}"
                    CubicResult.ThreeRealRoots(x1, x1, x1, steps)
                } else {
                    val u = cbrt(-qDep / 2.0)
                    val x1 = 2.0 * u + shift
                    val x2 = -u + shift
                    steps += "x\u2081 = ${fmt(x1)}"
                    steps += "x\u2082 = x\u2083 = ${fmt(x2)}"
                    val roots = listOf(x1, x2, x2).sortedDescending()
                    CubicResult.ThreeRealRoots(roots[0], roots[1], roots[2], steps)
                }
            }
            else -> {
                // Three distinct real roots — use trigonometric method
                steps += "\u0394 < 0: three distinct real roots"
                val rTrig = sqrt(-(pDep / 3.0).pow(3))
                val theta = acos(-qDep / (2.0 * rTrig))
                val m = 2.0 * cbrt(rTrig)
                val x1 = m * cos(theta / 3.0) + shift
                val x2 = m * cos((theta + 2.0 * Math.PI) / 3.0) + shift
                val x3 = m * cos((theta + 4.0 * Math.PI) / 3.0) + shift
                val roots = listOf(x1, x2, x3).sortedDescending()
                steps += "x\u2081 = ${fmt(roots[0])}"
                steps += "x\u2082 = ${fmt(roots[1])}"
                steps += "x\u2083 = ${fmt(roots[2])}"
                CubicResult.ThreeRealRoots(roots[0], roots[1], roots[2], steps)
            }
        }
    }

    /**
     * Solve 2x2 system using Cramer's rule:
     *   a1*x + b1*y = c1
     *   a2*x + b2*y = c2
     */
    fun solveSystem2x2(
        a1: Double, b1: Double, c1: Double,
        a2: Double, b2: Double, c2: Double,
    ): SystemResult {
        val det = a1 * b2 - a2 * b1
        if (abs(det) < EPS) {
            // Check consistency: if both cross-determinants are also zero => infinite
            val detX = c1 * b2 - c2 * b1
            val detY = a1 * c2 - a2 * c1
            return if (abs(detX) < EPS && abs(detY) < EPS) {
                SystemResult.InfiniteSolutions
            } else {
                SystemResult.NoSolution
            }
        }
        val x = (c1 * b2 - c2 * b1) / det
        val y = (a1 * c2 - a2 * c1) / det
        return SystemResult.Solution(x, y)
    }

    /**
     * Solve 3x3 system via Gaussian elimination with partial pivoting.
     *
     * @param coefficients 3x4 augmented matrix [A|b]:
     *   row 0: [a11, a12, a13, b1]
     *   row 1: [a21, a22, a23, b2]
     *   row 2: [a31, a32, a33, b3]
     */
    fun solveSystem3x3(coefficients: Array<DoubleArray>): SystemResult3x3 {
        // Deep copy so we don't mutate the caller's array
        val m = Array(3) { coefficients[it].copyOf() }

        // Forward elimination with partial pivoting
        for (col in 0 until 3) {
            // Find pivot
            var maxRow = col
            var maxVal = abs(m[col][col])
            for (row in col + 1 until 3) {
                if (abs(m[row][col]) > maxVal) {
                    maxVal = abs(m[row][col])
                    maxRow = row
                }
            }

            // Swap rows
            if (maxRow != col) {
                val tmp = m[col]
                m[col] = m[maxRow]
                m[maxRow] = tmp
            }

            // Check for singular / degenerate
            if (abs(m[col][col]) < EPS) {
                return classifyDegenerate3x3(m)
            }

            // Eliminate below
            for (row in col + 1 until 3) {
                val factor = m[row][col] / m[col][col]
                for (j in col until 4) {
                    m[row][j] -= factor * m[col][j]
                }
            }
        }

        // Check last pivot
        if (abs(m[2][2]) < EPS) {
            return classifyDegenerate3x3(m)
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

        return SystemResult3x3.Solution(result[0], result[1], result[2])
    }

    /** After Gaussian elimination finds a zero pivot, decide if it is no-solution or infinite. */
    private fun classifyDegenerate3x3(m: Array<DoubleArray>): SystemResult3x3 {
        // If any row is [0 0 0 | k] with k != 0 => inconsistent
        for (row in 0 until 3) {
            val allCoeffZero = abs(m[row][0]) < EPS && abs(m[row][1]) < EPS && abs(m[row][2]) < EPS
            if (allCoeffZero && abs(m[row][3]) > EPS) {
                return SystemResult3x3.NoSolution
            }
        }
        return SystemResult3x3.InfiniteSolutions
    }

    /** Format a double to 4 decimal places, stripping trailing zeros. */
    private fun fmt(value: Double): String {
        if (value == -0.0) return "0"
        val s = "%.4f".format(value)
        return s.trimEnd('0').trimEnd('.')
    }
}
