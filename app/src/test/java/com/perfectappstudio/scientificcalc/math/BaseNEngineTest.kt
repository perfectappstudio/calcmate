package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.BaseNEngine
import com.perfectappstudio.scientificcalc.core.parser.NumberBase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseNEngineTest {

    // ---------------------------------------------------------------
    // Binary arithmetic
    // ---------------------------------------------------------------

    @Test
    fun `binary addition 10111 + 11010 = 110001`() {
        val a = BaseNEngine.toLong("10111", NumberBase.BIN)  // 23
        val b = BaseNEngine.toLong("11010", NumberBase.BIN)  // 26
        val result = BaseNEngine.add(a, b)                    // 49
        assertEquals("110001", BaseNEngine.toString(result, NumberBase.BIN))
    }

    // ---------------------------------------------------------------
    // Octal arithmetic
    // ---------------------------------------------------------------

    @Test
    fun `octal addition 7 + 1 = 10`() {
        val a = BaseNEngine.toLong("7", NumberBase.OCT)
        val b = BaseNEngine.toLong("1", NumberBase.OCT)
        val result = BaseNEngine.add(a, b)
        assertEquals("10", BaseNEngine.toString(result, NumberBase.OCT))
    }

    // ---------------------------------------------------------------
    // Hex arithmetic
    // ---------------------------------------------------------------

    @Test
    fun `hex addition 1F + 1 = 20`() {
        val a = BaseNEngine.toLong("1F", NumberBase.HEX)
        val b = BaseNEngine.toLong("1", NumberBase.HEX)
        val result = BaseNEngine.add(a, b)
        assertEquals("20", BaseNEngine.toString(result, NumberBase.HEX))
    }

    // ---------------------------------------------------------------
    // Base conversion from decimal
    // ---------------------------------------------------------------

    @Test
    fun `decimal 30 to binary is 11110`() {
        assertEquals("11110", BaseNEngine.toString(30L, NumberBase.BIN))
    }

    @Test
    fun `decimal 30 to octal is 36`() {
        assertEquals("36", BaseNEngine.toString(30L, NumberBase.OCT))
    }

    @Test
    fun `decimal 30 to hex is 1E`() {
        assertEquals("1E", BaseNEngine.toString(30L, NumberBase.HEX))
    }

    // ---------------------------------------------------------------
    // Logical AND
    // ---------------------------------------------------------------

    @Test
    fun `binary AND 1010 AND 1100 = 1000`() {
        val a = BaseNEngine.toLong("1010", NumberBase.BIN)  // 10
        val b = BaseNEngine.toLong("1100", NumberBase.BIN)  // 12
        val result = BaseNEngine.and(a, b)                   // 8
        assertEquals("1000", BaseNEngine.toString(result, NumberBase.BIN))
    }

    // ---------------------------------------------------------------
    // Logical OR
    // ---------------------------------------------------------------

    @Test
    fun `binary OR 1011 OR 11010 = 11011`() {
        val a = BaseNEngine.toLong("1011", NumberBase.BIN)   // 11
        val b = BaseNEngine.toLong("11010", NumberBase.BIN)  // 26
        val result = BaseNEngine.or(a, b)                     // 27
        assertEquals("11011", BaseNEngine.toString(result, NumberBase.BIN))
    }

    // ---------------------------------------------------------------
    // Logical NOT (32-bit complement)
    // ---------------------------------------------------------------

    @Test
    fun `binary NOT 1010 = 32-bit complement`() {
        val a = BaseNEngine.toLong("1010", NumberBase.BIN) // 10
        val result = BaseNEngine.not(a)
        // NOT(10) in 32-bit = 0xFFFFFFF5 = 11111111111111111111111111110101
        assertEquals(
            "11111111111111111111111111110101",
            BaseNEngine.toString(result, NumberBase.BIN),
        )
    }

    // ---------------------------------------------------------------
    // XOR
    // ---------------------------------------------------------------

    @Test
    fun `binary XOR 1010 XOR 1100 = 0110`() {
        val a = BaseNEngine.toLong("1010", NumberBase.BIN) // 10
        val b = BaseNEngine.toLong("1100", NumberBase.BIN) // 12
        val result = BaseNEngine.xor(a, b)                  // 6
        assertEquals("110", BaseNEngine.toString(result, NumberBase.BIN))
    }

    // ---------------------------------------------------------------
    // XNOR
    // ---------------------------------------------------------------

    @Test
    fun `binary XNOR 1010 XNOR 1100 = 32-bit result`() {
        val a = BaseNEngine.toLong("1010", NumberBase.BIN) // 10
        val b = BaseNEngine.toLong("1100", NumberBase.BIN) // 12
        val result = BaseNEngine.xnor(a, b)
        // XNOR = NOT(XOR) = NOT(0110) = FFFFFFF9 in hex
        assertEquals("FFFFFFF9", BaseNEngine.toString(result, NumberBase.HEX))
    }

    // ---------------------------------------------------------------
    // NEG (two's complement negation)
    // ---------------------------------------------------------------

    @Test
    fun `neg of 5 is -5`() {
        val result = BaseNEngine.neg(5L)
        assertEquals(-5L, result)
        assertEquals("-5", BaseNEngine.toString(result, NumberBase.DEC))
    }

    @Test
    fun `neg of 5 in hex is FFFFFFFB`() {
        val result = BaseNEngine.neg(5L)
        assertEquals("FFFFFFFB", BaseNEngine.toString(result, NumberBase.HEX))
    }

    // ---------------------------------------------------------------
    // Negative values and two's complement display
    // ---------------------------------------------------------------

    @Test
    fun `negative decimal -1 displays as FFFFFFFF in hex`() {
        assertEquals("FFFFFFFF", BaseNEngine.toString(-1L, NumberBase.HEX))
    }

    @Test
    fun `negative decimal -1 displays as all 1s in binary`() {
        assertEquals(
            "11111111111111111111111111111111",
            BaseNEngine.toString(-1L, NumberBase.BIN),
        )
    }

    // ---------------------------------------------------------------
    // Clamp to 32-bit
    // ---------------------------------------------------------------

    @Test
    fun `clamp large positive to 32-bit wraps`() {
        val result = BaseNEngine.clampTo32Bit(2_147_483_648L) // Int.MAX + 1
        assertEquals(-2_147_483_648L, result) // wraps to Int.MIN
    }

    @Test
    fun `clamp Int MAX stays`() {
        assertEquals(2_147_483_647L, BaseNEngine.clampTo32Bit(2_147_483_647L))
    }

    @Test
    fun `clamp Int MIN stays`() {
        assertEquals(-2_147_483_648L, BaseNEngine.clampTo32Bit(-2_147_483_648L))
    }

    // ---------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------

    @Test
    fun `isValidForBase binary accepts 0 and 1`() {
        assertTrue(BaseNEngine.isValidForBase("101010", NumberBase.BIN))
    }

    @Test
    fun `isValidForBase binary rejects 2`() {
        assertFalse(BaseNEngine.isValidForBase("102", NumberBase.BIN))
    }

    @Test
    fun `isValidForBase hex accepts A-F`() {
        assertTrue(BaseNEngine.isValidForBase("1F3A", NumberBase.HEX))
    }

    @Test
    fun `isValidForBase hex rejects G`() {
        assertFalse(BaseNEngine.isValidForBase("1G", NumberBase.HEX))
    }

    @Test
    fun `isValidForBase octal rejects 8`() {
        assertFalse(BaseNEngine.isValidForBase("78", NumberBase.OCT))
    }

    @Test
    fun `isValidForBase decimal accepts negative`() {
        assertTrue(BaseNEngine.isValidForBase("-42", NumberBase.DEC))
    }

    // ---------------------------------------------------------------
    // Division
    // ---------------------------------------------------------------

    @Test
    fun `integer division truncates`() {
        assertEquals(3L, BaseNEngine.divide(10L, 3L))
    }

    @Test(expected = ArithmeticException::class)
    fun `division by zero throws`() {
        BaseNEngine.divide(10L, 0L)
    }

    // ---------------------------------------------------------------
    // Round-trip conversion
    // ---------------------------------------------------------------

    @Test
    fun `round trip DEC to BIN and back`() {
        val original = 255L
        val bin = BaseNEngine.toString(original, NumberBase.BIN)
        assertEquals("11111111", bin)
        val back = BaseNEngine.toLong(bin, NumberBase.BIN)
        assertEquals(original, back)
    }

    @Test
    fun `round trip negative through hex`() {
        val original = -100L
        val hex = BaseNEngine.toString(original, NumberBase.HEX)
        assertEquals("FFFFFF9C", hex)
        val back = BaseNEngine.toLong(hex, NumberBase.HEX)
        assertEquals(original, back)
    }
}
