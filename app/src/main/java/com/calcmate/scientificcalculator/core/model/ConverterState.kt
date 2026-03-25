package com.calcmate.scientificcalculator.core.model

import com.calcmate.scientificcalculator.core.data.UnitCategory
import com.calcmate.scientificcalculator.core.data.UnitData
import com.calcmate.scientificcalculator.core.data.UnitDef

data class ConverterState(
    val selectedCategory: UnitCategory = UnitCategory.LENGTH,
    val fromUnit: UnitDef = UnitData.unitsFor(UnitCategory.LENGTH).first(),
    val toUnit: UnitDef = UnitData.unitsFor(UnitCategory.LENGTH)[1],
    val inputValue: String = "1",
    val result: String = "",
    val quickConversions: List<Pair<String, String>> = emptyList(),
)
