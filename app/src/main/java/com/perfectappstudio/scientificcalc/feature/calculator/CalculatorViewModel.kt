package com.perfectappstudio.scientificcalc.feature.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.perfectappstudio.scientificcalc.core.data.AppDatabase
import com.perfectappstudio.scientificcalc.core.data.HistoryEntry
import com.perfectappstudio.scientificcalc.core.data.HistoryRepository
import com.perfectappstudio.scientificcalc.core.data.PreferencesManager
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.model.CalculatorAction
import com.perfectappstudio.scientificcalc.core.model.CalculatorState
import com.perfectappstudio.scientificcalc.core.model.DisplaySettings
import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import com.perfectappstudio.scientificcalc.core.parser.CalcResult
import com.perfectappstudio.scientificcalc.core.parser.Evaluator
import com.perfectappstudio.scientificcalc.core.parser.Formatter
import com.perfectappstudio.scientificcalc.core.parser.Lexer
import com.perfectappstudio.scientificcalc.core.parser.Parser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    private val historyRepository: HistoryRepository
    private var completedResult: CalcResult? = null
    private val preferences = PreferencesManager(application)

    init {
        viewModelScope.launch {
            preferences.angleUnit.collect { unit ->
                val angle = AngleUnit.entries.firstOrNull { it.name.equals(unit, ignoreCase = true) }
                    ?: AngleUnit.DEGREE
                _state.update { it.copy(angleUnit = angle) }
                updateLivePreview()
            }
        }
        viewModelScope.launch {
            preferences.displaySettings.collect { settings ->
                applyDisplaySettings(settings)
            }
        }
        val dao = AppDatabase.getDatabase(application).historyDao()
        historyRepository = HistoryRepository(dao)

        viewModelScope.launch {
            historyRepository.history.collect { entries ->
                _state.update { it.copy(historyEntries = entries) }
            }
        }
    }

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
            is CalculatorAction.ToggleFractionDisplay -> setDisplaySettings(
                _state.value.displaySettings.copy(showFractions = !_state.value.displaySettings.showFractions),
            )
            is CalculatorAction.ToggleAngleUnit -> onToggleAngleUnit()
            is CalculatorAction.ToggleScientific -> onToggleScientific()
            is CalculatorAction.ToggleInverse -> onToggleInverse()
            is CalculatorAction.ToggleHyperbolic -> onToggleHyperbolic()
            is CalculatorAction.ShowHistory -> onShowHistory()
            is CalculatorAction.HideHistory -> onHideHistory()
            is CalculatorAction.ReuseHistoryEntry -> onReuseHistoryEntry(action.entry)
            is CalculatorAction.DeleteHistoryEntry -> onDeleteHistoryEntry(action.entry)
            is CalculatorAction.ClearHistory -> onClearHistory()
            is CalculatorAction.StoreVariable -> onStoreVariable(action.name)
            is CalculatorAction.RecallVariable -> onRecallVariable(action.name)
            is CalculatorAction.AddToM -> onAddToM()
            is CalculatorAction.SubtractFromM -> onSubtractFromM()
            is CalculatorAction.RecallM -> onRecallM()
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
        val currentNumber = current.expression.takeLastWhile { it.isDigit() || it == '.' }
        if (!current.hasEvaluated && '.' in currentNumber) return
        val newExpression = when {
            current.hasEvaluated -> "0."
            currentNumber.isEmpty() -> current.expression + "0."
            else -> current.expression + "."
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

    private fun onOperator(symbol: String) {
        val current = _state.value
        val base = if (current.hasEvaluated && current.result.isNotEmpty()) {
            "Ans"
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
        val base = if (current.hasEvaluated) "" else current.expression
        val multiply = base.lastOrNull()?.let { it.isDigit() || it in ").!%πeABCDEFMXY" } == true || base.endsWith("Ans")
        val newExpression = base + (if (multiply) "×" else "") + symbol
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
            val (displayStr, calcResult) = evaluateExpressionFull(current.expression, current.angleUnit, current.displaySettings)
            completedResult = calcResult
            MemoryManager.ans = calcResult
            _state.update {
                it.copy(
                    result = displayStr,
                    error = null,
                    hasEvaluated = true,
                )
            }
            // Save to history
            val formatName = if (current.displaySettings.showFractions) {
                "fraction_${current.displaySettings.fractionFormat.name.lowercase()}"
            } else current.displaySettings.mode.name.lowercase()
            viewModelScope.launch {
                historyRepository.addEntry(current.expression, displayStr, formatName)
            }
        } catch (_: Exception) {
            _state.update {
                it.copy(
                    error = "Check the expression or the function's domain",
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

        val newExpression = if (current.hasEvaluated) {
            "-(${completedResult?.toDouble() ?: return})"
        } else if (current.expression.startsWith("-(") && current.expression.endsWith(")")) {
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
        val currentMode = _state.value.displaySettings.mode
        val nextMode = com.perfectappstudio.scientificcalc.core.model.DisplayMode.entries.let { modes ->
            val idx = modes.indexOf(currentMode)
            modes[(idx + 1) % modes.size]
        }
        val digits = when (nextMode) {
            com.perfectappstudio.scientificcalc.core.model.DisplayMode.FIX -> _state.value.displaySettings.digits.coerceIn(0, 9)
            com.perfectappstudio.scientificcalc.core.model.DisplayMode.SCI -> _state.value.displaySettings.digits.coerceIn(1, 10)
            else -> _state.value.displaySettings.digits
        }
        val nextSettings = _state.value.displaySettings.copy(mode = nextMode, digits = digits)
        setDisplaySettings(nextSettings)
    }

    private fun onToggleAngleUnit() {
        val nextUnit = when (_state.value.angleUnit) {
            AngleUnit.DEGREE -> AngleUnit.RADIAN
            AngleUnit.RADIAN -> AngleUnit.GRADIAN
            AngleUnit.GRADIAN -> AngleUnit.DEGREE
        }
        setAngleUnit(nextUnit)
    }

    fun setAngleUnit(unit: AngleUnit) {
        _state.update { it.copy(angleUnit = unit) }
        updateLivePreview()
        viewModelScope.launch { preferences.setAngleUnit(unit.name.lowercase()) }
    }

    fun setDisplaySettings(settings: DisplaySettings) {
        applyDisplaySettings(settings)
        viewModelScope.launch { preferences.setDisplaySettings(settings) }
    }

    fun useToolResult(value: Double) {
        require(value.isFinite())
        val result = CalcResult.RealResult(value)
        completedResult = result
        MemoryManager.ans = result
        _state.update {
            it.copy(expression = value.toString(), result = Formatter().formatWithSettings(value, it.displaySettings),
                error = null, hasEvaluated = true)
        }
    }

    private fun applyDisplaySettings(settings: DisplaySettings) {
        _state.update { it.copy(displaySettings = settings) }
        if (_state.value.expression.isBlank()) return
        if (_state.value.hasEvaluated) {
            completedResult?.let { result ->
                _state.update { it.copy(result = Formatter().formatWithSettings(result.toDouble(), settings)) }
            }
        } else {
            updateLivePreview()
        }
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
                result = "",
                error = null,
                hasEvaluated = false,
                showHistory = false,
            )
        }
        updateLivePreview()
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
        if (current.hasEvaluated) return
        if (current.expression.isBlank()) {
            _state.update { it.copy(result = "") }
            return
        }
        try {
            val result = evaluateExpression(current.expression, current.angleUnit, current.displaySettings)
            _state.update { it.copy(result = result, error = null) }
        } catch (_: Exception) {
            _state.update { it.copy(result = "", error = null) }
        }
    }

    private fun onStoreVariable(name: Char) {
        val current = _state.value
        if (!current.hasEvaluated || current.result.isEmpty()) return
        try {
            MemoryManager.storeVariable(name, completedResult ?: return)
        } catch (_: Exception) {
            // ignore
        }
    }

    private fun onRecallVariable(name: Char) {
        val recalled = MemoryManager.recallVariable(name)
        val current = _state.value
        val insertText = if (name == 'M' || name in 'A'..'F' || name == 'X' || name == 'Y') {
            name.toString()
        } else {
            recalled.toDouble().toString()
        }
        val newExpression = if (current.hasEvaluated) insertText else current.expression + insertText
        _state.update {
            it.copy(
                expression = newExpression,
                error = null,
                hasEvaluated = false,
            )
        }
        updateLivePreview()
    }

    private fun onAddToM() {
        val current = _state.value
        if (!current.hasEvaluated || current.result.isEmpty()) return
        try {
            MemoryManager.addToM(completedResult?.toDouble() ?: return)
            _state.update { it.copy(mIndicator = MemoryManager.independentM != 0.0) }
        } catch (_: Exception) {
            // ignore
        }
    }

    private fun onSubtractFromM() {
        val current = _state.value
        if (!current.hasEvaluated || current.result.isEmpty()) return
        try {
            MemoryManager.subtractFromM(completedResult?.toDouble() ?: return)
            _state.update { it.copy(mIndicator = MemoryManager.independentM != 0.0) }
        } catch (_: Exception) {
            // ignore
        }
    }

    private fun onRecallM() {
        onRecallVariable('M')
    }

    private fun evaluateExpressionFull(
        expression: String,
        angleUnit: AngleUnit,
        displaySettings: DisplaySettings,
    ): Pair<String, CalcResult> {
        val normalized = expression
            .replace("\u00D7", "*")
            .replace("\u00F7", "/")
            .replace("\u2212", "-")
            .replace("\u03C0", "pi")

        val tokens = Lexer(normalized).tokenize()
        val ast = Parser(tokens).parse()
        val result = Evaluator(angleUnit).evaluate(ast)
        require(result.toDouble().isFinite()) { "No finite real result" }

        val formatter = Formatter()
        val displayStr = formatter.formatWithSettings(result.toDouble(), displaySettings)
        return displayStr to result
    }

    private fun evaluateExpression(
        expression: String,
        angleUnit: AngleUnit,
        displaySettings: DisplaySettings,
    ): String {
        return evaluateExpressionFull(expression, angleUnit, displaySettings).first
    }
}
