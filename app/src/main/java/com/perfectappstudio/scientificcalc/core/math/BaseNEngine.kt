package com.perfectappstudio.scientificcalc.core.math

import com.perfectappstudio.scientificcalc.core.parser.NumberBase

/**
 * Engine for base-N integer arithmetic and logical operations.
 *
 * All operations are performed on 32-bit integers:
 * - DEC range: -2_147_483_648 .. 2_147_483_647 (signed)
 * - BIN/OCT/HEX: unsigned 32-bit representation (two's complement for negatives)
 */
object BaseNEngine {

    private const val MASK_32 = 0xFFFFFFFFL // 32-bit mask

    // ---------------------------------------------------------------
    // Conversion
    // ---------------------------------------------------------------

    /**
     * Parse a string in the given [base] and return its Long value.
     * For BIN/OCT/HEX the value is treated as unsigned 32-bit, then
     * sign-extended to a signed 32-bit integer stored in a Long.
     */
    fun toLong(value: String, base: NumberBase): Long {
        val cleaned = value.trim().uppercase()
        if (cleaned.isEmpty()) return 0L

        val radix = base.radix()

        return if (base == NumberBase.DEC) {
            // DEC accepts a leading minus sign
            val parsed = cleaned.toLong(radix)
            clampTo32Bit(parsed)
        } else {
            // Non-DEC: parse as unsigned, then sign-extend from 32-bit
            val unsigned = cleaned.toULong(radix)
            if (unsigned > MASK_32.toULong()) {
                throw IllegalArgumentException("Value exceeds 32-bit range")
            }
            signExtend32(unsigned.toLong() and MASK_32)
        }
    }

    /**
     * Format a signed 32-bit value as a string in the given [base].
     * BIN/OCT/HEX display unsigned two's complement representation.
     */
    fun toString(value: Long, base: NumberBase): String {
        val clamped = clampTo32Bit(value)
        return when (base) {
            NumberBase.DEC -> clamped.toString()
            else -> {
                val unsigned = clamped.toInt().toUInt().toLong()
                unsigned.toString(base.radix()).uppercase()
            }
        }
    }

    // ---------------------------------------------------------------
    // Logical operations (32-bit)
    // ---------------------------------------------------------------

    fun and(a: Long, b: Long): Long = signExtend32((a and b) and MASK_32)

    fun or(a: Long, b: Long): Long = signExtend32((a or b) and MASK_32)

    fun xor(a: Long, b: Long): Long = signExtend32((a xor b) and MASK_32)

    fun xnor(a: Long, b: Long): Long = signExtend32((a xor b).inv() and MASK_32)

    /** Bitwise complement (32-bit). */
    fun not(a: Long): Long = signExtend32(a.inv() and MASK_32)

    /** Two's complement negation (32-bit). */
    fun neg(a: Long): Long = clampTo32Bit(-a)

    // ---------------------------------------------------------------
    // Arithmetic (32-bit, integer only)
    // ---------------------------------------------------------------

    fun add(a: Long, b: Long): Long = clampTo32Bit(a + b)

    fun subtract(a: Long, b: Long): Long = clampTo32Bit(a - b)

    fun multiply(a: Long, b: Long): Long = clampTo32Bit(a * b)

    fun divide(a: Long, b: Long): Long {
        if (b == 0L) throw ArithmeticException("Division by zero")
        return clampTo32Bit(a / b)
    }

    // ---------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------

    fun isValidForBase(value: String, base: NumberBase): Boolean {
        val cleaned = value.trim().uppercase()
        if (cleaned.isEmpty()) return true
        val validChars = when (base) {
            NumberBase.BIN -> "01"
            NumberBase.OCT -> "01234567"
            NumberBase.DEC -> "0123456789"
            NumberBase.HEX -> "0123456789ABCDEF"
        }
        val startIndex = if (base == NumberBase.DEC && cleaned.startsWith("-")) 1 else 0
        return cleaned.substring(startIndex).all { it in validChars }
    }

    fun clampTo32Bit(value: Long): Long {
        // Truncate to 32-bit and sign-extend
        return (value.toInt()).toLong()
    }

    // ---------------------------------------------------------------
    // Internal
    // ---------------------------------------------------------------

    private fun signExtend32(value: Long): Long = (value.toInt()).toLong()

    private fun NumberBase.radix(): Int = when (this) {
        NumberBase.BIN -> 2
        NumberBase.OCT -> 8
        NumberBase.DEC -> 10
        NumberBase.HEX -> 16
    }
}
