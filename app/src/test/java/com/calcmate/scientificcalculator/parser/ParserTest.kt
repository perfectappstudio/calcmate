package com.calcmate.scientificcalculator.parser

import com.calcmate.scientificcalculator.core.parser.ASTNode
import com.calcmate.scientificcalculator.core.parser.Lexer
import com.calcmate.scientificcalculator.core.parser.Parser
import com.calcmate.scientificcalculator.core.parser.TokenType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParserTest {

    private fun parse(input: String): ASTNode =
        Parser(Lexer(input).tokenize()).parse()

    // ---------------------------------------------------------------
    // Simple binary operations
    // ---------------------------------------------------------------

    @Test
    fun `parse addition 2+3`() {
        val ast = parse("2+3")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.PLUS, node.op)
        assertEquals(2.0, (node.left as ASTNode.NumberNode).value, 1e-9)
        assertEquals(3.0, (node.right as ASTNode.NumberNode).value, 1e-9)
    }

    @Test
    fun `parse subtraction 10-5`() {
        val ast = parse("10-5")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.MINUS, node.op)
        assertEquals(10.0, (node.left as ASTNode.NumberNode).value, 1e-9)
        assertEquals(5.0, (node.right as ASTNode.NumberNode).value, 1e-9)
    }

    @Test
    fun `parse multiplication 6 times 7`() {
        val ast = parse("6*7")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.MULTIPLY, node.op)
    }

    @Test
    fun `parse division 8 div 4`() {
        val ast = parse("8/4")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.DIVIDE, node.op)
    }

    // ---------------------------------------------------------------
    // Operator precedence
    // ---------------------------------------------------------------

    @Test
    fun `precedence multiply before add -- 2+3 times 4 gives 14 structure`() {
        // 2+3*4 should parse as 2+(3*4)
        val ast = parse("2+3*4")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.PLUS, node.op)

        // left is just 2
        assertEquals(2.0, (node.left as ASTNode.NumberNode).value, 1e-9)

        // right is 3*4
        val right = node.right as ASTNode.BinaryOpNode
        assertEquals(TokenType.MULTIPLY, right.op)
        assertEquals(3.0, (right.left as ASTNode.NumberNode).value, 1e-9)
        assertEquals(4.0, (right.right as ASTNode.NumberNode).value, 1e-9)
    }

    @Test
    fun `power is right associative -- 2^3^2 parses as 2^(3^2)`() {
        val ast = parse("2^3^2")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.POWER, node.op)

        // left is just 2
        assertEquals(2.0, (node.left as ASTNode.NumberNode).value, 1e-9)

        // right is 3^2
        val right = node.right as ASTNode.BinaryOpNode
        assertEquals(TokenType.POWER, right.op)
        assertEquals(3.0, (right.left as ASTNode.NumberNode).value, 1e-9)
        assertEquals(2.0, (right.right as ASTNode.NumberNode).value, 1e-9)
    }

    // ---------------------------------------------------------------
    // Parentheses
    // ---------------------------------------------------------------

    @Test
    fun `parentheses override precedence -- (2+3) times 4`() {
        val ast = parse("(2+3)*4")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.MULTIPLY, node.op)

        // left is (2+3)
        val left = node.left as ASTNode.BinaryOpNode
        assertEquals(TokenType.PLUS, left.op)
        assertEquals(2.0, (left.left as ASTNode.NumberNode).value, 1e-9)
        assertEquals(3.0, (left.right as ASTNode.NumberNode).value, 1e-9)

        // right is 4
        assertEquals(4.0, (node.right as ASTNode.NumberNode).value, 1e-9)
    }

    @Test
    fun `nested parentheses`() {
        val ast = parse("((1+2))")
        assertTrue(ast is ASTNode.BinaryOpNode)
        val node = ast as ASTNode.BinaryOpNode
        assertEquals(TokenType.PLUS, node.op)
    }

    // ---------------------------------------------------------------
    // Nested functions
    // ---------------------------------------------------------------

    @Test
    fun `nested functions sin(cos(0))`() {
        val ast = parse("sin(cos(0))")
        assertTrue(ast is ASTNode.UnaryFuncNode)
        val outer = ast as ASTNode.UnaryFuncNode
        assertEquals(TokenType.SIN, outer.func)

        val inner = outer.argument as ASTNode.UnaryFuncNode
        assertEquals(TokenType.COS, inner.func)
        assertEquals(0.0, (inner.argument as ASTNode.NumberNode).value, 1e-9)
    }

    // ---------------------------------------------------------------
    // Unary minus
    // ---------------------------------------------------------------

    @Test
    fun `unary minus on literal`() {
        val ast = parse("-5")
        assertTrue(ast is ASTNode.NegationNode)
        val neg = ast as ASTNode.NegationNode
        assertEquals(5.0, (neg.operand as ASTNode.NumberNode).value, 1e-9)
    }

    @Test
    fun `unary minus on parenthesized expression`() {
        val ast = parse("-(3+2)")
        assertTrue(ast is ASTNode.NegationNode)
        val neg = ast as ASTNode.NegationNode
        assertTrue(neg.operand is ASTNode.BinaryOpNode)
    }

    // ---------------------------------------------------------------
    // Factorial
    // ---------------------------------------------------------------

    @Test
    fun `factorial node 5!`() {
        val ast = parse("5!")
        assertTrue(ast is ASTNode.FactorialNode)
        val fac = ast as ASTNode.FactorialNode
        assertEquals(5.0, (fac.operand as ASTNode.NumberNode).value, 1e-9)
    }

    // ---------------------------------------------------------------
    // Multi-argument functions
    // ---------------------------------------------------------------

    @Test
    fun `log with base -- log(2,8)`() {
        val ast = parse("log(2,8)")
        assertTrue(ast is ASTNode.LogBaseNode)
        val logNode = ast as ASTNode.LogBaseNode
        assertEquals(2.0, (logNode.base as ASTNode.NumberNode).value, 1e-9)
        assertEquals(8.0, (logNode.argument as ASTNode.NumberNode).value, 1e-9)
    }

    @Test
    fun `log without base -- log(100) is unary`() {
        val ast = parse("log(100)")
        assertTrue(ast is ASTNode.UnaryFuncNode)
        val func = ast as ASTNode.UnaryFuncNode
        assertEquals(TokenType.LOG, func.func)
    }

    @Test
    fun `nPr(5,2) parsed as PermCombNode`() {
        val ast = parse("nPr(5,2)")
        assertTrue(ast is ASTNode.PermCombNode)
        val node = ast as ASTNode.PermCombNode
        assertEquals(TokenType.NPR, node.type)
        assertEquals(5.0, (node.n as ASTNode.NumberNode).value, 1e-9)
        assertEquals(2.0, (node.r as ASTNode.NumberNode).value, 1e-9)
    }

    @Test
    fun `nCr(5,2) parsed as PermCombNode`() {
        val ast = parse("nCr(5,2)")
        assertTrue(ast is ASTNode.PermCombNode)
        val node = ast as ASTNode.PermCombNode
        assertEquals(TokenType.NCR, node.type)
        assertEquals(5.0, (node.n as ASTNode.NumberNode).value, 1e-9)
        assertEquals(2.0, (node.r as ASTNode.NumberNode).value, 1e-9)
    }
}
