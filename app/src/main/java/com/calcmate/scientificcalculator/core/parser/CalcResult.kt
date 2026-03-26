package com.calcmate.scientificcalculator.core.parser

enum class NumberBase { BIN, OCT, DEC, HEX }

sealed class CalcResult {
    data class RealResult(val value: Double) : CalcResult()
    data class ComplexResult(val real: Double, val imag: Double) : CalcResult()
    data class IntegerResult(val value: Long, val base: NumberBase = NumberBase.DEC) : CalcResult()
    data class MatrixResult(val data: Array<DoubleArray>, val rows: Int, val cols: Int) : CalcResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MatrixResult) return false
            return rows == other.rows && cols == other.cols && data.contentDeepEquals(other.data)
        }
        override fun hashCode(): Int = 31 * (31 * data.contentDeepHashCode() + rows) + cols
    }
    data class VectorResult(val components: DoubleArray) : CalcResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is VectorResult) return false
            return components.contentEquals(other.components)
        }
        override fun hashCode(): Int = components.contentHashCode()
    }

    fun toDouble(): Double = when (this) {
        is RealResult -> value
        is IntegerResult -> value.toDouble()
        else -> throw IllegalStateException("Cannot convert ${this::class.simpleName} to Double")
    }

    fun toDisplayString(formatter: Formatter): String = when (this) {
        is RealResult -> formatter.format(value)
        is IntegerResult -> formatter.format(value.toDouble())
        is ComplexResult -> {
            val realPart = formatter.format(real)
            val imagPart = formatter.format(kotlin.math.abs(imag))
            when {
                imag == 0.0 -> realPart
                real == 0.0 && imag == 1.0 -> "i"
                real == 0.0 && imag == -1.0 -> "-i"
                real == 0.0 -> "${formatter.format(imag)}i"
                imag > 0 -> "$realPart+${imagPart}i"
                else -> "$realPart-${imagPart}i"
            }
        }
        is MatrixResult -> {
            data.joinToString("; ", "[", "]") { row ->
                row.joinToString(", ") { formatter.format(it) }
            }
        }
        is VectorResult -> {
            components.joinToString(", ", "(", ")") { formatter.format(it) }
        }
    }
}
