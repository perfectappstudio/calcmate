package com.calcmate.scientificcalculator.math

import com.calcmate.scientificcalculator.core.math.MatrixEngine
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class MatrixEngineTest {

    private val delta = 1e-9

    // ---------------------------------------------------------------
    // Multiply
    // ---------------------------------------------------------------

    @Test
    fun multiply_2x2() {
        // [[2,1],[1,1]] * [[2,-1],[-1,2]] = [[3,0],[-1,1]]
        val a = arrayOf(doubleArrayOf(2.0, 1.0), doubleArrayOf(1.0, 1.0))
        val b = arrayOf(doubleArrayOf(2.0, -1.0), doubleArrayOf(-1.0, 2.0))
        val result = MatrixEngine.multiply(a, b)
        assertArrayEquals(doubleArrayOf(3.0, 0.0), result[0], delta)
        assertArrayEquals(doubleArrayOf(1.0, 1.0), result[1], delta)
    }

    // ---------------------------------------------------------------
    // Add
    // ---------------------------------------------------------------

    @Test
    fun add_2x2() {
        // [[2,1],[1,1]] + [[2,-1],[-1,2]] = [[4,0],[0,3]]
        val a = arrayOf(doubleArrayOf(2.0, 1.0), doubleArrayOf(1.0, 1.0))
        val b = arrayOf(doubleArrayOf(2.0, -1.0), doubleArrayOf(-1.0, 2.0))
        val result = MatrixEngine.add(a, b)
        assertArrayEquals(doubleArrayOf(4.0, 0.0), result[0], delta)
        assertArrayEquals(doubleArrayOf(0.0, 3.0), result[1], delta)
    }

    // ---------------------------------------------------------------
    // Subtract
    // ---------------------------------------------------------------

    @Test
    fun subtract_2x2() {
        val a = arrayOf(doubleArrayOf(5.0, 3.0), doubleArrayOf(2.0, 7.0))
        val b = arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
        val result = MatrixEngine.subtract(a, b)
        assertArrayEquals(doubleArrayOf(4.0, 1.0), result[0], delta)
        assertArrayEquals(doubleArrayOf(-1.0, 3.0), result[1], delta)
    }

    // ---------------------------------------------------------------
    // Determinant
    // ---------------------------------------------------------------

    @Test
    fun determinant_2x2() {
        // Det([[2,1],[1,1]]) = 2*1 - 1*1 = 1
        val m = arrayOf(doubleArrayOf(2.0, 1.0), doubleArrayOf(1.0, 1.0))
        assertEquals(1.0, MatrixEngine.determinant(m), delta)
    }

    @Test
    fun determinant_3x3() {
        val m = arrayOf(
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(4.0, 5.0, 6.0),
            doubleArrayOf(7.0, 8.0, 0.0),
        )
        // det = 1*(5*0-6*8) - 2*(4*0-6*7) + 3*(4*8-5*7) = -48 + 84 - 9 = 27
        assertEquals(27.0, MatrixEngine.determinant(m), delta)
    }

    @Test
    fun determinant_1x1() {
        val m = arrayOf(doubleArrayOf(42.0))
        assertEquals(42.0, MatrixEngine.determinant(m), delta)
    }

    // ---------------------------------------------------------------
    // Inverse
    // ---------------------------------------------------------------

    @Test
    fun inverse_2x2() {
        // Inverse([[2,1],[1,1]]) = [[1,-1],[-1,2]]
        val m = arrayOf(doubleArrayOf(2.0, 1.0), doubleArrayOf(1.0, 1.0))
        val inv = MatrixEngine.inverse(m)
        assertArrayEquals(doubleArrayOf(1.0, -1.0), inv[0], delta)
        assertArrayEquals(doubleArrayOf(-1.0, 2.0), inv[1], delta)
    }

    @Test(expected = IllegalArgumentException::class)
    fun inverse_singular_throws() {
        val m = arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(2.0, 4.0))
        MatrixEngine.inverse(m)
    }

    // ---------------------------------------------------------------
    // Transpose
    // ---------------------------------------------------------------

    @Test
    fun transpose_2x3() {
        val m = arrayOf(
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(4.0, 5.0, 6.0),
        )
        val t = MatrixEngine.transpose(m)
        assertEquals(3, t.size)
        assertEquals(2, t[0].size)
        assertArrayEquals(doubleArrayOf(1.0, 4.0), t[0], delta)
        assertArrayEquals(doubleArrayOf(2.0, 5.0), t[1], delta)
        assertArrayEquals(doubleArrayOf(3.0, 6.0), t[2], delta)
    }

    // ---------------------------------------------------------------
    // Scalar multiply
    // ---------------------------------------------------------------

    @Test
    fun scalarMultiply() {
        val m = arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
        val result = MatrixEngine.scalarMultiply(3.0, m)
        assertArrayEquals(doubleArrayOf(3.0, 6.0), result[0], delta)
        assertArrayEquals(doubleArrayOf(9.0, 12.0), result[1], delta)
    }

    // ---------------------------------------------------------------
    // Square and Cube
    // ---------------------------------------------------------------

    @Test
    fun square() {
        val m = arrayOf(doubleArrayOf(1.0, 1.0), doubleArrayOf(0.0, 1.0))
        val sq = MatrixEngine.square(m)
        // [[1,1],[0,1]]^2 = [[1,2],[0,1]]
        assertArrayEquals(doubleArrayOf(1.0, 2.0), sq[0], delta)
        assertArrayEquals(doubleArrayOf(0.0, 1.0), sq[1], delta)
    }

    @Test
    fun cube() {
        val m = arrayOf(doubleArrayOf(1.0, 1.0), doubleArrayOf(0.0, 1.0))
        val cu = MatrixEngine.cube(m)
        // [[1,1],[0,1]]^3 = [[1,3],[0,1]]
        assertArrayEquals(doubleArrayOf(1.0, 3.0), cu[0], delta)
        assertArrayEquals(doubleArrayOf(0.0, 1.0), cu[1], delta)
    }

    // ---------------------------------------------------------------
    // Abs elements
    // ---------------------------------------------------------------

    @Test
    fun absElements() {
        val m = arrayOf(doubleArrayOf(-1.0, 2.0), doubleArrayOf(3.0, -4.0))
        val result = MatrixEngine.absElements(m)
        assertArrayEquals(doubleArrayOf(1.0, 2.0), result[0], delta)
        assertArrayEquals(doubleArrayOf(3.0, 4.0), result[1], delta)
    }

    // ---------------------------------------------------------------
    // Dimension validation
    // ---------------------------------------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun add_mismatchedDimensions_throws() {
        val a = arrayOf(doubleArrayOf(1.0, 2.0))
        val b = arrayOf(doubleArrayOf(1.0), doubleArrayOf(2.0))
        MatrixEngine.add(a, b)
    }

    @Test(expected = IllegalArgumentException::class)
    fun multiply_incompatibleDimensions_throws() {
        val a = arrayOf(doubleArrayOf(1.0, 2.0))  // 1x2
        val b = arrayOf(doubleArrayOf(1.0, 2.0))  // 1x2  (need 2xN)
        MatrixEngine.multiply(a, b)
    }

    @Test(expected = IllegalArgumentException::class)
    fun determinant_nonSquare_throws() {
        val m = arrayOf(doubleArrayOf(1.0, 2.0, 3.0), doubleArrayOf(4.0, 5.0, 6.0))
        MatrixEngine.determinant(m)
    }
}
