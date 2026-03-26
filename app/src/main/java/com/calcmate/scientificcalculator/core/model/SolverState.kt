package com.calcmate.scientificcalculator.core.model

import com.calcmate.scientificcalculator.core.math.CubicResult
import com.calcmate.scientificcalculator.core.math.LinearResult
import com.calcmate.scientificcalculator.core.math.NewtonResult
import com.calcmate.scientificcalculator.core.math.QuadraticResult
import com.calcmate.scientificcalculator.core.math.SystemResult
import com.calcmate.scientificcalculator.core.math.SystemResult3x3

enum class SolverType {
    LINEAR,
    QUADRATIC,
    CUBIC,
    SYSTEM_2X2,
    SYSTEM_3X3,
    NEWTON,
}

sealed class SolverResult {
    data class Linear(val result: LinearResult) : SolverResult()
    data class Quadratic(val result: QuadraticResult) : SolverResult()
    data class Cubic(val result: CubicResult) : SolverResult()
    data class System2x2(val result: SystemResult) : SolverResult()
    data class System3x3(val result: SystemResult3x3) : SolverResult()
    data class Newton(val result: NewtonResult) : SolverResult()
    data class Error(val message: String) : SolverResult()
}

data class SolverState(
    val solverType: SolverType = SolverType.QUADRATIC,
    val linearA: String = "",
    val linearB: String = "",
    val quadA: String = "",
    val quadB: String = "",
    val quadC: String = "",
    val cubA: String = "",
    val cubB: String = "",
    val cubC: String = "",
    val cubD: String = "",
    val system2x2: Array<String> = Array(6) { "" },
    val system3x3: Array<String> = Array(12) { "" },
    val newtonExpression: String = "",
    val newtonGuess: String = "0",
    val result: SolverResult? = null,
    val showSteps: Boolean = false,
) {
    /** Convenience copy that keeps array semantics correct. */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SolverState) return false
        return solverType == other.solverType &&
            linearA == other.linearA &&
            linearB == other.linearB &&
            quadA == other.quadA &&
            quadB == other.quadB &&
            quadC == other.quadC &&
            cubA == other.cubA &&
            cubB == other.cubB &&
            cubC == other.cubC &&
            cubD == other.cubD &&
            system2x2.contentEquals(other.system2x2) &&
            system3x3.contentEquals(other.system3x3) &&
            newtonExpression == other.newtonExpression &&
            newtonGuess == other.newtonGuess &&
            result == other.result &&
            showSteps == other.showSteps
    }

    override fun hashCode(): Int {
        var h = solverType.hashCode()
        h = 31 * h + linearA.hashCode()
        h = 31 * h + linearB.hashCode()
        h = 31 * h + quadA.hashCode()
        h = 31 * h + quadB.hashCode()
        h = 31 * h + quadC.hashCode()
        h = 31 * h + cubA.hashCode()
        h = 31 * h + cubB.hashCode()
        h = 31 * h + cubC.hashCode()
        h = 31 * h + cubD.hashCode()
        h = 31 * h + system2x2.contentHashCode()
        h = 31 * h + system3x3.contentHashCode()
        h = 31 * h + newtonExpression.hashCode()
        h = 31 * h + newtonGuess.hashCode()
        h = 31 * h + (result?.hashCode() ?: 0)
        h = 31 * h + showSteps.hashCode()
        return h
    }
}
