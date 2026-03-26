package com.calcmate.scientificcalculator.core.model

data class VectorState(
    val vctA: DoubleArray? = null,
    val vctB: DoubleArray? = null,
    val vctC: DoubleArray? = null,
    val vctAns: DoubleArray? = null,
    val editingVector: Char? = null,
    val expression: String = "",
    val error: String? = null,
    // Editor fields
    val dimA: Int = 2,
    val dimB: Int = 2,
    val dimC: Int = 2,
    val cellsA: Array<String> = Array(3) { "" },
    val cellsB: Array<String> = Array(3) { "" },
    val cellsC: Array<String> = Array(3) { "" },
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VectorState) return false
        return contentEquals(vctA, other.vctA) &&
            contentEquals(vctB, other.vctB) &&
            contentEquals(vctC, other.vctC) &&
            contentEquals(vctAns, other.vctAns) &&
            editingVector == other.editingVector &&
            expression == other.expression && error == other.error &&
            dimA == other.dimA && dimB == other.dimB && dimC == other.dimC &&
            cellsA.contentEquals(other.cellsA) &&
            cellsB.contentEquals(other.cellsB) &&
            cellsC.contentEquals(other.cellsC)
    }

    override fun hashCode(): Int {
        var h = vctA?.contentHashCode() ?: 0
        h = 31 * h + (vctB?.contentHashCode() ?: 0)
        h = 31 * h + (vctC?.contentHashCode() ?: 0)
        h = 31 * h + (vctAns?.contentHashCode() ?: 0)
        h = 31 * h + (editingVector?.hashCode() ?: 0)
        h = 31 * h + expression.hashCode()
        h = 31 * h + (error?.hashCode() ?: 0)
        h = 31 * h + dimA + dimB + dimC
        h = 31 * h + cellsA.contentHashCode()
        h = 31 * h + cellsB.contentHashCode()
        h = 31 * h + cellsC.contentHashCode()
        return h
    }

    private fun contentEquals(a: DoubleArray?, b: DoubleArray?): Boolean {
        if (a === b) return true
        if (a == null || b == null) return false
        return a.contentEquals(b)
    }
}
