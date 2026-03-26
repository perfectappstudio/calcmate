package com.calcmate.scientificcalculator.feature.basen

import androidx.lifecycle.ViewModel
import com.calcmate.scientificcalculator.core.math.BaseNEngine
import com.calcmate.scientificcalculator.core.model.BaseNState
import com.calcmate.scientificcalculator.core.parser.NumberBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BaseNViewModel : ViewModel() {

    private val _state = MutableStateFlow(BaseNState())
    val state: StateFlow<BaseNState> = _state.asStateFlow()

    /** Current computed value (signed 32-bit), kept in sync with the display result. */
    private var currentValue: Long = 0L
    private var hasResult: Boolean = false

    // ---------------------------------------------------------------
    // Digit input
    // ---------------------------------------------------------------

    fun onDigit(digit: String) {
        val base = _state.value.currentBase
        if (!isValidDigitForBase(digit, base)) return

        _state.update { s ->
            val expr = if (hasResult && !endsWithOperator(s.expression)) {
                // Start fresh after evaluation
                hasResult = false
                digit
            } else {
                hasResult = false
                s.expression + digit
            }
            s.copy(expression = expr, error = null)
        }
        liveEvaluate()
    }

    // ---------------------------------------------------------------
    // Operator input
    // ---------------------------------------------------------------

    fun onOperator(op: String) {
        when (op) {
            "NOT" -> applyUnaryOp(op)
            "NEG" -> applyUnaryOp(op)
            else -> appendBinaryOp(op)
        }
    }

    private fun applyUnaryOp(op: String) {
        _state.update { s ->
            try {
                val base = s.currentBase
                val operand = extractLastOperand(s.expression, base)
                val result = when (op) {
                    "NOT" -> BaseNEngine.not(operand)
                    "NEG" -> BaseNEngine.neg(operand)
                    else -> operand
                }
                val resultStr = BaseNEngine.toString(result, base)
                // Replace the last operand in the expression with the result
                val prefix = dropLastOperand(s.expression, base)
                val newExpr = prefix + resultStr
                currentValue = result
                hasResult = false
                s.copy(expression = newExpr, result = resultStr, error = null)
            } catch (e: Exception) {
                s.copy(error = e.message ?: "Error")
            }
        }
    }

    private fun appendBinaryOp(op: String) {
        val symbol = when (op) {
            "+", "-", "\u00D7", "\u00F7" -> op
            "AND" -> " AND "
            "OR" -> " OR "
            "XOR" -> " XOR "
            "XNOR" -> " XNOR "
            else -> op
        }
        _state.update { s ->
            hasResult = false
            val expr = if (s.expression.isEmpty()) {
                "0$symbol"
            } else if (endsWithOperator(s.expression)) {
                // Replace trailing operator
                replaceTrailingOperator(s.expression, symbol)
            } else {
                s.expression + symbol
            }
            s.copy(expression = expr, error = null)
        }
    }

    // ---------------------------------------------------------------
    // Equals
    // ---------------------------------------------------------------

    fun onEquals() {
        _state.update { s ->
            try {
                val base = s.currentBase
                val value = evaluateExpression(s.expression, base)
                currentValue = value
                hasResult = true
                val resultStr = BaseNEngine.toString(value, base)
                s.copy(result = resultStr, error = null)
            } catch (e: Exception) {
                s.copy(error = e.message ?: "Error")
            }
        }
    }

    // ---------------------------------------------------------------
    // Base change
    // ---------------------------------------------------------------

    fun onBaseChange(base: NumberBase) {
        _state.update { s ->
            try {
                // Convert current result to the new base display
                val oldBase = s.currentBase
                val value = if (s.result.isNotEmpty()) {
                    BaseNEngine.toLong(s.result, oldBase)
                } else if (s.expression.isNotEmpty() && !containsOperator(s.expression)) {
                    BaseNEngine.toLong(s.expression, oldBase)
                } else {
                    currentValue
                }
                currentValue = value
                val newResult = if (s.result.isNotEmpty()) {
                    BaseNEngine.toString(value, base)
                } else {
                    s.result
                }
                val newExpr = if (s.expression.isNotEmpty() && !containsOperator(s.expression)) {
                    BaseNEngine.toString(value, base)
                } else if (hasResult) {
                    BaseNEngine.toString(value, base)
                } else {
                    // If there's a complex expression, clear it since base changed
                    ""
                }
                s.copy(
                    currentBase = base,
                    expression = newExpr,
                    result = newResult,
                    error = null,
                )
            } catch (e: Exception) {
                s.copy(currentBase = base, error = e.message ?: "Error")
            }
        }
    }

    // ---------------------------------------------------------------
    // Clear / Delete
    // ---------------------------------------------------------------

    fun onClear() {
        currentValue = 0L
        hasResult = false
        _state.update { BaseNState(currentBase = it.currentBase) }
    }

    fun onDelete() {
        _state.update { s ->
            if (s.expression.isNotEmpty()) {
                // Remove trailing logical op keyword if present
                val trimmed = dropTrailingToken(s.expression)
                s.copy(expression = trimmed, error = null)
            } else {
                s
            }
        }
        liveEvaluate()
    }

    fun onOpenParen() {
        _state.update { s ->
            if (hasResult) {
                hasResult = false
                s.copy(expression = "(", error = null)
            } else {
                s.copy(expression = s.expression + "(", error = null)
            }
        }
    }

    fun onCloseParen() {
        _state.update { s ->
            s.copy(expression = s.expression + ")", error = null)
        }
        liveEvaluate()
    }

    // ---------------------------------------------------------------
    // Expression evaluation
    // ---------------------------------------------------------------

    private fun evaluateExpression(expr: String, base: NumberBase): Long {
        if (expr.isBlank()) return 0L
        val tokens = tokenize(expr, base)
        return parseExpression(tokens, base)
    }

    /**
     * Simple recursive-descent parser for:
     *   expr     -> addExpr
     *   addExpr  -> mulExpr (('+' | '-') mulExpr)*
     *   mulExpr  -> logicExpr (('x' | '/') logicExpr)*
     *   logicExpr -> primary (('AND' | 'OR' | 'XOR' | 'XNOR') primary)*
     *   primary  -> '(' expr ')' | number
     */
    private fun parseExpression(tokens: List<BaseNToken>, base: NumberBase): Long {
        val parser = BaseNParser(tokens, base)
        val result = parser.parseAdditive()
        return result
    }

    // ---------------------------------------------------------------
    // Tokenizer
    // ---------------------------------------------------------------

    private sealed class BaseNToken {
        data class Num(val value: String) : BaseNToken()
        data class Op(val op: String) : BaseNToken()
        data object LParen : BaseNToken()
        data object RParen : BaseNToken()
        data object End : BaseNToken()
    }

    private fun tokenize(expr: String, base: NumberBase): List<BaseNToken> {
        val tokens = mutableListOf<BaseNToken>()
        var i = 0
        val s = expr.trim()
        val validDigits = when (base) {
            NumberBase.BIN -> "01"
            NumberBase.OCT -> "01234567"
            NumberBase.DEC -> "0123456789"
            NumberBase.HEX -> "0123456789ABCDEF"
        }

        while (i < s.length) {
            when {
                s[i] == ' ' -> i++
                s[i] == '(' -> { tokens.add(BaseNToken.LParen); i++ }
                s[i] == ')' -> { tokens.add(BaseNToken.RParen); i++ }
                s[i] == '+' -> { tokens.add(BaseNToken.Op("+")); i++ }
                s[i] == '\u00D7' -> { tokens.add(BaseNToken.Op("\u00D7")); i++ }
                s[i] == '\u00F7' -> { tokens.add(BaseNToken.Op("\u00F7")); i++ }
                s[i] == '-' || s[i] == '\u2212' -> {
                    // Negative sign vs subtraction: if previous token is a number or rparen, it's subtraction
                    if (tokens.isNotEmpty() && (tokens.last() is BaseNToken.Num || tokens.last() is BaseNToken.RParen)) {
                        tokens.add(BaseNToken.Op("-"))
                        i++
                    } else if (base == NumberBase.DEC) {
                        // Unary minus in DEC mode: read the negative number
                        i++ // skip the minus
                        val start = i
                        while (i < s.length && s[i].uppercaseChar() in validDigits) i++
                        val numStr = s.substring(start, i)
                        if (numStr.isNotEmpty()) {
                            tokens.add(BaseNToken.Num("-$numStr"))
                        } else {
                            tokens.add(BaseNToken.Op("-"))
                        }
                    } else {
                        tokens.add(BaseNToken.Op("-"))
                        i++
                    }
                }
                s.startsWith("AND", i, ignoreCase = true) -> {
                    tokens.add(BaseNToken.Op("AND")); i += 3
                }
                s.startsWith("OR", i, ignoreCase = true) && (i + 2 >= s.length || s[i + 2].uppercaseChar() !in 'A'..'Z') -> {
                    tokens.add(BaseNToken.Op("OR")); i += 2
                }
                s.startsWith("XOR", i, ignoreCase = true) -> {
                    tokens.add(BaseNToken.Op("XOR")); i += 3
                }
                s.startsWith("XNOR", i, ignoreCase = true) -> {
                    tokens.add(BaseNToken.Op("XNOR")); i += 4
                }
                s[i].uppercaseChar() in validDigits -> {
                    val start = i
                    while (i < s.length && s[i].uppercaseChar() in validDigits) i++
                    tokens.add(BaseNToken.Num(s.substring(start, i).uppercase()))
                }
                else -> i++ // skip unknown chars
            }
        }
        tokens.add(BaseNToken.End)
        return tokens
    }

    private inner class BaseNParser(
        private val tokens: List<BaseNToken>,
        private val base: NumberBase,
    ) {
        private var pos = 0

        private fun current(): BaseNToken = tokens.getOrElse(pos) { BaseNToken.End }

        private fun consume(): BaseNToken {
            val t = current()
            pos++
            return t
        }

        fun parseAdditive(): Long {
            var left = parseMultiplicative()
            while (true) {
                when ((current() as? BaseNToken.Op)?.op) {
                    "+" -> { consume(); left = BaseNEngine.add(left, parseMultiplicative()) }
                    "-" -> { consume(); left = BaseNEngine.subtract(left, parseMultiplicative()) }
                    else -> break
                }
            }
            return left
        }

        private fun parseMultiplicative(): Long {
            var left = parseLogic()
            while (true) {
                when ((current() as? BaseNToken.Op)?.op) {
                    "\u00D7" -> { consume(); left = BaseNEngine.multiply(left, parseLogic()) }
                    "\u00F7" -> { consume(); left = BaseNEngine.divide(left, parseLogic()) }
                    else -> break
                }
            }
            return left
        }

        private fun parseLogic(): Long {
            var left = parsePrimary()
            while (true) {
                when ((current() as? BaseNToken.Op)?.op) {
                    "AND" -> { consume(); left = BaseNEngine.and(left, parsePrimary()) }
                    "OR" -> { consume(); left = BaseNEngine.or(left, parsePrimary()) }
                    "XOR" -> { consume(); left = BaseNEngine.xor(left, parsePrimary()) }
                    "XNOR" -> { consume(); left = BaseNEngine.xnor(left, parsePrimary()) }
                    else -> break
                }
            }
            return left
        }

        private fun parsePrimary(): Long {
            return when (val t = current()) {
                is BaseNToken.LParen -> {
                    consume() // skip (
                    val value = parseAdditive()
                    if (current() is BaseNToken.RParen) consume() // skip )
                    value
                }
                is BaseNToken.Num -> {
                    consume()
                    BaseNEngine.toLong(t.value, base)
                }
                is BaseNToken.Op -> {
                    // Handle unary minus
                    if (t.op == "-") {
                        consume()
                        BaseNEngine.neg(parsePrimary())
                    } else {
                        0L
                    }
                }
                else -> 0L
            }
        }
    }

    // ---------------------------------------------------------------
    // Live evaluation (silent preview)
    // ---------------------------------------------------------------

    private fun liveEvaluate() {
        _state.update { s ->
            try {
                if (s.expression.isNotEmpty() && !endsWithOperator(s.expression)) {
                    val value = evaluateExpression(s.expression, s.currentBase)
                    currentValue = value
                    val resultStr = BaseNEngine.toString(value, s.currentBase)
                    s.copy(result = resultStr)
                } else {
                    s
                }
            } catch (_: Exception) {
                // Don't show errors during live typing
                s
            }
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private fun isValidDigitForBase(digit: String, base: NumberBase): Boolean {
        val d = digit.uppercase()
        return when (base) {
            NumberBase.BIN -> d in listOf("0", "1")
            NumberBase.OCT -> d.length == 1 && d[0] in '0'..'7'
            NumberBase.DEC -> d.length == 1 && d[0] in '0'..'9'
            NumberBase.HEX -> d.length == 1 && d[0] in "0123456789ABCDEF"
        }
    }

    private fun endsWithOperator(expr: String): Boolean {
        val trimmed = expr.trimEnd()
        if (trimmed.isEmpty()) return false
        val ops = listOf("+", "-", "\u00D7", "\u00F7", "\u2212")
        if (ops.any { trimmed.endsWith(it) }) return true
        val logicOps = listOf("AND", "OR", "XOR", "XNOR")
        return logicOps.any { trimmed.endsWith(it, ignoreCase = true) }
    }

    private fun containsOperator(expr: String): Boolean {
        val ops = listOf("+", "-", "\u00D7", "\u00F7", "\u2212", "AND", "OR", "XOR", "XNOR")
        return ops.any { expr.contains(it, ignoreCase = true) }
    }

    private fun replaceTrailingOperator(expr: String, newOp: String): String {
        val trimmed = expr.trimEnd()
        val logicOps = listOf("XNOR", "XOR", "AND", "OR") // longest first
        for (op in logicOps) {
            if (trimmed.endsWith(op, ignoreCase = true)) {
                return trimmed.dropLast(op.length).trimEnd() + newOp
            }
        }
        // Single-char operator
        val singleOps = listOf('+', '-', '\u00D7', '\u00F7', '\u2212')
        if (trimmed.isNotEmpty() && trimmed.last() in singleOps) {
            return trimmed.dropLast(1) + newOp
        }
        return expr + newOp
    }

    private fun extractLastOperand(expr: String, base: NumberBase): Long {
        // Find the last number token in the expression
        val tokens = tokenize(expr, base)
        var lastNum = 0L
        for (t in tokens) {
            if (t is BaseNToken.Num) {
                lastNum = BaseNEngine.toLong(t.value, base)
            }
        }
        // If expression is just a number, evaluate it fully
        return try {
            evaluateExpression(expr, base)
        } catch (_: Exception) {
            lastNum
        }
    }

    private fun dropLastOperand(expr: String, @Suppress("UNUSED_PARAMETER") base: NumberBase): String {
        // For unary ops, we replace the entire expression result, so return empty prefix
        // unless there's an operator before
        val trimmed = expr.trimEnd()
        val logicOps = listOf("XNOR", "XOR", "AND", "OR")
        for (op in logicOps) {
            val idx = trimmed.lastIndexOf(op, ignoreCase = true)
            if (idx >= 0) {
                val afterOp = idx + op.length
                if (afterOp < trimmed.length) {
                    return trimmed.substring(0, afterOp) + " "
                }
            }
        }
        val singleOps = listOf('+', '-', '\u00D7', '\u00F7', '\u2212')
        val lastOpIdx = trimmed.indexOfLast { it in singleOps }
        if (lastOpIdx > 0) {
            return trimmed.substring(0, lastOpIdx + 1)
        }
        return ""
    }

    private fun dropTrailingToken(expr: String): String {
        val trimmed = expr.trimEnd()
        if (trimmed.isEmpty()) return ""
        // Check for trailing logic op keyword with spaces
        val logicOps = listOf(" XNOR ", " XOR ", " AND ", " OR ")
        for (op in logicOps) {
            if (trimmed.endsWith(op.trimEnd(), ignoreCase = true)) {
                return trimmed.dropLast(op.trimEnd().length)
            }
        }
        // Drop last character
        return trimmed.dropLast(1)
    }
}
