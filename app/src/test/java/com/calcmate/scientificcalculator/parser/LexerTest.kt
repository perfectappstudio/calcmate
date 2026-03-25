package com.calcmate.scientificcalculator.parser

import com.calcmate.scientificcalculator.core.parser.Lexer
import com.calcmate.scientificcalculator.core.parser.TokenType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LexerTest {

    private fun tokenTypes(input: String): List<TokenType> =
        Lexer(input).tokenize().map { it.type }

    private fun tokenValues(input: String): List<String> =
        Lexer(input).tokenize().map { it.value }

    // ---------------------------------------------------------------
    // Simple number tokenization
    // ---------------------------------------------------------------

    @Test
    fun `tokenize integer`() {
        val tokens = Lexer("42").tokenize()
        assertEquals(2, tokens.size)
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("42", tokens[0].value)
        assertEquals(TokenType.EOF, tokens[1].type)
    }

    @Test
    fun `tokenize decimal number`() {
        val tokens = Lexer("3.14").tokenize()
        assertEquals(2, tokens.size)
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("3.14", tokens[0].value)
    }

    @Test
    fun `tokenize scientific notation`() {
        val tokens = Lexer("1.5e10").tokenize()
        assertEquals(2, tokens.size)
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("1.5e10", tokens[0].value)
    }

    @Test
    fun `tokenize scientific notation with positive exponent`() {
        val tokens = Lexer("2.5E+3").tokenize()
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("2.5E+3", tokens[0].value)
    }

    @Test
    fun `tokenize scientific notation with negative exponent`() {
        val tokens = Lexer("6.02e-23").tokenize()
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("6.02e-23", tokens[0].value)
    }

    @Test
    fun `tokenize leading decimal point`() {
        val tokens = Lexer(".5").tokenize()
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals(".5", tokens[0].value)
    }

    // ---------------------------------------------------------------
    // Operator tokenization
    // ---------------------------------------------------------------

    @Test
    fun `tokenize plus operator`() {
        val types = tokenTypes("2+3")
        assertEquals(listOf(TokenType.NUMBER, TokenType.PLUS, TokenType.NUMBER, TokenType.EOF), types)
    }

    @Test
    fun `tokenize minus operator`() {
        val types = tokenTypes("10-5")
        assertEquals(listOf(TokenType.NUMBER, TokenType.MINUS, TokenType.NUMBER, TokenType.EOF), types)
    }

    @Test
    fun `tokenize multiply operator`() {
        val types = tokenTypes("6*7")
        assertEquals(listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.NUMBER, TokenType.EOF), types)
    }

    @Test
    fun `tokenize unicode multiply operator`() {
        val types = tokenTypes("6\u00D77")
        assertEquals(listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.NUMBER, TokenType.EOF), types)
    }

    @Test
    fun `tokenize divide operator`() {
        val types = tokenTypes("8/4")
        assertEquals(listOf(TokenType.NUMBER, TokenType.DIVIDE, TokenType.NUMBER, TokenType.EOF), types)
    }

    @Test
    fun `tokenize power operator`() {
        val types = tokenTypes("2^3")
        assertEquals(listOf(TokenType.NUMBER, TokenType.POWER, TokenType.NUMBER, TokenType.EOF), types)
    }

    @Test
    fun `tokenize factorial operator`() {
        val types = tokenTypes("5!")
        assertEquals(listOf(TokenType.NUMBER, TokenType.FACTORIAL, TokenType.EOF), types)
    }

    @Test
    fun `tokenize mod operator`() {
        val types = tokenTypes("10%3")
        assertEquals(listOf(TokenType.NUMBER, TokenType.MOD, TokenType.NUMBER, TokenType.EOF), types)
    }

    @Test
    fun `tokenize parentheses`() {
        val types = tokenTypes("(2+3)")
        assertEquals(
            listOf(TokenType.LPAREN, TokenType.NUMBER, TokenType.PLUS, TokenType.NUMBER, TokenType.RPAREN, TokenType.EOF),
            types
        )
    }

    @Test
    fun `tokenize comma`() {
        val types = tokenTypes("log(2,8)")
        assertTrue(types.contains(TokenType.COMMA))
    }

    // ---------------------------------------------------------------
    // Function name recognition
    // ---------------------------------------------------------------

    @Test
    fun `tokenize sin function`() {
        val types = tokenTypes("sin(0)")
        assertEquals(TokenType.SIN, types[0])
    }

    @Test
    fun `tokenize cos function`() {
        val types = tokenTypes("cos(0)")
        assertEquals(TokenType.COS, types[0])
    }

    @Test
    fun `tokenize tan function`() {
        val types = tokenTypes("tan(0)")
        assertEquals(TokenType.TAN, types[0])
    }

    @Test
    fun `tokenize ln function`() {
        val types = tokenTypes("ln(1)")
        assertEquals(TokenType.LN, types[0])
    }

    @Test
    fun `tokenize log function`() {
        val types = tokenTypes("log(10)")
        assertEquals(TokenType.LOG, types[0])
    }

    @Test
    fun `tokenize sqrt function`() {
        val types = tokenTypes("sqrt(4)")
        assertEquals(TokenType.SQRT, types[0])
    }

    @Test
    fun `tokenize cbrt function`() {
        val types = tokenTypes("cbrt(8)")
        assertEquals(TokenType.CBRT, types[0])
    }

    @Test
    fun `tokenize abs function`() {
        val types = tokenTypes("abs(5)")
        assertEquals(TokenType.ABS, types[0])
    }

    @Test
    fun `tokenize inverse trig functions`() {
        assertEquals(TokenType.ASIN, tokenTypes("asin(0)")[0])
        assertEquals(TokenType.ACOS, tokenTypes("acos(1)")[0])
        assertEquals(TokenType.ATAN, tokenTypes("atan(0)")[0])
    }

    @Test
    fun `tokenize hyperbolic functions`() {
        assertEquals(TokenType.SINH, tokenTypes("sinh(0)")[0])
        assertEquals(TokenType.COSH, tokenTypes("cosh(0)")[0])
        assertEquals(TokenType.TANH, tokenTypes("tanh(0)")[0])
    }

    // ---------------------------------------------------------------
    // Constant recognition
    // ---------------------------------------------------------------

    @Test
    fun `tokenize pi keyword`() {
        val tokens = Lexer("pi").tokenize()
        assertEquals(TokenType.PI, tokens[0].type)
        assertEquals("pi", tokens[0].value)
    }

    @Test
    fun `tokenize pi unicode symbol`() {
        val tokens = Lexer("\u03C0").tokenize()
        assertEquals(TokenType.PI, tokens[0].type)
        assertEquals("\u03C0", tokens[0].value)
    }

    @Test
    fun `tokenize e constant`() {
        val tokens = Lexer("e").tokenize()
        assertEquals(TokenType.E, tokens[0].type)
    }

    // ---------------------------------------------------------------
    // Implicit multiplication insertion
    // ---------------------------------------------------------------

    @Test
    fun `implicit multiply number before pi`() {
        // "2pi" -> NUMBER MULTIPLY PI
        val types = tokenTypes("2pi")
        assertEquals(
            listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.PI, TokenType.EOF),
            types
        )
    }

    @Test
    fun `implicit multiply number before unicode pi`() {
        val types = tokenTypes("2\u03C0")
        assertEquals(
            listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.PI, TokenType.EOF),
            types
        )
    }

    @Test
    fun `implicit multiply number before sin`() {
        // "5sin(0)" -> NUMBER MULTIPLY SIN LPAREN NUMBER RPAREN
        val types = tokenTypes("5sin(0)")
        assertEquals(TokenType.NUMBER, types[0])
        assertEquals(TokenType.MULTIPLY, types[1])
        assertEquals(TokenType.SIN, types[2])
    }

    @Test
    fun `implicit multiply between closing and opening paren`() {
        // "(2)(3)" -> LPAREN NUMBER RPAREN MULTIPLY LPAREN NUMBER RPAREN
        val types = tokenTypes("(2)(3)")
        val expected = listOf(
            TokenType.LPAREN, TokenType.NUMBER, TokenType.RPAREN,
            TokenType.MULTIPLY,
            TokenType.LPAREN, TokenType.NUMBER, TokenType.RPAREN,
            TokenType.EOF
        )
        assertEquals(expected, types)
    }

    @Test
    fun `implicit multiply rparen before number`() {
        // "(2)3" -> LPAREN NUMBER RPAREN MULTIPLY NUMBER
        val types = tokenTypes("(2)3")
        assertEquals(TokenType.RPAREN, types[2])
        assertEquals(TokenType.MULTIPLY, types[3])
        assertEquals(TokenType.NUMBER, types[4])
    }

    @Test
    fun `implicit multiply pi before number`() {
        // "pi2" -> PI MULTIPLY NUMBER
        val types = tokenTypes("pi2")
        assertEquals(
            listOf(TokenType.PI, TokenType.MULTIPLY, TokenType.NUMBER, TokenType.EOF),
            types
        )
    }

    @Test
    fun `implicit multiply after factorial before number`() {
        // "5!3" -> NUMBER FACTORIAL MULTIPLY NUMBER
        val types = tokenTypes("5!3")
        assertEquals(TokenType.NUMBER, types[0])
        assertEquals(TokenType.FACTORIAL, types[1])
        assertEquals(TokenType.MULTIPLY, types[2])
        assertEquals(TokenType.NUMBER, types[3])
    }

    @Test
    fun `implicit multiply number before lparen`() {
        // "2(3)" -> NUMBER MULTIPLY LPAREN NUMBER RPAREN
        val types = tokenTypes("2(3)")
        assertEquals(
            listOf(TokenType.NUMBER, TokenType.MULTIPLY, TokenType.LPAREN, TokenType.NUMBER, TokenType.RPAREN, TokenType.EOF),
            types
        )
    }

    // ---------------------------------------------------------------
    // Edge cases
    // ---------------------------------------------------------------

    @Test
    fun `empty string produces only EOF`() {
        val tokens = Lexer("").tokenize()
        assertEquals(1, tokens.size)
        assertEquals(TokenType.EOF, tokens[0].type)
    }

    @Test
    fun `single number`() {
        val tokens = Lexer("7").tokenize()
        assertEquals(2, tokens.size)
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("7", tokens[0].value)
    }

    @Test
    fun `single operator plus`() {
        val tokens = Lexer("+").tokenize()
        assertEquals(2, tokens.size)
        assertEquals(TokenType.PLUS, tokens[0].type)
    }

    @Test
    fun `whitespace is ignored`() {
        val types = tokenTypes("  2  +  3  ")
        assertEquals(
            listOf(TokenType.NUMBER, TokenType.PLUS, TokenType.NUMBER, TokenType.EOF),
            types
        )
    }
}
