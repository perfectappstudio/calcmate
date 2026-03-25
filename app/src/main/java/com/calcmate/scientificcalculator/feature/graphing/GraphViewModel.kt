package com.calcmate.scientificcalculator.feature.graphing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calcmate.scientificcalculator.core.model.GraphFunction
import com.calcmate.scientificcalculator.core.model.GraphState
import com.calcmate.scientificcalculator.core.model.TracePoint
import com.calcmate.scientificcalculator.core.model.Viewport
import com.calcmate.scientificcalculator.core.parser.Evaluator
import com.calcmate.scientificcalculator.core.parser.Lexer
import com.calcmate.scientificcalculator.core.parser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class GraphViewModel : ViewModel() {

    companion object {
        private const val MAX_FUNCTIONS = 8
        private const val SAMPLE_COUNT = 500

        val FUNCTION_COLORS = listOf(
            0xFFD0BCFF,
            0xFFEFB8C8,
            0xFF80CBC4,
            0xFFFFCC80,
            0xFF90CAF9,
            0xFFA5D6A7,
            0xFFEF9A9A,
            0xFFCE93D8,
        )
    }

    private val _state = MutableStateFlow(GraphState())
    val state: StateFlow<GraphState> = _state.asStateFlow()

    private var nextId = 0

    fun addFunction() {
        val current = _state.value.functions
        if (current.size >= MAX_FUNCTIONS) return

        val colorIndex = current.size % FUNCTION_COLORS.size
        val newFunction = GraphFunction(
            id = nextId++,
            expression = "",
            color = FUNCTION_COLORS[colorIndex],
        )
        _state.update { it.copy(functions = it.functions + newFunction) }
    }

    fun removeFunction(id: Int) {
        _state.update { state ->
            val updated = state.functions.filter { it.id != id }
            val tracePoint = if (state.tracePoint?.functionId == id) null else state.tracePoint
            state.copy(functions = updated, tracePoint = tracePoint)
        }
    }

    fun updateFunction(id: Int, expression: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val isValid = validateExpression(expression)
            _state.update { state ->
                val updated = state.functions.map { func ->
                    if (func.id == id) func.copy(expression = expression, isValid = isValid)
                    else func
                }
                state.copy(functions = updated)
            }
        }
    }

    fun setViewport(viewport: Viewport) {
        _state.update { it.copy(viewport = viewport) }
    }

    fun resetZoom() {
        _state.update { it.copy(viewport = Viewport(), tracePoint = null, isTracing = false) }
    }

    fun trace(screenX: Float, screenY: Float, canvasWidth: Float, canvasHeight: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            val viewport = _state.value.viewport
            val mathX = viewport.xMin + (screenX / canvasWidth) * (viewport.xMax - viewport.xMin)
            val mathY = viewport.yMax - (screenY / canvasHeight) * (viewport.yMax - viewport.yMin)

            var bestFunction: GraphFunction? = null
            var bestY = Double.NaN
            var bestDistance = Double.MAX_VALUE

            for (func in _state.value.functions) {
                if (!func.isValid || func.expression.isBlank()) continue
                val y = evaluateAt(func.expression, mathX)
                if (y.isNaN() || y.isInfinite()) continue

                val distance = abs(y - mathY)
                if (distance < bestDistance) {
                    bestDistance = distance
                    bestY = y
                    bestFunction = func
                }
            }

            if (bestFunction != null && !bestY.isNaN()) {
                _state.update {
                    it.copy(
                        tracePoint = TracePoint(
                            functionId = bestFunction.id,
                            x = mathX,
                            y = bestY,
                        ),
                        isTracing = true,
                    )
                }
            }
        }
    }

    fun dismissTrace() {
        _state.update { it.copy(tracePoint = null, isTracing = false) }
    }

    /**
     * Evaluates [expression] as f(x) at the given [x] value.
     *
     * Standalone "x" tokens are replaced with the numeric value while
     * preserving function names that contain "x" (e.g. "exp", "max").
     */
    fun evaluateAt(expression: String, x: Double): Double {
        return try {
            val substituted = substituteX(expression, x)
            val tokens = Lexer(substituted).tokenize()
            val ast = Parser(tokens).parse()
            Evaluator().evaluate(ast)
        } catch (_: Exception) {
            Double.NaN
        }
    }

    /**
     * Generates [count] evenly-spaced sample points for the given expression
     * across the current viewport's x-range.
     */
    fun sampleFunction(expression: String, count: Int = SAMPLE_COUNT): List<Pair<Double, Double>> {
        val viewport = _state.value.viewport
        val step = (viewport.xMax - viewport.xMin) / count
        return (0..count).map { i ->
            val x = viewport.xMin + i * step
            val y = evaluateAt(expression, x)
            x to y
        }
    }

    // ---- private helpers ----

    private fun validateExpression(expression: String): Boolean {
        if (expression.isBlank()) return true // empty is not "invalid"
        return try {
            val substituted = substituteX(expression, 1.0)
            val tokens = Lexer(substituted).tokenize()
            Parser(tokens).parse()
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Replaces standalone "x" with the numeric value.
     *
     * Uses a regex with word boundaries but also protects against partial
     * matches inside function names like "exp", "max", "hex" by requiring
     * that "x" is not preceded/followed by a letter.
     */
    private fun substituteX(expression: String, x: Double): String {
        val xStr = if (x < 0) "($x)" else x.toString()
        return expression.replace(Regex("(?<![a-wyzA-WYZ])x(?![a-zA-Z])"), xStr)
    }
}
