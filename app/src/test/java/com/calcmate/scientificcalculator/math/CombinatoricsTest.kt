package com.calcmate.scientificcalculator.math

import com.calcmate.scientificcalculator.core.math.Combinatorics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CombinatoricsTest {

    private val DELTA = 1e-9

    // ---------------------------------------------------------------
    // Factorial
    // ---------------------------------------------------------------

    @Test
    fun `factorial of 0 is 1`() {
        assertEquals(1.0, Combinatorics.factorial(0), DELTA)
    }

    @Test
    fun `factorial of 1 is 1`() {
        assertEquals(1.0, Combinatorics.factorial(1), DELTA)
    }

    @Test
    fun `factorial of 5 is 120`() {
        assertEquals(120.0, Combinatorics.factorial(5), DELTA)
    }

    @Test
    fun `factorial of 10 is 3628800`() {
        assertEquals(3628800.0, Combinatorics.factorial(10), DELTA)
    }

    @Test
    fun `factorial of 170 is finite`() {
        val result = Combinatorics.factorial(170)
        assertTrue(result.isFinite())
        assertTrue(result > 0)
    }

    @Test
    fun `factorial of 171 is Infinity`() {
        val result = Combinatorics.factorial(171)
        assertTrue(result.isInfinite())
    }

    @Test
    fun `factorial of negative returns NaN`() {
        assertTrue(Combinatorics.factorial(-1).isNaN())
    }

    // ---------------------------------------------------------------
    // nPr (permutations)
    // ---------------------------------------------------------------

    @Test
    fun `nPr(5,2) equals 20`() {
        assertEquals(20.0, Combinatorics.nPr(5, 2), DELTA)
    }

    @Test
    fun `nPr(0,0) equals 1`() {
        assertEquals(1.0, Combinatorics.nPr(0, 0), DELTA)
    }

    @Test
    fun `nPr(5,0) equals 1`() {
        assertEquals(1.0, Combinatorics.nPr(5, 0), DELTA)
    }

    @Test
    fun `nPr(5,5) equals 120`() {
        assertEquals(120.0, Combinatorics.nPr(5, 5), DELTA)
    }

    @Test
    fun `nPr(10,3) equals 720`() {
        assertEquals(720.0, Combinatorics.nPr(10, 3), DELTA)
    }

    // ---------------------------------------------------------------
    // nCr (combinations)
    // ---------------------------------------------------------------

    @Test
    fun `nCr(5,2) equals 10`() {
        assertEquals(10.0, Combinatorics.nCr(5, 2), DELTA)
    }

    @Test
    fun `nCr(10,5) equals 252`() {
        assertEquals(252.0, Combinatorics.nCr(10, 5), DELTA)
    }

    @Test
    fun `nCr(0,0) equals 1`() {
        assertEquals(1.0, Combinatorics.nCr(0, 0), DELTA)
    }

    @Test
    fun `nCr(5,0) equals 1`() {
        assertEquals(1.0, Combinatorics.nCr(5, 0), DELTA)
    }

    @Test
    fun `nCr(5,5) equals 1`() {
        assertEquals(1.0, Combinatorics.nCr(5, 5), DELTA)
    }

    // ---------------------------------------------------------------
    // Invalid inputs
    // ---------------------------------------------------------------

    @Test
    fun `nPr with negative n returns NaN`() {
        assertTrue(Combinatorics.nPr(-1, 2).isNaN())
    }

    @Test
    fun `nPr with negative r returns NaN`() {
        assertTrue(Combinatorics.nPr(5, -1).isNaN())
    }

    @Test
    fun `nCr with r greater than n returns NaN`() {
        assertTrue(Combinatorics.nCr(2, 3).isNaN())
    }

    @Test
    fun `nPr with r greater than n returns NaN`() {
        assertTrue(Combinatorics.nPr(2, 3).isNaN())
    }

    @Test
    fun `nCr with negative n returns NaN`() {
        assertTrue(Combinatorics.nCr(-1, 2).isNaN())
    }
}
