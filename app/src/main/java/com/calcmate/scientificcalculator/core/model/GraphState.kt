package com.calcmate.scientificcalculator.core.model

data class GraphState(
    val functions: List<GraphFunction> = emptyList(),
    val viewport: Viewport = Viewport(),
    val tracePoint: TracePoint? = null,
    val isTracing: Boolean = false,
)

data class GraphFunction(
    val id: Int,
    val expression: String,
    val color: Long,
    val isValid: Boolean = true,
)

data class Viewport(
    val xMin: Double = -10.0,
    val xMax: Double = 10.0,
    val yMin: Double = -6.0,
    val yMax: Double = 6.0,
)

data class TracePoint(
    val functionId: Int,
    val x: Double,
    val y: Double,
)
