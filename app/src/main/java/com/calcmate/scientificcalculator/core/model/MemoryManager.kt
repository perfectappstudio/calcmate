package com.calcmate.scientificcalculator.core.model

import com.calcmate.scientificcalculator.core.parser.CalcResult

object MemoryManager {

    var ans: CalcResult = CalcResult.RealResult(0.0)

    private val variableNames = setOf('A', 'B', 'C', 'D', 'E', 'F', 'M', 'X', 'Y')

    val variables: MutableMap<Char, CalcResult> = variableNames
        .associateWith { CalcResult.RealResult(0.0) as CalcResult }
        .toMutableMap()

    var independentM: Double = 0.0
        private set

    fun storeVariable(name: Char, value: CalcResult) {
        require(name in variableNames) { "Invalid variable name: $name" }
        variables[name] = value
    }

    fun recallVariable(name: Char): CalcResult {
        return variables[name] ?: CalcResult.RealResult(0.0)
    }

    fun addToM(value: Double) {
        independentM += value
        variables['M'] = CalcResult.RealResult(independentM)
    }

    fun subtractFromM(value: Double) {
        independentM -= value
        variables['M'] = CalcResult.RealResult(independentM)
    }

    fun clearAll() {
        ans = CalcResult.RealResult(0.0)
        variableNames.forEach { variables[it] = CalcResult.RealResult(0.0) }
        independentM = 0.0
    }
}
