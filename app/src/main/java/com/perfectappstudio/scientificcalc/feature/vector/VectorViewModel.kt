package com.perfectappstudio.scientificcalc.feature.vector

import androidx.lifecycle.ViewModel
import com.perfectappstudio.scientificcalc.core.math.VectorEngine
import com.perfectappstudio.scientificcalc.core.model.VectorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VectorViewModel : ViewModel() {

    private val _state = MutableStateFlow(VectorState())
    val state: StateFlow<VectorState> = _state.asStateFlow()

    // ---------------------------------------------------------------
    // Editing selection
    // ---------------------------------------------------------------

    fun selectVector(vector: Char) {
        _state.update { it.copy(editingVector = vector, error = null) }
    }

    // ---------------------------------------------------------------
    // Dimension changes
    // ---------------------------------------------------------------

    fun setDimension(vector: Char, dim: Int) {
        _state.update { s ->
            when (vector) {
                'A' -> s.copy(dimA = dim)
                'B' -> s.copy(dimB = dim)
                'C' -> s.copy(dimC = dim)
                else -> s
            }
        }
    }

    // ---------------------------------------------------------------
    // Component editing
    // ---------------------------------------------------------------

    fun setComponent(vector: Char, index: Int, value: String) {
        _state.update { s ->
            when (vector) {
                'A' -> {
                    val updated = s.cellsA.copyOf()
                    updated[index] = value
                    s.copy(cellsA = updated)
                }
                'B' -> {
                    val updated = s.cellsB.copyOf()
                    updated[index] = value
                    s.copy(cellsB = updated)
                }
                'C' -> {
                    val updated = s.cellsC.copyOf()
                    updated[index] = value
                    s.copy(cellsC = updated)
                }
                else -> s
            }
        }
    }

    /** Store the currently edited vector data from the cell fields. */
    fun storeVector(vector: Char) {
        val s = _state.value
        try {
            val data = buildVectorData(vector, s)
            _state.update { current ->
                when (vector) {
                    'A' -> current.copy(vctA = data, error = null)
                    'B' -> current.copy(vctB = data, error = null)
                    'C' -> current.copy(vctC = data, error = null)
                    else -> current
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Invalid number in Vct$vector: ${e.message}") }
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
        _state.update { it.copy(expression = "", error = null, vctAns = null) }
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
            _state.update { it.copy(vctAns = result, error = null) }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message ?: "Evaluation error", vctAns = null) }
        }
    }

    // ---------------------------------------------------------------
    // Expression parser
    // ---------------------------------------------------------------

    private fun evaluateExpression(expr: String, s: VectorState): DoubleArray {
        // Magnitude: Abs VctA
        val absMatch = Regex("^Abs\\s+Vct([A-C])$", RegexOption.IGNORE_CASE).find(expr)
        if (absMatch != null) {
            val v = resolveVector(absMatch.groupValues[1][0], s)
            val mag = VectorEngine.magnitude(v)
            return doubleArrayOf(mag)
        }

        // Dot product: Dot VctA VctB  or  VctA · VctB
        val dotMatch = Regex("^(?:Dot\\s+Vct([A-C])\\s+Vct([A-C])|Vct([A-C])\\s*·\\s*Vct([A-C]))$",
            RegexOption.IGNORE_CASE).find(expr)
        if (dotMatch != null) {
            val g = dotMatch.groupValues
            val aName = (g[1].ifEmpty { g[3] })[0]
            val bName = (g[2].ifEmpty { g[4] })[0]
            val a = resolveVector(aName, s)
            val b = resolveVector(bName, s)
            val dot = VectorEngine.dotProduct(a, b)
            return doubleArrayOf(dot)
        }

        // Cross product: Cross VctA VctB  or  VctA ✕ VctB
        val crossMatch = Regex("^(?:Cross\\s+Vct([A-C])\\s+Vct([A-C])|Vct([A-C])\\s*✕\\s*Vct([A-C]))$",
            RegexOption.IGNORE_CASE).find(expr)
        if (crossMatch != null) {
            val g = crossMatch.groupValues
            val aName = (g[1].ifEmpty { g[3] })[0]
            val bName = (g[2].ifEmpty { g[4] })[0]
            val a = resolveVector(aName, s)
            val b = resolveVector(bName, s)
            return VectorEngine.crossProduct(a, b)
        }

        // Binary: VctA + VctB, VctA - VctB
        val binMatch = Regex("^Vct([A-C])\\s*([+\\-])\\s*Vct([A-C])$").find(expr)
        if (binMatch != null) {
            val left = resolveVector(binMatch.groupValues[1][0], s)
            val op = binMatch.groupValues[2]
            val right = resolveVector(binMatch.groupValues[3][0], s)
            return when (op) {
                "+" -> VectorEngine.add(left, right)
                "-" -> VectorEngine.subtract(left, right)
                else -> throw IllegalArgumentException("Unknown operator: $op")
            }
        }

        // Scalar multiplication: 3 × VctA
        val scalarMatch = Regex("^(-?[\\d.]+)\\s*[×*]\\s*Vct([A-C])$").find(expr)
        if (scalarMatch != null) {
            val scalar = scalarMatch.groupValues[1].toDouble()
            val v = resolveVector(scalarMatch.groupValues[2][0], s)
            return VectorEngine.scalarMultiply(scalar, v)
        }

        throw IllegalArgumentException("Unrecognized expression: $expr")
    }

    private fun resolveVector(name: Char, s: VectorState): DoubleArray {
        return when (name.uppercaseChar()) {
            'A' -> s.vctA ?: throw IllegalArgumentException("VctA is not defined")
            'B' -> s.vctB ?: throw IllegalArgumentException("VctB is not defined")
            'C' -> s.vctC ?: throw IllegalArgumentException("VctC is not defined")
            else -> throw IllegalArgumentException("Unknown vector: Vct$name")
        }
    }

    private fun buildVectorData(vector: Char, s: VectorState): DoubleArray {
        val (dim, cells) = when (vector) {
            'A' -> s.dimA to s.cellsA
            'B' -> s.dimB to s.cellsB
            'C' -> s.dimC to s.cellsC
            else -> throw IllegalArgumentException("Unknown vector: $vector")
        }
        return DoubleArray(dim) { i ->
            val text = cells[i]
            if (text.isBlank()) 0.0 else text.toDouble()
        }
    }
}
