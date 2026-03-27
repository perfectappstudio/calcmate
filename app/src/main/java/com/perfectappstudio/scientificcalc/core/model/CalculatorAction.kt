package com.perfectappstudio.scientificcalc.core.model

import com.perfectappstudio.scientificcalc.core.data.HistoryEntry

sealed interface CalculatorAction {
    data class Digit(val digit: String) : CalculatorAction
    data object Decimal : CalculatorAction
    data class Operator(val symbol: String) : CalculatorAction
    data class Function(val name: String) : CalculatorAction
    data class Constant(val symbol: String) : CalculatorAction
    data object OpenParen : CalculatorAction
    data object CloseParen : CalculatorAction
    data object Equals : CalculatorAction
    data object Clear : CalculatorAction
    data object Backspace : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object ToggleDisplayFormat : CalculatorAction
    data object ToggleAngleUnit : CalculatorAction
    data object ToggleScientific : CalculatorAction
    data object ToggleInverse : CalculatorAction
    data object ToggleHyperbolic : CalculatorAction
    data object ShowHistory : CalculatorAction
    data object HideHistory : CalculatorAction
    data class ReuseHistoryEntry(val entry: HistoryEntry) : CalculatorAction
    data class DeleteHistoryEntry(val entry: HistoryEntry) : CalculatorAction
    data object ClearHistory : CalculatorAction
    data class StoreVariable(val name: Char) : CalculatorAction
    data class RecallVariable(val name: Char) : CalculatorAction
    data object AddToM : CalculatorAction
    data object SubtractFromM : CalculatorAction
    data object RecallM : CalculatorAction
}
