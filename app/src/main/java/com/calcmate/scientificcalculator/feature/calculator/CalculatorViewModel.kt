package com.calcmate.scientificcalculator.feature.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calcmate.scientificcalculator.core.data.AppDatabase
import com.calcmate.scientificcalculator.core.data.HistoryEntry
import com.calcmate.scientificcalculator.core.data.HistoryRepository
import com.calcmate.scientificcalculator.core.model.AngleUnit
import com.calcmate.scientificcalculator.core.model.CalculatorAction
import com.calcmate.scientificcalculator.core.model.CalculatorState
import com.calcmate.scientificcalculator.core.model.DisplayFormat
import com.calcmate.scientificcalculator.core.parser.DisplayMode
import com.calcmate.scientificcalculator.core.parser.Evaluator
import com.calcmate.scientificcalculator.core.parser.Formatter
import com.calcmate.scientificcalculator.core.parser.Lexer
import com.calcmate.scientificcalculator.core.parser.Parser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository: HistoryRepository

    init {
        val dao = AppDatabase.getDatabase(application).historyDao()
        historyRepository = HistoryRepository(dao)

        viewModelScope.launch {
            historyRepository.history.collect { entries ->
                _state.update { it.copy(historyEntries = entries) }
            }
        }
    }

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> onDigit(action.digit)
            is CalculatorAction.Decimal -> onDecimal()
            is CalculatorAction.Operator -> onOperator(action.symbol)
            is CalculatorAction.Function -> onFunction(action.name)
            is CalculatorAction.Constant -> onConstant(action.symbol)
            is CalculatorAction.OpenParen -> onOpenParen()
            is CalculatorAction.CloseParen -> onCloseParen()
            is CalculatorAction.Equals -> onEquals()
            is CalculatorAction.Clear -> onClear()
            is CalculatorAction.Backspace -> onBackspace()
            is CalculatorAction.ToggleSign -> onToggleSign()
            is CalculatorAction.ToggleDisplayFormat -> onToggleDisplayFormat()
            is CalculatorAction.ToggleAngleUnit -> onToggleAngleUnit()
            is CalculatorAction.ToggleScientific -> onToggleScientific()
            is CalculatorAction.ToggleInverse -> onToggleInverse()
            is CalculatorAction.ToggleHyperbolic -> onToggleHyperbolic()
            is CalculatorAction.ShowHistory -> onShowHistory()
            is CalculatorAction.HideHistory -> onHideHistory()
            is CalculatorAction.ReuseHistoryEntry -> onReuseHistoryEntry(action.entry)
            is CalculatorAction.DeleteHistoryEntry -> onDeleteHistoryEntry(action.entry)
            is CalculatorAction.ClearHistory -> onClearHistory()
        }
    }

    private fun onDigit(digit: String) {
        val current = _state.value
        val newExpression = if (current.hasEvaluated) digit else current.expression + digit
        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onDecimal() {
        val current = _state.value
        val newExpression = if (current.hasEvaluated) "0." else current.expression + "."
        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onOperator(symbol: String) {
        val current = _state.value
        val base = if (current.hasEvaluated && current.result.isNotEmpty()) {
            current.result
        } else {
            current.expression
        }
        _state.update {
            it.copy(
                expression = base + symbol,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onFunction(name: String) {
        val current = _state.value
        val resolvedName = resolveFunctionName(name)
        val append = "$resolvedName("
        val newExpression = if (current.hasEvaluated) append else current.expression + append
        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun resolveFunctionName(baseName: String): String {
        val current = _state.value
        val isInv = current.isInverse
        val isHyp = current.isHyperbolic

        return when {
            isInv && isHyp -> when (baseName) {
                "sin" -> "asinh"
                "cos" -> "acosh"
                "tan" -> "atanh"
                else -> baseName
            }
            isInv -> when (baseName) {
                "sin" -> "asin"
                "cos" -> "acos"
                "tan" -> "atan"
                else -> baseName
            }
            isHyp -> when (baseName) {
                "sin" -> "sinh"
                "cos" -> "cosh"
                "tan" -> "tanh"
                else -> baseName
            }
            else -> baseName
        }
    }

    private fun onConstant(symbol: String) {
        val current = _state.value
        val newExpression = if (current.hasEvaluated) symbol else current.expression + symbol
        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onOpenParen() {
        val current = _state.value
        val newExpression = if (current.hasEvaluated) "(" else current.expression + "("
        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onCloseParen() {
        _state.update {
            it.copy(
                expression = it.expression + ")",
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onEquals() {
        val current = _state.value
        if (current.expression.isBlank()) return

        try {
            val result = evaluateExpression(current.expression, current.angleUnit, current.displayFormat)
            _state.update {
                it.copy(
                    result = result,
                    error = null,
                    hasEvaluated = true,
                )
            }
            // Save to history
            val formatName = current.displayFormat.name.lowercase()
            viewModelScope.launch {
                historyRepository.addEntry(current.expression, result, formatName)
            }
        } catch (_: Exception) {
            _state.update {
                it.copy(
                    error = "Error",
                    result = "",
                    hasEvaluated = false,
                )
            }
        }
    }

    private fun onClear() {
        _state.update {
            it.copy(
                expression = "",
                result = "",
                error = null,
                hasEvaluated = false,
            )
        }
    }

    private fun onBackspace() {
        val current = _state.value
        if (current.expression.isEmpty()) return

        // Check if the expression ends with a function name + "("
        val functionPattern = Regex("""(a?(?:sin|cos|tan)h?|ln|log|sqrt|cbrt|abs)\($""")
        val match = functionPattern.find(current.expression)

        val newExpression = if (match != null) {
            current.expression.removeRange(match.range)
        } else {
            current.expression.dropLast(1)
        }

        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onToggleSign() {
        val current = _state.value
        if (current.expression.isEmpty()) return

        val newExpression = if (current.expression.startsWith("-(") && current.expression.endsWith(")")) {
            // Remove negation wrapper: -(expr) -> expr
            current.expression.removePrefix("-(").removeSuffix(")")
        } else {
            // Wrap entire expression in negation
            "-(${current.expression})"
        }

        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onToggleDisplayFormat() {
        val nextFormat = when (_state.value.displayFormat) {
            DisplayFormat.DECIMAL -> DisplayFormat.FRACTION
            DisplayFormat.FRACTION -> DisplayFormat.SCIENTIFIC
            DisplayFormat.SCIENTIFIC -> DisplayFormat.DECIMAL
        }
        _state.update { it.copy(displayFormat = nextFormat) }
        // Re-evaluate if there's a result to show in the new format
        if (_state.value.hasEvaluated && _state.value.expression.isNotBlank()) {
            try {
                val result = evaluateExpression(
                    _state.value.expression,
                    _state.value.angleUnit,
                    nextFormat,
                )
                _state.update { it.copy(result = result) }
            } catch (_: Exception) {
                // keep existing result
            }
        }
    }

    private fun onToggleAngleUnit() {
        val nextUnit = when (_state.value.angleUnit) {
            AngleUnit.DEGREE -> AngleUnit.RADIAN
            AngleUnit.RADIAN -> AngleUnit.DEGREE
        }
        _state.update { it.copy(angleUnit = nextUnit) }
        updateLivePreview()
    }

    private fun onToggleScientific() {
        _state.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    private fun onToggleInverse() {
        _state.update { it.copy(isInverse = !it.isInverse) }
    }

    private fun onToggleHyperbolic() {
        _state.update { it.copy(isHyperbolic = !it.isHyperbolic) }
    }

    // -- History actions --

    private fun onShowHistory() {
        _state.update { it.copy(showHistory = true) }
    }

    private fun onHideHistory() {
        _state.update { it.copy(showHistory = false) }
    }

    private fun onReuseHistoryEntry(entry: HistoryEntry) {
        _state.update {
            it.copy(
                expression = entry.expression,
                result = entry.result,
                error = null,
                hasEvaluated = true,
                showHistory = false,
            )
        }
    }

    private fun onDeleteHistoryEntry(entry: HistoryEntry) {
        viewModelScope.launch {
            historyRepository.deleteEntry(entry)
        }
    }

    private fun onClearHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    private fun updateLivePreview() {
        val current = _state.value
        if (current.expression.isBlank()) {
            _state.update { it.copy(result = "") }
            return
        }
        try {
            val result = evaluateExpression(current.expression, current.angleUnit, current.displayFormat)
            _state.update { it.copy(result = result, error = null) }
        } catch (_: Exception) {
            // Silently ignore preview errors -- expression is likely incomplete
        }
    }

    private fun evaluateExpression(
        expression: String,
        angleUnit: AngleUnit,
        displayFormat: DisplayFormat,
    ): String {
        // Normalize display operators to parser-compatible symbols
        val normalized = expression
            .replace("\u00D7", "*")  // multiply sign
            .replace("\u00F7", "/") // division sign
            .replace("\u2212", "-") // minus sign
            .replace("\u03C0", "pi") // pi symbol -> keyword for lexer

        val tokens = Lexer(normalized).tokenize()
        val ast = Parser(tokens).parse()
        val value = Evaluator(angleUnit).evaluate(ast)

        val displayMode = when (displayFormat) {
            DisplayFormat.DECIMAL -> DisplayMode.DECIMAL
            DisplayFormat.FRACTION -> DisplayMode.FRACTION
            DisplayFormat.SCIENTIFIC -> DisplayMode.SCIENTIFIC
        }
        return Formatter(displayMode).format(value)
    }
}
