package com.perfectappstudio.scientificcalc.core.math

import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import kotlin.math.*

/**
 * Arithmetic and utility operations on complex numbers represented as (real, imag) pairs.
 */
object ComplexMath {

    fun add(r1: Double, i1: Double, r2: Double, i2: Double): Pair<Double, Double> =
        Pair(r1 + r2, i1 + i2)

    fun subtract(r1: Double, i1: Double, r2: Double, i2: Double): Pair<Double, Double> =
        Pair(r1 - r2, i1 - i2)

    fun multiply(r1: Double, i1: Double, r2: Double, i2: Double): Pair<Double, Double> =
        Pair(r1 * r2 - i1 * i2, r1 * i2 + i1 * r2)

    fun divide(r1: Double, i1: Double, r2: Double, i2: Double): Pair<Double, Double> {
        val denom = r2 * r2 + i2 * i2
        return Pair((r1 * r2 + i1 * i2) / denom, (i1 * r2 - r1 * i2) / denom)
    }

    fun conjugate(r: Double, i: Double): Pair<Double, Double> = Pair(r, -i)

    fun abs(r: Double, i: Double): Double = sqrt(r * r + i * i)

    fun arg(r: Double, i: Double, angleUnit: AngleUnit = AngleUnit.RADIAN): Double {
        val radians = atan2(i, r)
        return when (angleUnit) {
            AngleUnit.RADIAN -> radians
            AngleUnit.DEGREE -> Math.toDegrees(radians)
            AngleUnit.GRADIAN -> radians * 200.0 / PI
        }
    }

    fun polarToRect(mag: Double, theta: Double, angleUnit: AngleUnit): Pair<Double, Double> {
        val rad = toRadians(theta, angleUnit)
        return Pair(mag * cos(rad), mag * sin(rad))
    }

    fun rectToPolar(real: Double, imag: Double, angleUnit: AngleUnit): Pair<Double, Double> {
        val mag = sqrt(real * real + imag * imag)
        val theta = arg(real, imag, angleUnit)
        return Pair(mag, theta)
    }

    private fun toRadians(value: Double, unit: AngleUnit): Double = when (unit) {
        AngleUnit.RADIAN -> value
        AngleUnit.DEGREE -> Math.toRadians(value)
        AngleUnit.GRADIAN -> value * PI / 200.0
    }
}
