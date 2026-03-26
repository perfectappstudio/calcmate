package com.calcmate.scientificcalculator.feature.matrix

import androidx.lifecycle.ViewModel
import com.calcmate.scientificcalculator.core.math.MatrixEngine
import com.calcmate.scientificcalculator.core.model.MatrixData
import com.calcmate.scientificcalculator.core.model.MatrixState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MatrixViewModel : ViewModel() {

    private val _state = MutableStateFlow(MatrixState())
    val state: StateFlow<MatrixState> = _state.asStateFlow()

    // ---------------------------------------------------------------
    // Editing selection
    // ---------------------------------------------------------------

    fun selectMatrix(matrix: Char) {
        _state.update { it.copy(editingMatrix = matrix, error = null) }
    }

    // ---------------------------------------------------------------
    // Dimension changes
    // ---------------------------------------------------------------

    fun setDimensions(matrix: Char, rows: Int, cols: Int) {
        _state.update { s ->
            when (matrix) {
                'A' -> s.copy(dimRowsA = rows, dimColsA = cols)
                'B' -> s.copy(dimRowsB = rows, dimColsB = cols)
                'C' -> s.copy(dimRowsC = rows, dimColsC = cols)
                else -> s
            }
        }
    }

    // ---------------------------------------------------------------
    // Cell editing
    // ---------------------------------------------------------------

    fun setCell(matrix: Char, row: Int, col: Int, value: String) {
        _state.update { s ->
            when (matrix) {
                'A' -> {
                    val updated = s.cellsA.map { it.copyOf() }.toTypedArray()
                    updated[row][col] = value
                    s.copy(cellsA = updated)
                }
                'B' -> {
                    val updated = s.cellsB.map { it.copyOf() }.toTypedArray()
                    updated[row][col] = value
                    s.copy(cellsB = updated)
                }
                'C' -> {
                    val updated = s.cellsC.map { it.copyOf() }.toTypedArray()
                    updated[row][col] = value
                    s.copy(cellsC = updated)
                }
                else -> s
            }
        }
    }

    /** Store the currently edited matrix data from the cell fields. */
    fun storeMatrix(matrix: Char) {
        val s = _state.value
        try {
            val data = buildMatrixData(matrix, s)
            _state.update { current ->
                when (matrix) {
                    'A' -> current.copy(matA = data, error = null)
                    'B' -> current.copy(matB = data, error = null)
                    'C' -> current.copy(matC = data, error = null)
                    else -> current
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Invalid number in Mat$matrix: ${e.message}") }
        }
    }

    // ---------------------------------------------------------------
    // Expression
    // ---------------------------------------------------------------

    fun onExpressionChange(expr: String) {
        _state.update { it.copy(expression = expr, error = null) }
    }

    fun appendToExpression(text: String) {
        _state.update { it.copy(expression = it.expression + text, error = null) }
    }

    fun clearExpression() {
        _state.update { it.copy(expression = "", error = null, matAns = null) }
    }

    // ---------------------------------------------------------------
    // Evaluate
    // ---------------------------------------------------------------

    fun evaluate() {
        val s = _state.value
        val expr = s.expression.trim()
        if (expr.isEmpty()) {
            _state.update { it.copy(error = "Enter an expression") }
            return
        }

        try {
            val result = evaluateExpression(expr, s)
            _state.update { it.copy(matAns = result, error = null) }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message ?: "Evaluation error", matAns = null) }
        }
    }

    // ---------------------------------------------------------------
    // Expression parser
    // ---------------------------------------------------------------

    private fun evaluateExpression(expr: String, s: MatrixState): MatrixData {
        // Unary operations: Det MatA, Trn MatA, MatA⁻¹, MatA², MatA³, Abs MatA
        val detMatch = Regex("^Det\\s+Mat([A-C])$", RegexOption.IGNORE_CASE).find(expr)
        if (detMatch != null) {
            val m = resolveMatrix(detMatch.groupValues[1][0], s)
            val det = MatrixEngine.determinant(m.data)
            return MatrixData(arrayOf(doubleArrayOf(det)), 1, 1)
        }

        val trnMatch = Regex("^Trn\\s+Mat([A-C])$", RegexOption.IGNORE_CASE).find(expr)
        if (trnMatch != null) {
            val m = resolveMatrix(trnMatch.groupValues[1][0], s)
            val result = MatrixEngine.transpose(m.data)
            return MatrixData(result, result.size, result[0].size)
        }

        val invMatch = Regex("^Mat([A-C])⁻¹$").find(expr)
        if (invMatch != null) {
            val m = resolveMatrix(invMatch.groupValues[1][0], s)
            val result = MatrixEngine.inverse(m.data)
            return MatrixData(result, result.size, result[0].size)
        }

        val sqMatch = Regex("^Mat([A-C])²$").find(expr)
        if (sqMatch != null) {
            val m = resolveMatrix(sqMatch.groupValues[1][0], s)
            val result = MatrixEngine.square(m.data)
            return MatrixData(result, result.size, result[0].size)
        }

        val cubeMatch = Regex("^Mat([A-C])³$").find(expr)
        if (cubeMatch != null) {
            val m = resolveMatrix(cubeMatch.groupValues[1][0], s)
            val result = MatrixEngine.cube(m.data)
            return MatrixData(result, result.size, result[0].size)
        }

        val absMatch = Regex("^Abs\\s+Mat([A-C])$", RegexOption.IGNORE_CASE).find(expr)
        if (absMatch != null) {
            val m = resolveMatrix(absMatch.groupValues[1][0], s)
            val result = MatrixEngine.absElements(m.data)
            return MatrixData(result, result.size, result[0].size)
        }

        // Binary operations: MatA + MatB, MatA - MatB, MatA × MatB
        val binMatch = Regex("^Mat([A-C])\\s*([+\\-×])\\s*Mat([A-C])$").find(expr)
        if (binMatch != null) {
            val left = resolveMatrix(binMatch.groupValues[1][0], s)
            val op = binMatch.groupValues[2]
            val right = resolveMatrix(binMatch.groupValues[3][0], s)
            val result = when (op) {
                "+" -> MatrixEngine.add(left.data, right.data)
                "-" -> MatrixEngine.subtract(left.data, right.data)
                "×" -> MatrixEngine.multiply(left.data, right.data)
                else -> throw IllegalArgumentException("Unknown operator: $op")
            }
            return MatrixData(result, result.size, result[0].size)
        }

        // Scalar multiplication: 3 × MatA or 3 * MatA
        val scalarLeft = Regex("^(-?[\\d.]+)\\s*[×*]\\s*Mat([A-C])$").find(expr)
        if (scalarLeft != null) {
            val scalar = scalarLeft.groupValues[1].toDouble()
            val m = resolveMatrix(scalarLeft.groupValues[2][0], s)
            val result = MatrixEngine.scalarMultiply(scalar, m.data)
            return MatrixData(result, result.size, result[0].size)
        }

        throw IllegalArgumentException("Unrecognized expression: $expr")
    }

    private fun resolveMatrix(name: Char, s: MatrixState): MatrixData {
        return when (name.uppercaseChar()) {
            'A' -> s.matA ?: throw IllegalArgumentException("MatA is not defined")
            'B' -> s.matB ?: throw IllegalArgumentException("MatB is not defined")
            'C' -> s.matC ?: throw IllegalArgumentException("MatC is not defined")
            else -> throw IllegalArgumentException("Unknown matrix: Mat$name")
        }
    }

    private fun buildMatrixData(matrix: Char, s: MatrixState): MatrixData {
        val (rows, cols, cells) = when (matrix) {
            'A' -> Triple(s.dimRowsA, s.dimColsA, s.cellsA)
            'B' -> Triple(s.dimRowsB, s.dimColsB, s.cellsB)
            'C' -> Triple(s.dimRowsC, s.dimColsC, s.cellsC)
            else -> throw IllegalArgumentException("Unknown matrix: $matrix")
        }
        val data = Array(rows) { r ->
            DoubleArray(cols) { c ->
                val text = cells[r][c]
                if (text.isBlank()) 0.0 else text.toDouble()
            }
        }
        return MatrixData(data, rows, cols)
    }
}
