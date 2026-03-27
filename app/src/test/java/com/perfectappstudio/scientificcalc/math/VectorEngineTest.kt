package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.VectorEngine
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class VectorEngineTest {

    private val delta = 1e-9

    // ---------------------------------------------------------------
    // Add
    // ---------------------------------------------------------------

    @Test
    fun add_2d() {
        // (1,2) + (3,4) = (4,6)
        val result = VectorEngine.add(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
        assertArrayEquals(doubleArrayOf(4.0, 6.0), result, delta)
    }

    @Test
    fun add_3d() {
        val result = VectorEngine.add(doubleArrayOf(1.0, 2.0, 3.0), doubleArrayOf(4.0, 5.0, 6.0))
        assertArrayEquals(doubleArrayOf(5.0, 7.0, 9.0), result, delta)
    }

    // ---------------------------------------------------------------
    // Subtract
    // ---------------------------------------------------------------

    @Test
    fun subtract_2d() {
        val result = VectorEngine.subtract(doubleArrayOf(5.0, 3.0), doubleArrayOf(2.0, 1.0))
        assertArrayEquals(doubleArrayOf(3.0, 2.0), result, delta)
    }

    // ---------------------------------------------------------------
    // Scalar multiply
    // ---------------------------------------------------------------

    @Test
    fun scalarMultiply() {
        val result = VectorEngine.scalarMultiply(3.0, doubleArrayOf(1.0, 2.0, 3.0))
        assertArrayEquals(doubleArrayOf(3.0, 6.0, 9.0), result, delta)
    }

    // ---------------------------------------------------------------
    // Dot product
    // ---------------------------------------------------------------

    @Test
    fun dotProduct_2d() {
        // dot((1,2), (3,4)) = 1*3 + 2*4 = 11
        val result = VectorEngine.dotProduct(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
        assertEquals(11.0, result, delta)
    }

    @Test
    fun dotProduct_3d() {
        val result = VectorEngine.dotProduct(
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(4.0, 5.0, 6.0),
        )
        assertEquals(32.0, result, delta) // 4 + 10 + 18
    }

    // ---------------------------------------------------------------
    // Cross product
    // ---------------------------------------------------------------

    @Test
    fun crossProduct_3d() {
        // cross((1,2,0), (3,4,0)) = (2*0-0*4, 0*3-1*0, 1*4-2*3) = (0, 0, -2)
        val result = VectorEngine.crossProduct(
            doubleArrayOf(1.0, 2.0, 0.0),
            doubleArrayOf(3.0, 4.0, 0.0),
        )
        assertArrayEquals(doubleArrayOf(0.0, 0.0, -2.0), result, delta)
    }

    @Test(expected = IllegalArgumentException::class)
    fun crossProduct_2d_throws() {
        VectorEngine.crossProduct(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
    }

    // ---------------------------------------------------------------
    // Magnitude
    // ---------------------------------------------------------------

    @Test
    fun magnitude_3d() {
        // magnitude((2, -1, 2)) = sqrt(4 + 1 + 4) = 3
        val result = VectorEngine.magnitude(doubleArrayOf(2.0, -1.0, 2.0))
        assertEquals(3.0, result, delta)
    }

    @Test
    fun magnitude_2d() {
        // magnitude((3, 4)) = 5
        val result = VectorEngine.magnitude(doubleArrayOf(3.0, 4.0))
        assertEquals(5.0, result, delta)
    }

    // ---------------------------------------------------------------
    // Dimension validation
    // ---------------------------------------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun add_mismatchedDimension_throws() {
        VectorEngine.add(doubleArrayOf(1.0, 2.0), doubleArrayOf(1.0, 2.0, 3.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun dotProduct_mismatchedDimension_throws() {
        VectorEngine.dotProduct(doubleArrayOf(1.0, 2.0), doubleArrayOf(1.0, 2.0, 3.0))
    }
}
