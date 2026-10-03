package com.perfectappstudio.scientificcalc.feature

import com.perfectappstudio.scientificcalc.feature.calculus.CalculusViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CalculusViewModelTest {
    @Before fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun latestRequestWinsAndEditingInvalidatesResult() = runTest {
        val model = CalculusViewModel(StandardTestDispatcher(testScheduler))
        model.calculate()
        model.edit { it.copy(point = "3") }
        model.calculate()
        advanceUntilIdle()
        assertEquals(6.0, model.state.value.result!!.value, 1e-6)
        assertFalse(model.state.value.busy)
        model.edit { it.copy(function = "sin(x)") }
        assertNull(model.state.value.result)
        assertNull(model.state.value.error)
    }

    @Test fun cancellingPendingCalculationDoesNotPublishResult() = runTest {
        val model = CalculusViewModel(StandardTestDispatcher(testScheduler))
        model.calculate()
        assertTrue(model.state.value.busy)
        model.cancel()
        advanceUntilIdle()
        assertFalse(model.state.value.busy)
        assertNull(model.state.value.result)
        assertNull(model.state.value.error)
    }

    @Test fun invalidInputsProduceErrorAndCanBeCorrected() = runTest {
        val model = CalculusViewModel(StandardTestDispatcher(testScheduler))
        model.edit { it.copy(point = "1/0") }
        model.calculate()
        advanceUntilIdle()
        assertNotNull(model.state.value.error)
        assertNull(model.state.value.result)
        assertFalse(model.state.value.busy)
        model.edit { it.copy(point = "2") }
        model.calculate()
        advanceUntilIdle()
        assertEquals(4.0, model.state.value.result!!.value, 1e-6)
        assertNull(model.state.value.error)
    }
}
