package com.calcmate.scientificcalculator.feature.solver

import androidx.lifecycle.ViewModel
import com.calcmate.scientificcalculator.core.math.EquationSolver
import com.calcmate.scientificcalculator.core.model.SolverResult
import com.calcmate.scientificcalculator.core.model.SolverState
import com.calcmate.scientificcalculator.core.model.SolverType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SolverViewModel : ViewModel() {

    private val _state = MutableStateFlow(SolverState())
    val state: StateFlow<SolverState> = _state.asStateFlow()

    // --- Type switching ---

    fun onTypeChange(type: SolverType) {
        _state.update {
            it.copy(
                solverType = type,
                result = null,
            )
        }
    }

    // --- Coefficient updates ---

    fun onLinearCoefficientChange(index: Int, value: String) {
        _state.update {
            when (index) {
                0 -> it.copy(linearA = value, result = null)
                1 -> it.copy(linearB = value, result = null)
                else -> it
            }
        }
    }

    fun onQuadCoefficientChange(index: Int, value: String) {
        _state.update {
            when (index) {
                0 -> it.copy(quadA = value, result = null)
                1 -> it.copy(quadB = value, result = null)
                2 -> it.copy(quadC = value, result = null)
                else -> it
            }
        }
    }

    fun onSystem2x2CoefficientChange(index: Int, value: String) {
        _state.update { current ->
            val updated = current.system2x2.copyOf()
            updated[index] = value
            current.copy(system2x2 = updated, result = null)
        }
    }

    fun onSystem3x3CoefficientChange(index: Int, value: String) {
        _state.update { current ->
            val updated = current.system3x3.copyOf()
            updated[index] = value
            current.copy(system3x3 = updated, result = null)
        }
    }

    // --- Solve ---

    fun onSolve() {
        val current = _state.value
        val result = when (current.solverType) {
            SolverType.LINEAR -> solveLinear(current)
            SolverType.QUADRATIC -> solveQuadratic(current)
            SolverType.SYSTEM_2X2 -> solveSystem2x2(current)
            SolverType.SYSTEM_3X3 -> solveSystem3x3(current)
        }
        _state.update { it.copy(result = result) }
    }

    fun onToggleSteps() {
        _state.update { it.copy(showSteps = !it.showSteps) }
    }

    // --- Private solving helpers ---

    private fun solveLinear(s: SolverState): SolverResult {
        val a = s.linearA.toDoubleOrNull()
            ?: return SolverResult.Error("Invalid coefficient a")
        val b = s.linearB.toDoubleOrNull()
            ?: return SolverResult.Error("Invalid coefficient b")
        return SolverResult.Linear(EquationSolver.solveLinear(a, b))
    }

    private fun solveQuadratic(s: SolverState): SolverResult {
        val a = s.quadA.toDoubleOrNull()
            ?: return SolverResult.Error("Invalid coefficient a")
        val b = s.quadB.toDoubleOrNull()
            ?: return SolverResult.Error("Invalid coefficient b")
        val c = s.quadC.toDoubleOrNull()
            ?: return SolverResult.Error("Invalid coefficient c")
        return SolverResult.Quadratic(EquationSolver.solveQuadratic(a, b, c))
    }

    private fun solveSystem2x2(s: SolverState): SolverResult {
        val v = DoubleArray(6)
        for (i in 0 until 6) {
            v[i] = s.system2x2[i].toDoubleOrNull()
                ?: return SolverResult.Error("Invalid coefficient at position ${i + 1}")
        }
        return SolverResult.System2x2(
            EquationSolver.solveSystem2x2(v[0], v[1], v[2], v[3], v[4], v[5]),
        )
    }

    private fun solveSystem3x3(s: SolverState): SolverResult {
        val matrix = Array(3) { row ->
            DoubleArray(4) { col ->
                val idx = row * 4 + col
                s.system3x3[idx].toDoubleOrNull()
                    ?: return SolverResult.Error("Invalid coefficient at row ${row + 1}, col ${col + 1}")
            }
        }
        return SolverResult.System3x3(EquationSolver.solveSystem3x3(matrix))
    }
}
