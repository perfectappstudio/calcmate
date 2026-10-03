package com.perfectappstudio.scientificcalc.math

import com.perfectappstudio.scientificcalc.core.math.NumericalCalculus
import com.perfectappstudio.scientificcalc.core.model.MemoryManager
import com.perfectappstudio.scientificcalc.core.parser.CalcResult
import com.perfectappstudio.scientificcalc.core.parser.ParseException
import java.util.concurrent.CancellationException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculusQualityTest {
    @Before fun resetMemory() { MemoryManager.clearAll() }

    @Test fun adaptiveIntegralHandlesPolynomialAndReversedLimits() {
        assertEquals(1.0 / 3, NumericalCalculus.estimateIntegral("x^2", 0.0, 1.0).value, 1e-10)
        assertEquals(-1.0 / 3, NumericalCalculus.estimateIntegral("x^2", 1.0, 0.0).value, 1e-10)
        assertEquals(0.0, NumericalCalculus.estimateIntegral("x^2", 2.0, 2.0).value, 0.0)
    }

    @Test fun adaptiveIntegralDoesNotAliasHighFrequencyToAConstant() {
        val result = NumericalCalculus.estimateIntegral("cos(1024*pi*x)", 0.0, 1.0)
        assertEquals(0.0, result.value, 1e-7)
        assertTrue(result.estimatedError <= 1e-8)
    }

    @Test fun adaptiveIntegralDetectsEndpointDecayAndNarrowPeak() {
        assertEquals(0.001, NumericalCalculus.estimateIntegral("e^(-1000*x)", 0.0, 1.0).value, 1e-9)
        assertEquals(Math.sqrt(Math.PI) / 100, NumericalCalculus.estimateIntegral("e^(-10000*(x-0.123)^2)", 0.0, 1.0).value, 1e-9)
    }

    @Test fun singularAndInvalidFunctionsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateIntegral("1/x", -1.0, 1.0, maxEvaluations = 3000) }
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateIntegral("sqrt(-x)", 0.0, 1.0) }
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateIntegral("Ran#", 0.0, 1.0) }
    }

    @Test fun derivativeHandlesSmoothFunctionsAndLargePoints() {
        assertEquals(7.0, NumericalCalculus.estimateDerivative("3x^2-5x+2", 2.0).value, 1e-6)
        assertEquals(1.0, NumericalCalculus.estimateDerivative("x", 1e16).value, 1e-8)
        assertEquals(0.0, NumericalCalculus.estimateDerivative("x^2", 0.0).value, 1e-8)
        assertEquals(1.0, NumericalCalculus.estimateDerivative("1+x", 1e-100).value, 1e-6)
        assertEquals(1.0, NumericalCalculus.estimateDerivative("ln(x)", 1e-100).value / 1e100, 1e-5)
        assertEquals(1.0, NumericalCalculus.estimateDerivative("ln(x)", 1e-8).value / 1e8, 1e-5)
    }

    @Test fun derivativeRejectsCuspsUnresolvedStepsAndRandomValues() {
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateDerivative("abs(x)", 0.0) }
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateDerivative("x", 1e16, dx = 1e-10) }
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateDerivative("Ran#", 0.0) }
    }

    @Test fun calculationsPreserveVariablesAndAnswerEvenAfterFailure() {
        MemoryManager.storeVariable('X', CalcResult.RealResult(42.0))
        MemoryManager.storeVariable('A', CalcResult.RealResult(2.0))
        MemoryManager.ans = CalcResult.RealResult(3.0)
        assertEquals(4.0, NumericalCalculus.estimateIntegral("A*x+Ans", 0.0, 1.0).value, 1e-8)
        assertEquals(2.0, NumericalCalculus.differentiate("A*x+Ans", 4.0), 1e-8)
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateDerivative("sqrt(x)", 0.0) }
        assertEquals(42.0, MemoryManager.recallVariable('X').toDouble(), 0.0)
        assertEquals(2.0, MemoryManager.recallVariable('A').toDouble(), 0.0)
        assertEquals(3.0, MemoryManager.ans.toDouble(), 0.0)
    }

    @Test fun invalidNumericalParametersFailBeforeRunning() {
        for (n in listOf(0, -1, Int.MAX_VALUE)) {
            assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.integrate("x", 0.0, 1.0, n) }
        }
        for (step in listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.differentiate("x", 1.0, step) }
        }
        assertThrows(IllegalArgumentException::class.java) { NumericalCalculus.estimateIntegral("x", Double.NaN, 1.0) }
    }

    @Test fun activeCalculationCanBeCancelled() {
        var count = 0
        assertThrows(CancellationException::class.java) {
            NumericalCalculus.estimateIntegral("sin(x)", 0.0, 1.0, checkCancelled = {
                if (++count > 15) throw CancellationException()
            })
        }
        assertEquals(16, count)
    }

    @Test fun deeplyNestedInputIsRejectedWithoutOverflowingTheStack() {
        val expression = "(".repeat(300) + "x" + ")".repeat(300)
        assertThrows(ParseException::class.java) { NumericalCalculus.estimateDerivative(expression, 1.0) }
    }
}
