package com.perfectappstudio.scientificcalc.feature.converter

import androidx.lifecycle.ViewModel
import com.perfectappstudio.scientificcalc.core.data.UnitCategory
import com.perfectappstudio.scientificcalc.core.data.UnitData
import com.perfectappstudio.scientificcalc.core.data.UnitDef
import com.perfectappstudio.scientificcalc.core.model.ConverterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class ConverterViewModel : ViewModel() {

    private val _state = MutableStateFlow(ConverterState())
    val state: StateFlow<ConverterState> = _state.asStateFlow()

    init {
        recalculate()
    }

    fun onCategoryChange(category: UnitCategory) {
        val units = UnitData.unitsFor(category)
        _state.update {
            it.copy(
                selectedCategory = category,
                fromUnit = units.first(),
                toUnit = units[1],
                inputValue = "1",
            )
        }
        recalculate()
    }

    fun onFromUnitChange(unit: UnitDef) {
        _state.update { it.copy(fromUnit = unit) }
        recalculate()
    }

    fun onToUnitChange(unit: UnitDef) {
        _state.update { it.copy(toUnit = unit) }
        recalculate()
    }

    fun onInputChange(input: String) {
        val sanitized = input.filter { ch -> ch.isDigit() || ch == '.' || ch == '-' }
        _state.update { it.copy(inputValue = sanitized) }
        recalculate()
    }

    fun onSwapUnits() {
        _state.update {
            it.copy(
                fromUnit = it.toUnit,
                toUnit = it.fromUnit,
            )
        }
        recalculate()
    }

    private fun recalculate() {
        val current = _state.value
        val inputDouble = current.inputValue.toDoubleOrNull()

        if (inputDouble == null) {
            _state.update { it.copy(result = "", quickConversions = emptyList()) }
            return
        }

        val result = UnitData.convert(inputDouble, current.fromUnit, current.toUnit)
        val formatted = formatResult(result)
        val quick = calculateQuickConversions(inputDouble, current)

        _state.update { it.copy(result = formatted, quickConversions = quick) }
    }

    private fun calculateQuickConversions(
        value: Double,
        current: ConverterState,
    ): List<Pair<String, String>> {
        val allUnits = UnitData.unitsFor(current.selectedCategory)
        val excluded = setOf(current.fromUnit.name, current.toUnit.name)

        return allUnits
            .filter { it.name !in excluded }
            .take(4)
            .map { unit ->
                val converted = UnitData.convert(value, current.fromUnit, unit)
                "${unit.name} (${unit.symbol})" to formatResult(converted)
            }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "---"

        val bd = BigDecimal(value, MathContext.DECIMAL128)
        val abs = bd.abs()

        return when {
            abs.compareTo(BigDecimal.ZERO) == 0 -> "0"
            abs >= BigDecimal("1E12") || (abs > BigDecimal.ZERO && abs < BigDecimal("1E-8")) -> {
                bd.setScale(6, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toString()
            }
            else -> {
                val precision = when {
                    abs >= BigDecimal("1000") -> 4
                    abs >= BigDecimal("1") -> 6
                    else -> 8
                }
                bd.setScale(precision, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()
            }
        }
    }
}
