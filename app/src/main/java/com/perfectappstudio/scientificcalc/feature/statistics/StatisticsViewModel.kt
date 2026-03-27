package com.perfectappstudio.scientificcalc.feature.statistics

import androidx.lifecycle.ViewModel
import com.perfectappstudio.scientificcalc.core.math.DataPair
import com.perfectappstudio.scientificcalc.core.math.DataPoint
import com.perfectappstudio.scientificcalc.core.math.StatisticsEngine
import com.perfectappstudio.scientificcalc.core.model.RegressionType
import com.perfectappstudio.scientificcalc.core.model.StatType
import com.perfectappstudio.scientificcalc.core.model.StatisticsMode
import com.perfectappstudio.scientificcalc.core.model.StatisticsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatisticsViewModel : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    // --- Mode switching ---

    fun setMode(mode: StatisticsMode) {
        _state.update { it.copy(mode = mode, lastResult = "") }
    }

    // --- SD data management ---

    fun addSDData(value: Double, frequency: Int = 1) {
        _state.update {
            it.copy(
                sdData = it.sdData + DataPoint(value, frequency),
                lastResult = "",
            )
        }
    }

    fun removeSDData(index: Int) {
        _state.update {
            it.copy(
                sdData = it.sdData.toMutableList().apply { removeAt(index) },
                lastResult = "",
            )
        }
    }

    fun clearSDData() {
        _state.update { it.copy(sdData = emptyList(), lastResult = "") }
    }

    // --- REG data management ---

    fun addREGData(x: Double, y: Double, frequency: Int = 1) {
        _state.update {
            it.copy(
                regData = it.regData + DataPair(x, y, frequency),
                lastResult = "",
            )
        }
    }

    fun removeREGData(index: Int) {
        _state.update {
            it.copy(
                regData = it.regData.toMutableList().apply { removeAt(index) },
                lastResult = "",
            )
        }
    }

    fun clearREGData() {
        _state.update { it.copy(regData = emptyList(), lastResult = "") }
    }

    // --- Regression type ---

    fun setRegressionType(type: RegressionType) {
        _state.update { it.copy(regressionType = type, lastResult = "") }
    }

    // --- Compute statistic ---

    fun computeStatistic(type: StatType) {
        val current = _state.value
        val result = try {
            when (current.mode) {
                StatisticsMode.SD -> computeSDStat(type, current.sdData)
                StatisticsMode.REG -> computeREGStat(type, current)
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
        _state.update { it.copy(lastResult = result) }
    }

    /**
     * Compute a normal distribution value given a t-value.
     */
    fun computeNormal(type: StatType, tValue: Double) {
        val result = try {
            when (type) {
                StatType.NORMAL_P -> formatResult(StatisticsEngine.normalP(tValue))
                StatType.NORMAL_Q -> formatResult(StatisticsEngine.normalQ(tValue))
                StatType.NORMAL_R -> formatResult(StatisticsEngine.normalR(tValue))
                else -> "Invalid normal distribution type"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
        _state.update { it.copy(lastResult = result) }
    }

    // --- Private helpers ---

    private fun computeSDStat(type: StatType, data: List<DataPoint>): String {
        return when (type) {
            StatType.N -> StatisticsEngine.count(data).toString()
            StatType.MEAN_X -> formatResult(StatisticsEngine.meanX(data))
            StatType.POPULATION_STD_DEV_X -> formatResult(StatisticsEngine.populationStdDevX(data))
            StatType.SAMPLE_STD_DEV_X -> formatResult(StatisticsEngine.sampleStdDevX(data))
            StatType.SUM_X -> formatResult(StatisticsEngine.sumX(data))
            StatType.SUM_X2 -> formatResult(StatisticsEngine.sumX2(data))
            else -> "Not available in SD mode"
        }
    }

    private fun computeREGStat(type: StatType, state: StatisticsState): String {
        val data = state.regData
        return when (type) {
            StatType.N -> StatisticsEngine.countPairs(data).toString()
            StatType.MEAN_X -> formatResult(StatisticsEngine.meanXPair(data))
            StatType.POPULATION_STD_DEV_X -> formatResult(StatisticsEngine.populationStdDevXPair(data))
            StatType.SAMPLE_STD_DEV_X -> formatResult(StatisticsEngine.sampleStdDevXPair(data))
            StatType.SUM_X -> formatResult(StatisticsEngine.sumXPair(data))
            StatType.SUM_X2 -> formatResult(StatisticsEngine.sumX2Pair(data))
            StatType.MEAN_Y -> formatResult(StatisticsEngine.meanY(data))
            StatType.POPULATION_STD_DEV_Y -> formatResult(StatisticsEngine.populationStdDevY(data))
            StatType.SAMPLE_STD_DEV_Y -> formatResult(StatisticsEngine.sampleStdDevY(data))
            StatType.SUM_Y -> formatResult(StatisticsEngine.sumY(data))
            StatType.SUM_Y2 -> formatResult(StatisticsEngine.sumY2(data))
            StatType.SUM_XY -> formatResult(StatisticsEngine.sumXY(data))
            StatType.REG_A, StatType.REG_B, StatType.REG_C, StatType.REG_R ->
                computeRegressionCoefficient(type, data, state.regressionType)
            else -> "Not available in REG mode"
        }
    }

    private fun computeRegressionCoefficient(
        type: StatType,
        data: List<DataPair>,
        regressionType: RegressionType,
    ): String {
        return when (regressionType) {
            RegressionType.QUADRATIC -> {
                val result = StatisticsEngine.quadraticRegression(data)
                when (type) {
                    StatType.REG_A -> formatResult(result.a)
                    StatType.REG_B -> formatResult(result.b)
                    StatType.REG_C -> formatResult(result.c)
                    else -> "N/A"
                }
            }
            else -> {
                val result = when (regressionType) {
                    RegressionType.LINEAR -> StatisticsEngine.linearRegression(data)
                    RegressionType.LOG -> StatisticsEngine.logRegression(data)
                    RegressionType.EXP -> StatisticsEngine.expRegression(data)
                    RegressionType.POWER -> StatisticsEngine.powerRegression(data)
                    RegressionType.INVERSE -> StatisticsEngine.inverseRegression(data)
                    RegressionType.QUADRATIC -> error("Unreachable")
                }
                when (type) {
                    StatType.REG_A -> formatResult(result.a)
                    StatType.REG_B -> formatResult(result.b)
                    StatType.REG_R -> formatResult(result.r)
                    StatType.REG_C -> "N/A (non-quadratic)"
                    else -> "N/A"
                }
            }
        }
    }

    private fun formatResult(value: Double): String {
        // Use up to 10 significant figures, strip trailing zeros
        if (value == 0.0 || value == -0.0) return "0"
        val s = "%.10g".format(value)
        // Remove trailing zeros after decimal point
        return if ('.' in s) {
            s.trimEnd('0').trimEnd('.')
        } else {
            s
        }
    }
}
