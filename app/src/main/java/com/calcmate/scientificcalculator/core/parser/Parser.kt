package com.calcmate.scientificcalculator.core.parser

class Parser(private val tokens: List<Token>) {

    private var pos = 0

    private val functionTokenTypes = setOf(
        TokenType.SIN, TokenType.COS, TokenType.TAN,
        TokenType.ASIN, TokenType.ACOS, TokenType.ATAN,
        TokenType.SINH, TokenType.COSH, TokenType.TANH,
        TokenType.ASINH, TokenType.ACOSH, TokenType.ATANH,
        TokenType.LN, TokenType.LOG,
        TokenType.SQRT, TokenType.CBRT, TokenType.ABS,
        TokenType.NPR, TokenType.NCR
    )

    fun parse(): ASTNode {
        val node = parseExpression()
        expect(TokenType.EOF)
        return node
    }

    private fun current(): Token = tokens[pos]

    private fun advance(): Token {
        val token = tokens[pos]
        pos++
        return token
    }

    private fun expect(type: TokenType): Token {
        if (current().type != type) {
            throw ParseException("Expected $type but found ${current().type}")
        }
        return advance()
    }

    private fun parseExpression(): ASTNode {
        var node = parseTerm()
        while (current().type == TokenType.PLUS || current().type == TokenType.MINUS) {
            val op = advance().type
            val right = parseTerm()
            node = ASTNode.BinaryOpNode(op, node, right)
        }
        return node
    }

    private fun parseTerm(): ASTNode {
        var node = parseExponent()
        while (current().type in setOf(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MOD)) {
            val op = advance().type
            val right = parseExponent()
            node = ASTNode.BinaryOpNode(op, node, right)
        }
        return node
    }

    // Right-associative: a^b^c = a^(b^c)
    private fun parseExponent(): ASTNode {
        val base = parseUnary()
        if (current().type == TokenType.POWER) {
            advance()
            val exponent = parseExponent()
            return ASTNode.BinaryOpNode(TokenType.POWER, base, exponent)
        }
        return base
    }

    private fun parseUnary(): ASTNode {
        if (current().type == TokenType.MINUS) {
            advance()
            val operand = parseUnary()
            return ASTNode.NegationNode(operand)
        }
        if (current().type == TokenType.PLUS) {
            advance()
            return parseUnary()
        }
        return parsePostfix()
    }

    private fun parsePostfix(): ASTNode {
        var node = parsePrimary()
        while (current().type == TokenType.FACTORIAL || current().type == TokenType.PERCENT) {
            when (advance().type) {
                TokenType.FACTORIAL -> node = ASTNode.FactorialNode(node)
                TokenType.PERCENT -> node = ASTNode.PercentNode(node)
                else -> {}
            }
        }
        return node
    }

    private fun parsePrimary(): ASTNode {
        return when (current().type) {
            TokenType.NUMBER -> {
                val value = advance().value.toDouble()
                ASTNode.NumberNode(value)
            }

            TokenType.PI -> {
                advance()
                ASTNode.ConstantNode(TokenType.PI)
            }

            TokenType.E -> {
                advance()
                ASTNode.ConstantNode(TokenType.E)
            }

            TokenType.LPAREN -> {
                advance()
                val node = parseExpression()
                expect(TokenType.RPAREN)
                node
            }

            TokenType.VARIABLE -> {
                val name = advance().value[0]
                ASTNode.VariableNode(name)
            }

            TokenType.ANS -> {
                advance()
                ASTNode.AnsNode
            }

            TokenType.RANDOM -> {
                advance()
                ASTNode.RandomNode
            }

            TokenType.LOG -> parseLogFunction()

            TokenType.NPR, TokenType.NCR -> parseTwoArgFunction()

            in functionTokenTypes -> parseFunction()

            else -> throw ParseException("Unexpected token: ${current().type} (${current().value})")
        }
    }

    private fun parseFunction(): ASTNode {
        val func = advance().type
        expect(TokenType.LPAREN)
        val argument = parseExpression()
        expect(TokenType.RPAREN)
        return ASTNode.UnaryFuncNode(func, argument)
    }

    private fun parseLogFunction(): ASTNode {
        advance() // consume LOG
        expect(TokenType.LPAREN)
        val first = parseExpression()

        return if (current().type == TokenType.COMMA) {
            advance()
            val second = parseExpression()
            expect(TokenType.RPAREN)
            ASTNode.LogBaseNode(base = first, argument = second)
        } else {
            expect(TokenType.RPAREN)
            ASTNode.UnaryFuncNode(TokenType.LOG, first)
        }
    }

    private fun parseTwoArgFunction(): ASTNode {
        val type = advance().type
        expect(TokenType.LPAREN)
        val n = parseExpression()
        expect(TokenType.COMMA)
        val r = parseExpression()
        expect(TokenType.RPAREN)
        return ASTNode.PermCombNode(type, n, r)
    }
}

class ParseException(message: String) : RuntimeException(message)
