package com.perfectappstudio.scientificcalc.core.math

import kotlin.math.sqrt

/**
 * Operations on 1D arrays representing 2D or 3D vectors.
 */
object VectorEngine {

    // ---------------------------------------------------------------
    // Element-wise operations
    // ---------------------------------------------------------------

    /** Element-wise addition. Both vectors must have the same dimension. */
    fun add(a: DoubleArray, b: DoubleArray): DoubleArray {
        requireSameDimension(a, b, "add")
        return DoubleArray(a.size) { i -> a[i] + b[i] }
    }

    /** Element-wise subtraction. Both vectors must have the same dimension. */
    fun subtract(a: DoubleArray, b: DoubleArray): DoubleArray {
        requireSameDimension(a, b, "subtract")
        return DoubleArray(a.size) { i -> a[i] - b[i] }
    }

    /** Multiply every component by a scalar. */
    fun scalarMultiply(scalar: Double, v: DoubleArray): DoubleArray {
        requireValid(v, "scalarMultiply")
        return DoubleArray(v.size) { i -> scalar * v[i] }
    }

    // ---------------------------------------------------------------
    // Products
    // ---------------------------------------------------------------

    /** Dot product: sum of element-wise products. Same dimension required. */
    fun dotProduct(a: DoubleArray, b: DoubleArray): Double {
        requireSameDimension(a, b, "dotProduct")
        var sum = 0.0
        for (i in a.indices) {
            sum += a[i] * b[i]
        }
        return sum
    }

    /**
     * Cross product: 3D vectors only.
     * Returns a 3D vector perpendicular to both inputs.
     */
    fun crossProduct(a: DoubleArray, b: DoubleArray): DoubleArray {
        require(a.size == 3 && b.size == 3) {
            "Cross product is only defined for 3D vectors (got ${a.size}D and ${b.size}D)"
        }
        return doubleArrayOf(
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0],
        )
    }

    // ---------------------------------------------------------------
    // Magnitude
    // ---------------------------------------------------------------

    /** Euclidean magnitude (length) of the vector. */
    fun magnitude(v: DoubleArray): Double {
        requireValid(v, "magnitude")
        var sum = 0.0
        for (component in v) {
            sum += component * component
        }
        return sqrt(sum)
    }

    // ---------------------------------------------------------------
    // Validation helpers
    // ---------------------------------------------------------------

    private fun requireValid(v: DoubleArray, op: String) {
        require(v.size in 2..3) {
            "Vector must be 2D or 3D for operation: $op (got ${v.size}D)"
        }
    }

    private fun requireSameDimension(a: DoubleArray, b: DoubleArray, op: String) {
        requireValid(a, op)
        requireValid(b, op)
        require(a.size == b.size) {
            "Vectors must have the same dimension for $op (got ${a.size}D and ${b.size}D)"
        }
    }
}
