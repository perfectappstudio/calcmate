package com.calcmate.scientificcalculator.core.model

import com.calcmate.scientificcalculator.core.parser.NumberBase

data class BaseNState(
    val currentBase: NumberBase = NumberBase.DEC,
    val expression: String = "",
    val result: String = "",
    val error: String? = null,
)
