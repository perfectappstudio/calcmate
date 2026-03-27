package com.perfectappstudio.scientificcalc.core.model

import com.perfectappstudio.scientificcalc.core.data.HistoryEntry

data class CalculatorState(
    val expression: String = "",
    val result: String = "",
    val displaySettings: DisplaySettings = DisplaySettings(),
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
