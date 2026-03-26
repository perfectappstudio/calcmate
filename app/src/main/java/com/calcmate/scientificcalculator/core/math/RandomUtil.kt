package com.calcmate.scientificcalculator.core.math

object RandomUtil {
    fun randomNumber(): Double = kotlin.random.Random.nextInt(0, 1000) / 1000.0
}
