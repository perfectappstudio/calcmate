package com.calcmate.scientificcalculator.core.model

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
}
