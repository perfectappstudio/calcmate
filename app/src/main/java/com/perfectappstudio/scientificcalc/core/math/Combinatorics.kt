package com.perfectappstudio.scientificcalc.core.math

object Combinatorics {

    fun factorial(n: Long): Double {
        if (n < 0) return Double.NaN
        if (n > 170) return Double.POSITIVE_INFINITY
        var result = 1.0
        for (i in 2..n) {
            result *= i
        }
        return result
    }

    fun nPr(n: Long, r: Long): Double {
        if (n < 0 || r < 0 || r > n) return Double.NaN
        if (r == 0L) return 1.0
        var result = 1.0
        for (i in (n - r + 1)..n) {
            result *= i
            if (!result.isFinite()) return Double.POSITIVE_INFINITY
        }
        return result
    }

    fun nCr(n: Long, r: Long): Double {
        if (n < 0 || r < 0 || r > n) return Double.NaN
        val rEffective = minOf(r, n - r)
        var result = 1.0
        for (i in 0 until rEffective) {
            result *= (n - i).toDouble() / (i + 1).toDouble()
            if (!result.isFinite()) return Double.POSITIVE_INFINITY
        }
        return result
    }
}
