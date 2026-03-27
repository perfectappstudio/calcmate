package com.perfectappstudio.scientificcalc.core.model

import com.perfectappstudio.scientificcalc.core.parser.NumberBase

data class BaseNState(
    val currentBase: NumberBase = NumberBase.DEC,
    val expression: String = "",
    val result: String = "",
    val error: String? = null,
)
