package com.perfectappstudio.scientificcalc.core.model

import com.perfectappstudio.scientificcalc.core.math.DataPair
import com.perfectappstudio.scientificcalc.core.math.DataPoint

data class StatisticsState(
    val mode: StatisticsMode = StatisticsMode.SD,
    val sdData: List<DataPoint> = emptyList(),
    val regData: List<DataPair> = emptyList(),
    val regressionType: RegressionType = RegressionType.LINEAR,
    val lastResult: String = "",
)

enum class StatisticsMode { SD, REG }

enum class RegressionType(val label: String, val formula: String) {
    LINEAR("Lin", "y = A + Bx"),
    LOG("Log", "y = A + B·ln(x)"),
    EXP("Exp", "y = A·e^(Bx)"),
    POWER("Pwr", "y = A·x^B"),
    INVERSE("Inv", "y = A + B/x"),
    QUADRATIC("Quad", "y = A + Bx + Cx²"),
}

/** Types of statistic that can be computed and displayed. */
enum class StatType(val label: String) {
    // Single-variable
    N("n"),
    MEAN_X("x̄"),
    POPULATION_STD_DEV_X("σx"),
    SAMPLE_STD_DEV_X("sx"),
    SUM_X("Σx"),
    SUM_X2("Σx²"),

    // Paired-variable
    MEAN_Y("ȳ"),
    POPULATION_STD_DEV_Y("σy"),
    SAMPLE_STD_DEV_Y("sy"),
    SUM_Y("Σy"),
    SUM_Y2("Σy²"),
    SUM_XY("Σxy"),

    // Regression coefficients
    REG_A("A"),
    REG_B("B"),
    REG_C("C"),
    REG_R("r"),

    // Normal distribution
    NORMAL_P("P(t)"),
    NORMAL_Q("Q(t)"),
    NORMAL_R("R(t)"),
}
