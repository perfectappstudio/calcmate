package com.calcmate.scientificcalculator.core.model

data class MatrixData(
    val data: Array<DoubleArray>,
    val rows: Int,
    val cols: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MatrixData) return false
        return rows == other.rows && cols == other.cols &&
            data.size == other.data.size &&
            data.indices.all { data[it].contentEquals(other.data[it]) }
    }

    override fun hashCode(): Int {
        var h = rows
        h = 31 * h + cols
        for (row in data) h = 31 * h + row.contentHashCode()
        return h
    }
}

data class MatrixState(
    val matA: MatrixData? = null,
    val matB: MatrixData? = null,
    val matC: MatrixData? = null,
    val matAns: MatrixData? = null,
    val editingMatrix: Char? = null,
    val expression: String = "",
    val error: String? = null,
    // Editor fields: dimension choices and cell text values per matrix
    val dimRowsA: Int = 2,
    val dimColsA: Int = 2,
    val dimRowsB: Int = 2,
    val dimColsB: Int = 2,
    val dimRowsC: Int = 2,
    val dimColsC: Int = 2,
    val cellsA: Array<Array<String>> = Array(3) { Array(3) { "" } },
    val cellsB: Array<Array<String>> = Array(3) { Array(3) { "" } },
    val cellsC: Array<Array<String>> = Array(3) { Array(3) { "" } },
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MatrixState) return false
        return matA == other.matA && matB == other.matB && matC == other.matC &&
            matAns == other.matAns && editingMatrix == other.editingMatrix &&
            expression == other.expression && error == other.error &&
            dimRowsA == other.dimRowsA && dimColsA == other.dimColsA &&
            dimRowsB == other.dimRowsB && dimColsB == other.dimColsB &&
            dimRowsC == other.dimRowsC && dimColsC == other.dimColsC &&
            cellsA.deepEquals(other.cellsA) &&
            cellsB.deepEquals(other.cellsB) &&
            cellsC.deepEquals(other.cellsC)
    }

    override fun hashCode(): Int {
        var h = matA.hashCode()
        h = 31 * h + matB.hashCode()
        h = 31 * h + matC.hashCode()
        h = 31 * h + matAns.hashCode()
        h = 31 * h + (editingMatrix?.hashCode() ?: 0)
        h = 31 * h + expression.hashCode()
        h = 31 * h + (error?.hashCode() ?: 0)
        h = 31 * h + dimRowsA + dimColsA + dimRowsB + dimColsB + dimRowsC + dimColsC
        h = 31 * h + cellsA.contentDeepHashCode()
        h = 31 * h + cellsB.contentDeepHashCode()
        h = 31 * h + cellsC.contentDeepHashCode()
        return h
    }
}

private fun Array<Array<String>>.deepEquals(other: Array<Array<String>>): Boolean {
    if (size != other.size) return false
    return indices.all { this[it].contentEquals(other[it]) }
}
