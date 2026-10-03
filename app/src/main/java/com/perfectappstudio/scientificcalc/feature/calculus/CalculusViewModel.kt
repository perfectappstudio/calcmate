package com.perfectappstudio.scientificcalc.feature.calculus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perfectappstudio.scientificcalc.core.math.NumericalCalculus
import com.perfectappstudio.scientificcalc.core.math.NumericalEstimate
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import com.perfectappstudio.scientificcalc.core.parser.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class CalculusMode { DERIVATIVE, INTEGRAL }

data class CalculusState(
    val mode: CalculusMode = CalculusMode.DERIVATIVE,
    val function: String = "x^2",
    val point: String = "2",
    val lower: String = "0",
    val upper: String = "1",
    val step: String = "",
    val angleUnit: AngleUnit = AngleUnit.RADIAN,
    val busy: Boolean = false,
    val result: NumericalEstimate? = null,
    val error: String? = null,
)

class CalculusViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {
    private val mutableState = MutableStateFlow(CalculusState())
    val state = mutableState.asStateFlow()
    private var calculation: Job? = null
    private var revision = 0

    fun edit(transform: (CalculusState) -> CalculusState) {
        revision++
        calculation?.cancel()
        mutableState.update { transform(it).copy(result = null, error = null, busy = false) }
    }

    fun calculate() {
        val input = state.value
        val variables = MemoryManager.variables.toMap()
        val answer = MemoryManager.ans
        val request = ++revision
        calculation?.cancel()
        mutableState.update { it.copy(busy = true, result = null, error = null) }
        calculation = viewModelScope.launch {
            val context = currentCoroutineContext()
            try {
                val result = withContext(dispatcher) {
                    fun number(text: String): Double {
                        require(text.length in 1..256) { "Enter a number or expression for each limit or point" }
                        val tokens = Lexer(text.replace('−', '-')).tokenize()
                        require(tokens.none { it.type == TokenType.RANDOM }) { "Use a fixed point or limit" }
                        val value = Evaluator(input.angleUnit, variables, answer).evaluate(Parser(tokens).parse()).toDouble()
                        require(value.isFinite()) { "Points and limits must be finite" }
                        return value
                    }
                    if (input.mode == CalculusMode.DERIVATIVE) {
                        NumericalCalculus.estimateDerivative(
                            input.function, number(input.point),
                            dx = input.step.takeIf { it.isNotBlank() }?.let(::number),
                            angleUnit = input.angleUnit, variables = variables, answer = answer,
                            checkCancelled = { context.ensureActive() },
                        )
                    } else {
                        NumericalCalculus.estimateIntegral(
                            input.function, number(input.lower), number(input.upper),
                            angleUnit = input.angleUnit, variables = variables, answer = answer,
                            checkCancelled = { context.ensureActive() },
                        )
                    }
                }
                if (request == revision) mutableState.update { it.copy(result = result) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (request == revision) mutableState.update { it.copy(error = error.message ?: "Check the function and its domain") }
            } finally {
                if (request == revision) mutableState.update { it.copy(busy = false) }
            }
        }
    }

    fun cancel() {
        revision++
        calculation?.cancel()
        mutableState.update { it.copy(busy = false) }
    }
}
