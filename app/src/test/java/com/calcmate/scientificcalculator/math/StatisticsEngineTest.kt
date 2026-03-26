package com.calcmate.scientificcalculator.math

import com.calcmate.scientificcalculator.core.math.DataPair
import com.calcmate.scientificcalculator.core.math.DataPoint
import com.calcmate.scientificcalculator.core.math.StatisticsEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsEngineTest {

    private val delta = 1e-4

    // ---------------------------------------------------------------
    // Single-variable (SD mode)
    // Data: {55, 54, 51, 55, 53, 53, 54, 52}
    // ---------------------------------------------------------------

    private val sdData = listOf(
        DataPoint(55.0), DataPoint(54.0), DataPoint(51.0), DataPoint(55.0),
        DataPoint(53.0), DataPoint(53.0), DataPoint(54.0), DataPoint(52.0),
    )

    @Test
    fun sd_count() {
        assertEquals(8, StatisticsEngine.count(sdData))
    }

    @Test
    fun sd_sumX() {
        assertEquals(427.0, StatisticsEngine.sumX(sdData), delta)
    }

    @Test
    fun sd_sumX2() {
        assertEquals(22805.0, StatisticsEngine.sumX2(sdData), delta)
    }

    @Test
    fun sd_meanX() {
        assertEquals(53.375, StatisticsEngine.meanX(sdData), delta)
    }

    @Test
    fun sd_sampleStdDevX() {
        assertEquals(1.407885953, StatisticsEngine.sampleStdDevX(sdData), delta)
    }

    @Test
    fun sd_populationStdDevX() {
        assertEquals(1.316956719, StatisticsEngine.populationStdDevX(sdData), delta)
    }

    // ---------------------------------------------------------------
    // SD with frequency
    // ---------------------------------------------------------------

    @Test
    fun sd_withFrequency() {
        // Same data as above but expressed with frequencies
        val data = listOf(
            DataPoint(51.0, 1),
            DataPoint(52.0, 1),
            DataPoint(53.0, 2),
            DataPoint(54.0, 2),
            DataPoint(55.0, 2),
        )
        assertEquals(8, StatisticsEngine.count(data))
        assertEquals(427.0, StatisticsEngine.sumX(data), delta)
        assertEquals(53.375, StatisticsEngine.meanX(data), delta)
        assertEquals(1.407885953, StatisticsEngine.sampleStdDevX(data), delta)
        assertEquals(1.316956719, StatisticsEngine.populationStdDevX(data), delta)
    }

    // ---------------------------------------------------------------
    // Linear regression
    // Temperature/pressure data (Casio manual example):
    //   x (temp): 10, 15, 20, 25, 30
    //   y (press): 1003, 1005, 1010, 1011, 1014
    // Expected: A=997.4, B=0.56, r=0.982607368
    // ---------------------------------------------------------------

    private val linearRegData = listOf(
        DataPair(10.0, 1003.0),
        DataPair(15.0, 1005.0),
        DataPair(20.0, 1010.0),
        DataPair(25.0, 1011.0),
        DataPair(30.0, 1014.0),
    )

    @Test
    fun linearRegression_A() {
        val result = StatisticsEngine.linearRegression(linearRegData)
        assertEquals(997.4, result.a, delta)
    }

    @Test
    fun linearRegression_B() {
        val result = StatisticsEngine.linearRegression(linearRegData)
        assertEquals(0.56, result.b, delta)
    }

    @Test
    fun linearRegression_r() {
        val result = StatisticsEngine.linearRegression(linearRegData)
        assertEquals(0.982607368, result.r, delta)
    }

    // ---------------------------------------------------------------
    // Paired-variable stats on regression data
    // ---------------------------------------------------------------

    @Test
    fun reg_countPairs() {
        assertEquals(5, StatisticsEngine.countPairs(linearRegData))
    }

    @Test
    fun reg_meanX() {
        assertEquals(20.0, StatisticsEngine.meanXPair(linearRegData), delta)
    }

    @Test
    fun reg_meanY() {
        assertEquals(1008.6, StatisticsEngine.meanY(linearRegData), delta)
    }

    @Test
    fun reg_sumXY() {
        // 10*1003 + 15*1005 + 20*1010 + 25*1011 + 30*1014 = 101000
        assertEquals(101000.0, StatisticsEngine.sumXY(linearRegData), delta)
    }

    // ---------------------------------------------------------------
    // Quadratic regression
    // Casio manual example data:
    //   x: 29, 50, 74, 103, 118
    //   y: 1.6, 23.5, 38.0, 46.4, 48.0
    // Expected: A=-35.59856934, B=1.495939413, C=-6.71629667e-3
    // ---------------------------------------------------------------

    private val quadRegData = listOf(
        DataPair(29.0, 1.6),
        DataPair(50.0, 23.5),
        DataPair(74.0, 38.0),
        DataPair(103.0, 46.4),
        DataPair(118.0, 48.0),
    )

    @Test
    fun quadraticRegression_A() {
        val result = StatisticsEngine.quadraticRegression(quadRegData)
        assertEquals(-35.59856934, result.a, 0.01)
    }

    @Test
    fun quadraticRegression_B() {
        val result = StatisticsEngine.quadraticRegression(quadRegData)
        assertEquals(1.495939413, result.b, 1e-4)
    }

    @Test
    fun quadraticRegression_C() {
        val result = StatisticsEngine.quadraticRegression(quadRegData)
        assertEquals(-6.71629667e-3, result.c, 1e-5)
    }

    // ---------------------------------------------------------------
    // Normal distribution
    // Standardize 53 with the SD data: t = (53 - 53.375) / 1.316956719 = -0.284747398
    // P(t) = 0.38974
    // ---------------------------------------------------------------

    @Test
    fun normal_standardize() {
        val mean = StatisticsEngine.meanX(sdData)
        val sigma = StatisticsEngine.populationStdDevX(sdData)
        val t = StatisticsEngine.standardize(53.0, mean, sigma)
        assertEquals(-0.284747398, t, delta)
    }

    @Test
    fun normal_P() {
        // P(-0.284747398) from standard normal CDF
        val t = -0.284747398
        val p = StatisticsEngine.normalP(t)
        assertEquals(0.38792, p, 1e-3)
    }

    @Test
    fun normal_Q() {
        // Q(t) = P(t) - 0.5
        val t = -0.284747398
        val q = StatisticsEngine.normalQ(t)
        assertEquals(0.38792 - 0.5, q, 1e-3)
    }

    @Test
    fun normal_R() {
        // R(t) = 1 - P(t)
        val t = -0.284747398
        val r = StatisticsEngine.normalR(t)
        assertEquals(1.0 - 0.38792, r, 1e-3)
    }

    @Test
    fun normal_P_zero() {
        // P(0) = 0.5
        assertEquals(0.5, StatisticsEngine.normalP(0.0), 1e-6)
    }

    @Test
    fun normal_P_largePositive() {
        // P(4) should be very close to 1
        val p = StatisticsEngine.normalP(4.0)
        assertEquals(1.0, p, 1e-4)
    }

    @Test
    fun normal_P_largeNegative() {
        // P(-4) should be very close to 0
        val p = StatisticsEngine.normalP(-4.0)
        assertEquals(0.0, p, 1e-4)
    }

    // ---------------------------------------------------------------
    // Edge cases
    // ---------------------------------------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun sd_meanX_emptyData() {
        StatisticsEngine.meanX(emptyList<DataPoint>())
    }

    @Test(expected = IllegalArgumentException::class)
    fun sd_sampleStdDev_singlePoint() {
        StatisticsEngine.sampleStdDevX(listOf(DataPoint(5.0)))
    }
}
