package com.calcmate.scientificcalculator.core.model

import com.calcmate.scientificcalculator.core.data.HistoryEntry

enum class DisplayFormat {
    DECIMAL,
    FRACTION,
    SCIENTIFIC
}

data class CalculatorState(
    val expression: String = "",
    val result: String = "",
    val displayFormat: DisplayFormat = DisplayFormat.DECIMAL,
    val angleUnit: AngleUnit = AngleUnit.DEGREE,
    val isScientificExpanded: Boolean = true,
    val isInverse: Boolean = false,
    val isHyperbolic: Boolean = false,
    val error: String? = null,
    val hasEvaluated: Boolean = false,
    val showHistory: Boolean = false,
    val historyEntries: List<HistoryEntry> = emptyList(),
    val mIndicator: Boolean = false,
)
