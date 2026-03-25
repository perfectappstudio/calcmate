package com.calcmate.scientificcalculator.core.parser

class Lexer(private val input: String) {

    private var pos = 0
    private val tokens = mutableListOf<Token>()

    fun tokenize(): List<Token> {
        while (pos < input.length) {
            when {
                input[pos].isWhitespace() -> pos++
                input[pos].isDigit() || (input[pos] == '.' && pos + 1 < input.length && input[pos + 1].isDigit()) -> readNumber()
                input[pos].isLetter() || input[pos] == 'π' -> readIdentifier()
                else -> readOperator()
            }
        }
        tokens.add(Token(TokenType.EOF))
        return tokens
    }

    private fun readNumber() {
        val start = pos
        while (pos < input.length && input[pos].isDigit()) pos++
        if (pos < input.length && input[pos] == '.') {
            pos++
            while (pos < input.length && input[pos].isDigit()) pos++
        }
        if (pos < input.length && (input[pos] == 'e' || input[pos] == 'E')) {
            pos++
            if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) pos++
            while (pos < input.length && input[pos].isDigit()) pos++
        }
        val numberStr = input.substring(start, pos)
        addWithImplicitMultiply(Token(TokenType.NUMBER, numberStr))
    }

    private fun readIdentifier() {
        if (input[pos] == 'π') {
            pos++
            addWithImplicitMultiply(Token(TokenType.PI, "π"))
            return
        }

        val start = pos
        while (pos < input.length && input[pos].isLetter()) pos++
        val name = input.substring(start, pos)

        val token = when (name) {
            "sin" -> Token(TokenType.SIN, name)
            "cos" -> Token(TokenType.COS, name)
            "tan" -> Token(TokenType.TAN, name)
            "asin", "arcsin" -> Token(TokenType.ASIN, name)
            "acos", "arccos" -> Token(TokenType.ACOS, name)
            "atan", "arctan" -> Token(TokenType.ATAN, name)
            "sinh" -> Token(TokenType.SINH, name)
            "cosh" -> Token(TokenType.COSH, name)
            "tanh" -> Token(TokenType.TANH, name)
            "asinh", "arcsinh" -> Token(TokenType.ASINH, name)
            "acosh", "arccosh" -> Token(TokenType.ACOSH, name)
            "atanh", "arctanh" -> Token(TokenType.ATANH, name)
            "ln" -> Token(TokenType.LN, name)
            "log" -> Token(TokenType.LOG, name)
            "sqrt" -> Token(TokenType.SQRT, name)
            "cbrt" -> Token(TokenType.CBRT, name)
            "abs" -> Token(TokenType.ABS, name)
            "nPr" -> Token(TokenType.NPR, name)
            "nCr" -> Token(TokenType.NCR, name)
            "pi" -> Token(TokenType.PI, name)
            "e" -> Token(TokenType.E, name)
            else -> Token(TokenType.NUMBER, "0") // unknown identifier fallback
        }
        addWithImplicitMultiply(token)
    }

    private fun readOperator() {
        val token = when (input[pos]) {
            '+' -> Token(TokenType.PLUS, "+")
            '-' -> Token(TokenType.MINUS, "-")
            '*', '×' -> Token(TokenType.MULTIPLY, "*")
            '/', '÷' -> Token(TokenType.DIVIDE, "/")
            '^' -> Token(TokenType.POWER, "^")
            '!' -> Token(TokenType.FACTORIAL, "!")
            '%' -> Token(TokenType.MOD, "%")
            '(' -> {
                pos++
                addWithImplicitMultiply(Token(TokenType.LPAREN, "("))
                return
            }
            ')' -> Token(TokenType.RPAREN, ")")
            ',' -> Token(TokenType.COMMA, ",")
            else -> {
                pos++
                return
            }
        }
        pos++
        tokens.add(token)
    }

    private fun addWithImplicitMultiply(token: Token) {
        if (tokens.isNotEmpty()) {
            val last = tokens.last()
            val needsMultiply = when (last.type) {
                TokenType.NUMBER, TokenType.PI, TokenType.E,
                TokenType.RPAREN, TokenType.FACTORIAL -> {
                    token.type in setOf(
                        TokenType.NUMBER, TokenType.PI, TokenType.E,
                        TokenType.LPAREN,
                        TokenType.SIN, TokenType.COS, TokenType.TAN,
                        TokenType.ASIN, TokenType.ACOS, TokenType.ATAN,
                        TokenType.SINH, TokenType.COSH, TokenType.TANH,
                        TokenType.ASINH, TokenType.ACOSH, TokenType.ATANH,
                        TokenType.LN, TokenType.LOG,
                        TokenType.SQRT, TokenType.CBRT, TokenType.ABS,
                        TokenType.NPR, TokenType.NCR
                    )
                }
                else -> false
            }
            if (needsMultiply) {
                tokens.add(Token(TokenType.MULTIPLY, "*"))
            }
        }
        tokens.add(token)
    }
}
