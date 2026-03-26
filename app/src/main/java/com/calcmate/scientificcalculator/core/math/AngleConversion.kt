package com.calcmate.scientificcalculator.core.math

import com.calcmate.scientificcalculator.core.model.AngleUnit
import kotlin.math.PI

/**
 * Converts a numeric value between angle units (DEG, RAD, GRA).
 */
object AngleConversion {

    fun convert(value: Double, from: AngleUnit, to: AngleUnit): Double {
        if (from == to) return value
        return when (from) {
            AngleUnit.DEGREE -> when (to) {
                AngleUnit.RADIAN -> value * PI / 180.0
                AngleUnit.GRADIAN -> value * 100.0 / 90.0
                AngleUnit.DEGREE -> value
            }
            AngleUnit.RADIAN -> when (to) {
                AngleUnit.DEGREE -> value * 180.0 / PI
                AngleUnit.GRADIAN -> value * 200.0 / PI
                AngleUnit.RADIAN -> value
            }
            AngleUnit.GRADIAN -> when (to) {
                AngleUnit.DEGREE -> value * 90.0 / 100.0
                AngleUnit.RADIAN -> value * PI / 200.0
                AngleUnit.GRADIAN -> value
            }
        }
    }
}
