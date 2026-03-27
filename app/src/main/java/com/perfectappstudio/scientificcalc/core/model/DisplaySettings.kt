package com.perfectappstudio.scientificcalc.core.model

data class DisplaySettings(
    val mode: DisplayMode = DisplayMode.NORM_1,
    val digits: Int = 10,
    val engineeringOn: Boolean = false,
    val fractionFormat: FractionFormat = FractionFormat.MIXED,
)

enum class DisplayMode { FIX, SCI, NORM_1, NORM_2 }

enum class FractionFormat { MIXED, IMPROPER }
