package com.perfectappstudio.scientificcalc.core.math

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Operations on 2D arrays (up to 3x3 matrices).
 * All matrices are represented as Array<DoubleArray> in row-major order.
 */
object MatrixEngine {

    private const val EPS = 1e-12

    // ---------------------------------------------------------------
    // Element-wise operations
    // ---------------------------------------------------------------

    /** Element-wise addition. Both matrices must have the same dimensions. */
    fun add(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        requireSameDimensions(a, b, "add")
        return Array(a.size) { r ->
            DoubleArray(a[0].size) { c -> a[r][c] + b[r][c] }
        }
    }

    /** Element-wise subtraction. Both matrices must have the same dimensions. */
    fun subtract(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        requireSameDimensions(a, b, "subtract")
        return Array(a.size) { r ->
            DoubleArray(a[0].size) { c -> a[r][c] - b[r][c] }
        }
    }

    /** Absolute value of each element. */
    fun absElements(m: Array<DoubleArray>): Array<DoubleArray> {
        requireNonEmpty(m, "absElements")
        return Array(m.size) { r ->
            DoubleArray(m[0].size) { c -> abs(m[r][c]) }
        }
    }

    // ---------------------------------------------------------------
    // Scalar and matrix multiplication
    // ---------------------------------------------------------------

    /** Multiply every element by a scalar. */
    fun scalarMultiply(scalar: Double, m: Array<DoubleArray>): Array<DoubleArray> {
        requireNonEmpty(m, "scalarMultiply")
        return Array(m.size) { r ->
            DoubleArray(m[0].size) { c -> scalar * m[r][c] }
        }
    }

    /**
     * Standard matrix multiplication (A x B).
     * Requires: A.cols == B.rows.
     */
    fun multiply(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        requireNonEmpty(a, "multiply (left)")
        requireNonEmpty(b, "multiply (right)")
        val aCols = a[0].size
        val bRows = b.size
        require(aCols == bRows) {
            "Cannot multiply: left matrix has $aCols columns but right matrix has $bRows rows"
        }
        val aRows = a.size
        val bCols = b[0].size
        return Array(aRows) { r ->
            DoubleArray(bCols) { c ->
                var sum = 0.0
                for (k in 0 until aCols) {
                    sum += a[r][k] * b[k][c]
                }
                sum
            }
        }
    }

    /** Square: M * M. Matrix must be square. */
    fun square(m: Array<DoubleArray>): Array<DoubleArray> {
        requireSquare(m, "square")
        return multiply(m, m)
    }

    /** Cube: M * M * M. Matrix must be square. */
    fun cube(m: Array<DoubleArray>): Array<DoubleArray> {
        requireSquare(m, "cube")
        return multiply(multiply(m, m), m)
    }

    // ---------------------------------------------------------------
    // Transpose
    // ---------------------------------------------------------------

    /** Transpose: swap rows and columns. */
    fun transpose(m: Array<DoubleArray>): Array<DoubleArray> {
        requireNonEmpty(m, "transpose")
        val rows = m.size
        val cols = m[0].size
        return Array(cols) { c ->
            DoubleArray(rows) { r -> m[r][c] }
        }
    }

    // ---------------------------------------------------------------
    // Determinant (cofactor expansion)
    // ---------------------------------------------------------------

    /** Determinant using cofactor expansion. Matrix must be square. */
    fun determinant(m: Array<DoubleArray>): Double {
        requireSquare(m, "determinant")
        val n = m.size
        return when (n) {
            1 -> m[0][0]
            2 -> m[0][0] * m[1][1] - m[0][1] * m[1][0]
            3 -> {
                m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1]) -
                    m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0]) +
                    m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0])
            }
            else -> throw IllegalArgumentException("Determinant only supported for up to 3x3 matrices")
        }
    }

    // ---------------------------------------------------------------
    // Inverse (adjugate / determinant method)
    // ---------------------------------------------------------------

    /** Inverse via adjugate/determinant. Square, non-singular matrices only. */
    fun inverse(m: Array<DoubleArray>): Array<DoubleArray> {
        requireSquare(m, "inverse")
        val det = determinant(m)
        require(abs(det) > EPS) { "Matrix is singular (determinant ≈ 0), cannot invert" }
        val n = m.size
        return when (n) {
            1 -> arrayOf(doubleArrayOf(1.0 / det))
            2 -> {
                val invDet = 1.0 / det
                arrayOf(
                    doubleArrayOf(m[1][1] * invDet, -m[0][1] * invDet),
                    doubleArrayOf(-m[1][0] * invDet, m[0][0] * invDet),
                )
            }
            3 -> {
                // Cofactor matrix, then transpose (= adjugate), then divide by det
                val cofactor = Array(3) { DoubleArray(3) }
                for (r in 0 until 3) {
                    for (c in 0 until 3) {
                        val minor = minor(m, r, c)
                        val sign = if ((r + c) % 2 == 0) 1.0 else -1.0
                        cofactor[r][c] = sign * minor
                    }
                }
                val adj = transpose(cofactor)
                scalarMultiply(1.0 / det, adj)
            }
            else -> throw IllegalArgumentException("Inverse only supported for up to 3x3 matrices")
        }
    }

    /** Compute the minor (determinant of sub-matrix excluding row r, col c). */
    private fun minor(m: Array<DoubleArray>, excludeRow: Int, excludeCol: Int): Double {
        val sub = Array(m.size - 1) { DoubleArray(m.size - 1) }
        var sr = 0
        for (r in m.indices) {
            if (r == excludeRow) continue
            var sc = 0
            for (c in m[0].indices) {
                if (c == excludeCol) continue
                sub[sr][sc] = m[r][c]
                sc++
            }
            sr++
        }
        return determinant(sub)
    }

    // ---------------------------------------------------------------
    // Validation helpers
    // ---------------------------------------------------------------

    private fun requireNonEmpty(m: Array<DoubleArray>, op: String) {
        require(m.isNotEmpty() && m[0].isNotEmpty()) {
            "Matrix must not be empty for operation: $op"
        }
    }

    private fun requireSquare(m: Array<DoubleArray>, op: String) {
        requireNonEmpty(m, op)
        require(m.size == m[0].size) {
            "Matrix must be square for operation: $op (got ${m.size}x${m[0].size})"
        }
    }

    private fun requireSameDimensions(a: Array<DoubleArray>, b: Array<DoubleArray>, op: String) {
        requireNonEmpty(a, op)
        requireNonEmpty(b, op)
        require(a.size == b.size && a[0].size == b[0].size) {
            "Matrices must have the same dimensions for $op " +
                "(got ${a.size}x${a[0].size} and ${b.size}x${b[0].size})"
        }
    }
}
