package com.calcmate.scientificcalculator.core.parser

enum class TokenType {
    NUMBER,
    PLUS, MINUS, MULTIPLY, DIVIDE, POWER,
    LPAREN, RPAREN,
    MOD, FACTORIAL,
    SIN, COS, TAN,
    ASIN, ACOS, ATAN,
    SINH, COSH, TANH,
    ASINH, ACOSH, ATANH,
    LN, LOG, LOG_BASE,
    SQRT, CBRT, ABS,
    PI, E,
    NPR, NCR,
    COMMA,
    EOF
}

data class Token(
    val type: TokenType,
    val value: String = ""
)
