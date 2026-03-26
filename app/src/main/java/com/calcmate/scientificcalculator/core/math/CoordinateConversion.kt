package com.calcmate.scientificcalculator.core.math

import com.calcmate.scientificcalculator.core.model.AngleUnit
import com.calcmate.scientificcalculator.core.model.MemoryManager
import com.calcmate.scientificcalculator.core.parser.CalcResult
import kotlin.math.*

/**
 * Rectangular <-> Polar coordinate conversion.
 *
 * Results are also stored in [MemoryManager] variables E and F for
 * subsequent recall, matching Casio fx-991ES behaviour.
 */
object CoordinateConversion {

    /**
     * Convert rectangular (x, y) to polar (r, theta).
     * Stores r in variable E, theta in variable F.
     */
    fun pol(x: Double, y: Double, angleUnit: AngleUnit): Pair<Double, Double> {
        val r = sqrt(x * x + y * y)
        val thetaRad = atan2(y, x)
        val theta = fromRadians(thetaRad, angleUnit)
        MemoryManager.storeVariable('E', CalcResult.RealResult(r))
        MemoryManager.storeVariable('F', CalcResult.RealResult(theta))
        return Pair(r, theta)
    }

    /**
     * Convert polar (r, theta) to rectangular (x, y).
     * Stores x in variable E, y in variable F.
     */
    fun rec(r: Double, theta: Double, angleUnit: AngleUnit): Pair<Double, Double> {
        val rad = toRadians(theta, angleUnit)
        val x = r * cos(rad)
        val y = r * sin(rad)
        MemoryManager.storeVariable('E', CalcResult.RealResult(x))
        MemoryManager.storeVariable('F', CalcResult.RealResult(y))
        return Pair(x, y)
    }

    private fun toRadians(value: Double, unit: AngleUnit): Double = when (unit) {
        AngleUnit.RADIAN -> value
        AngleUnit.DEGREE -> Math.toRadians(value)
        AngleUnit.GRADIAN -> value * PI / 200.0
    }

    private fun fromRadians(value: Double, unit: AngleUnit): Double = when (unit) {
        AngleUnit.RADIAN -> value
        AngleUnit.DEGREE -> Math.toDegrees(value)
        AngleUnit.GRADIAN -> value * 200.0 / PI
    }
}
