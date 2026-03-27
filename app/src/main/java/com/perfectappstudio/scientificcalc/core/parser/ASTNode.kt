package com.perfectappstudio.scientificcalc.core.parser

sealed class ASTNode {
    data class NumberNode(val value: Double) : ASTNode()
    data class BinaryOpNode(val op: TokenType, val left: ASTNode, val right: ASTNode) : ASTNode()
    data class UnaryFuncNode(val func: TokenType, val argument: ASTNode) : ASTNode()
    data class LogBaseNode(val base: ASTNode, val argument: ASTNode) : ASTNode()
    data class ConstantNode(val type: TokenType) : ASTNode()
    data class PermCombNode(val type: TokenType, val n: ASTNode, val r: ASTNode) : ASTNode()
    data class NegationNode(val operand: ASTNode) : ASTNode()
    data class FactorialNode(val operand: ASTNode) : ASTNode()
    data class VariableNode(val name: Char) : ASTNode()
    data object AnsNode : ASTNode()
    data class PercentNode(val operand: ASTNode) : ASTNode()
    data object RandomNode : ASTNode()
}
