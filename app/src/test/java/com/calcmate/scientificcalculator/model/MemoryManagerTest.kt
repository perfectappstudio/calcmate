package com.calcmate.scientificcalculator.model

import com.calcmate.scientificcalculator.core.model.MemoryManager
import com.calcmate.scientificcalculator.core.parser.CalcResult
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MemoryManagerTest {

    private val DELTA = 1e-9

    @Before
    fun setUp() {
        MemoryManager.clearAll()
    }

    // ---------------------------------------------------------------
    // Initial state
    // ---------------------------------------------------------------

    @Test
    fun `initial ans is RealResult 0`() {
        assertEquals(0.0, MemoryManager.ans.toDouble(), DELTA)
    }

    @Test
    fun `initial variables are all RealResult 0`() {
        for (name in listOf('A', 'B', 'C', 'D', 'E', 'F', 'M', 'X', 'Y')) {
            assertEquals(0.0, MemoryManager.recallVariable(name).toDouble(), DELTA)
        }
    }

    @Test
    fun `initial independentM is 0`() {
        assertEquals(0.0, MemoryManager.independentM, DELTA)
    }

    // ---------------------------------------------------------------
    // storeVariable / recallVariable
    // ---------------------------------------------------------------

    @Test
    fun `store and recall variable A`() {
        MemoryManager.storeVariable('A', CalcResult.RealResult(42.0))
        assertEquals(42.0, MemoryManager.recallVariable('A').toDouble(), DELTA)
    }

    @Test
    fun `store and recall each variable A through F`() {
        val vars = listOf('A', 'B', 'C', 'D', 'E', 'F')
        vars.forEachIndexed { i, name ->
            MemoryManager.storeVariable(name, CalcResult.RealResult((i + 1).toDouble()))
        }
        vars.forEachIndexed { i, name ->
            assertEquals((i + 1).toDouble(), MemoryManager.recallVariable(name).toDouble(), DELTA)
        }
    }

    @Test
    fun `store and recall variable M`() {
        MemoryManager.storeVariable('M', CalcResult.RealResult(99.0))
        assertEquals(99.0, MemoryManager.recallVariable('M').toDouble(), DELTA)
    }

    @Test
    fun `store and recall variables X and Y`() {
        MemoryManager.storeVariable('X', CalcResult.RealResult(1.5))
        MemoryManager.storeVariable('Y', CalcResult.RealResult(2.5))
        assertEquals(1.5, MemoryManager.recallVariable('X').toDouble(), DELTA)
        assertEquals(2.5, MemoryManager.recallVariable('Y').toDouble(), DELTA)
    }

    @Test
    fun `overwrite variable replaces old value`() {
        MemoryManager.storeVariable('A', CalcResult.RealResult(10.0))
        assertEquals(10.0, MemoryManager.recallVariable('A').toDouble(), DELTA)
        MemoryManager.storeVariable('A', CalcResult.RealResult(20.0))
        assertEquals(20.0, MemoryManager.recallVariable('A').toDouble(), DELTA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `store invalid variable name throws`() {
        MemoryManager.storeVariable('Z', CalcResult.RealResult(1.0))
    }

    // ---------------------------------------------------------------
    // Ans storage and recall
    // ---------------------------------------------------------------

    @Test
    fun `ans stores and returns value`() {
        MemoryManager.ans = CalcResult.RealResult(123.456)
        assertEquals(123.456, MemoryManager.ans.toDouble(), DELTA)
    }

    // ---------------------------------------------------------------
    // M+/M- accumulation
    // ---------------------------------------------------------------

    @Test
    fun `addToM accumulates into independentM and M variable`() {
        MemoryManager.addToM(10.0)
        assertEquals(10.0, MemoryManager.independentM, DELTA)
        assertEquals(10.0, MemoryManager.recallVariable('M').toDouble(), DELTA)

        MemoryManager.addToM(5.0)
        assertEquals(15.0, MemoryManager.independentM, DELTA)
        assertEquals(15.0, MemoryManager.recallVariable('M').toDouble(), DELTA)
    }

    @Test
    fun `subtractFromM decreases independentM and M variable`() {
        MemoryManager.addToM(20.0)
        MemoryManager.subtractFromM(8.0)
        assertEquals(12.0, MemoryManager.independentM, DELTA)
        assertEquals(12.0, MemoryManager.recallVariable('M').toDouble(), DELTA)
    }

    @Test
    fun `addToM and subtractFromM combined accumulation`() {
        MemoryManager.addToM(100.0)
        MemoryManager.subtractFromM(30.0)
        MemoryManager.addToM(5.0)
        assertEquals(75.0, MemoryManager.independentM, DELTA)
        assertEquals(75.0, MemoryManager.recallVariable('M').toDouble(), DELTA)
    }

    // ---------------------------------------------------------------
    // clearAll
    // ---------------------------------------------------------------

    @Test
    fun `clearAll resets ans, all variables, and independentM`() {
        MemoryManager.ans = CalcResult.RealResult(42.0)
        MemoryManager.storeVariable('A', CalcResult.RealResult(10.0))
        MemoryManager.storeVariable('X', CalcResult.RealResult(5.0))
        MemoryManager.addToM(100.0)

        MemoryManager.clearAll()

        assertEquals(0.0, MemoryManager.ans.toDouble(), DELTA)
        assertEquals(0.0, MemoryManager.recallVariable('A').toDouble(), DELTA)
        assertEquals(0.0, MemoryManager.recallVariable('X').toDouble(), DELTA)
        assertEquals(0.0, MemoryManager.recallVariable('M').toDouble(), DELTA)
        assertEquals(0.0, MemoryManager.independentM, DELTA)
    }
}
