package com.perfectappstudio.scientificcalc.core.math

object RandomUtil {
    fun randomNumber(): Double = kotlin.random.Random.nextInt(0, 1000) / 1000.0
}
