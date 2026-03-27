package com.perfectappstudio.scientificcalc.feature.graphing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.perfectappstudio.scientificcalc.core.model.GraphFunction
import com.perfectappstudio.scientificcalc.core.model.GraphState
import com.perfectappstudio.scientificcalc.core.model.Viewport
import com.perfectappstudio.scientificcalc.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Custom Canvas that renders the coordinate grid, function curves, and handles
 * zoom / pan / tap-to-trace gestures.
 */
@Composable
fun GraphCanvas(
    state: GraphState,
    viewModel: GraphViewModel,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = GlassLight
    val gridColor = Color.White.copy(alpha = 0.05f)
    val axisColor = Color.White.copy(alpha = 0.20f)
    val labelColor = Color.White.copy(alpha = 0.30f)
    val density = LocalDensity.current

    // Pre-computed sample points per function (evaluated off the main thread).
    var sampledCurves by remember { mutableStateOf<Map<Int, List<Pair<Double, Double>>>>(emptyMap()) }

    // Trigger re-sampling whenever viewport or functions change.
    // The key combines viewport values and function expressions.
    var recompKey by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.viewport, state.functions.map { it.id to it.expression }) {
        recompKey++
        val funcs = state.functions
        val result = withContext(Dispatchers.Default) {
            funcs.filter { it.isValid && it.expression.isNotBlank() }
                .associate { func ->
                    func.id to viewModel.sampleFunction(func.expression)
                }
        }
        sampledCurves = result
    }

    val labelSizeSp = 10.sp
    val labelSizePx = with(density) { labelSizeSp.toPx() }

    // Gesture: pinch-to-zoom and drag-to-pan combined via transformable.
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val vp = state.viewport
        val xRange = vp.xMax - vp.xMin
        val yRange = vp.yMax - vp.yMin

        // Zoom: scale range inversely with gesture zoom factor.
        val newXRange = xRange / zoomChange
        val newYRange = yRange / zoomChange
        val xCenter = (vp.xMin + vp.xMax) / 2.0
        val yCenter = (vp.yMin + vp.yMax) / 2.0

        // Pan: convert pixel pan to math-space delta.
        // panChange is in pixels; positive panX = drag right = shift viewport left.
        val xShift = -(panChange.x / 1000f) * newXRange
        val yShift = (panChange.y / 1000f) * newYRange

        viewModel.setViewport(
            Viewport(
                xMin = xCenter - newXRange / 2 + xShift,
                xMax = xCenter + newXRange / 2 + xShift,
                yMin = yCenter - newYRange / 2 + yShift,
                yMax = yCenter + newYRange / 2 + yShift,
            )
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .transformable(state = transformState)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    viewModel.trace(
                        screenX = offset.x,
                        screenY = offset.y,
                        canvasWidth = size.width.toFloat(),
                        canvasHeight = size.height.toFloat(),
                    )
                }
            },
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val vp = state.viewport

        // --- Background ---
        drawRect(color = backgroundColor, size = size)

        // --- Grid lines ---
        drawGrid(vp, canvasWidth, canvasHeight, gridColor, labelColor, labelSizePx)

        // --- Axes ---
        drawAxes(vp, canvasWidth, canvasHeight, axisColor, labelColor, labelSizePx)

        // --- Function curves ---
        val funcMap = state.functions.associateBy { it.id }
        for ((funcId, points) in sampledCurves) {
            val func = funcMap[funcId] ?: continue
            drawFunctionCurve(func, points, vp, canvasWidth, canvasHeight)
        }
    }
}

// ---------------------------------------------------------------------------
// Drawing helpers
// ---------------------------------------------------------------------------

/**
 * Picks a "nice" step size (1, 2, or 5 multiplied by a power of 10) so that
 * roughly 5-12 grid lines appear in the visible range.
 */
private fun niceStep(range: Double): Double {
    val rough = range / 8.0
    val magnitude = 10.0.pow(floor(log10(rough)))
    val residual = rough / magnitude
    val nice = when {
        residual <= 1.5 -> 1.0
        residual <= 3.5 -> 2.0
        residual <= 7.5 -> 5.0
        else -> 10.0
    }
    return nice * magnitude
}

private fun DrawScope.drawGrid(
    vp: Viewport,
    canvasWidth: Float,
    canvasHeight: Float,
    gridColor: Color,
    labelColor: Color,
    labelSize: Float,
) {
    val xStep = niceStep(vp.xMax - vp.xMin)
    val yStep = niceStep(vp.yMax - vp.yMin)

    val gridStroke = Stroke(width = 1f)

    // Vertical grid lines
    var gx = ceil(vp.xMin / xStep) * xStep
    while (gx <= vp.xMax) {
        val sx = ((gx - vp.xMin) / (vp.xMax - vp.xMin) * canvasWidth).toFloat()
        drawLine(
            color = gridColor,
            start = Offset(sx, 0f),
            end = Offset(sx, canvasHeight),
            strokeWidth = gridStroke.width,
        )
        gx += xStep
    }

    // Horizontal grid lines
    var gy = ceil(vp.yMin / yStep) * yStep
    while (gy <= vp.yMax) {
        val sy = ((vp.yMax - gy) / (vp.yMax - vp.yMin) * canvasHeight).toFloat()
        drawLine(
            color = gridColor,
            start = Offset(0f, sy),
            end = Offset(canvasWidth, sy),
            strokeWidth = gridStroke.width,
        )
        gy += yStep
    }
}

private fun DrawScope.drawAxes(
    vp: Viewport,
    canvasWidth: Float,
    canvasHeight: Float,
    axisColor: Color,
    labelColor: Color,
    labelSize: Float,
) {
    val axisStrokeWidth = 2f
    val tickHalf = 6f

    // Y-axis (x = 0)
    if (vp.xMin <= 0 && vp.xMax >= 0) {
        val sx = ((0 - vp.xMin) / (vp.xMax - vp.xMin) * canvasWidth).toFloat()
        drawLine(axisColor, Offset(sx, 0f), Offset(sx, canvasHeight), axisStrokeWidth)

        // Tick marks & labels along y-axis
        val yStep = niceStep(vp.yMax - vp.yMin)
        var gy = ceil(vp.yMin / yStep) * yStep
        while (gy <= vp.yMax) {
            if (abs(gy) > yStep * 0.01) {
                val sy = ((vp.yMax - gy) / (vp.yMax - vp.yMin) * canvasHeight).toFloat()
                drawLine(axisColor, Offset(sx - tickHalf, sy), Offset(sx + tickHalf, sy), axisStrokeWidth)
                drawContext.canvas.nativeCanvas.drawText(
                    formatTickLabel(gy),
                    sx + tickHalf + 4f,
                    sy + labelSize / 3f,
                    android.graphics.Paint().apply {
                        color = labelColor.hashCode()
                        textSize = labelSize
                        isAntiAlias = true
                    },
                )
            }
            gy += yStep
        }
    }

    // X-axis (y = 0)
    if (vp.yMin <= 0 && vp.yMax >= 0) {
        val sy = ((vp.yMax - 0) / (vp.yMax - vp.yMin) * canvasHeight).toFloat()
        drawLine(axisColor, Offset(0f, sy), Offset(canvasWidth, sy), axisStrokeWidth)

        // Tick marks & labels along x-axis
        val xStep = niceStep(vp.xMax - vp.xMin)
        var gx = ceil(vp.xMin / xStep) * xStep
        while (gx <= vp.xMax) {
            if (abs(gx) > xStep * 0.01) {
                val sx = ((gx - vp.xMin) / (vp.xMax - vp.xMin) * canvasWidth).toFloat()
                drawLine(axisColor, Offset(sx, sy - tickHalf), Offset(sx, sy + tickHalf), axisStrokeWidth)
                drawContext.canvas.nativeCanvas.drawText(
                    formatTickLabel(gx),
                    sx - 10f,
                    sy + tickHalf + labelSize + 2f,
                    android.graphics.Paint().apply {
                        color = labelColor.hashCode()
                        textSize = labelSize
                        isAntiAlias = true
                    },
                )
            }
            gx += xStep
        }
    }
}

private fun DrawScope.drawFunctionCurve(
    func: GraphFunction,
    points: List<Pair<Double, Double>>,
    vp: Viewport,
    canvasWidth: Float,
    canvasHeight: Float,
) {
    if (points.isEmpty()) return

    val curveColor = Color(func.color)
    val viewportHeight = vp.yMax - vp.yMin

    val path = Path()
    var drawing = false

    for (i in points.indices) {
        val (mx, my) = points[i]

        // Skip NaN / Infinity
        if (my.isNaN() || my.isInfinite()) {
            drawing = false
            continue
        }

        // Detect discontinuity (e.g. tan asymptotes)
        if (drawing && i > 0) {
            val prevY = points[i - 1].second
            if (!prevY.isNaN() && !prevY.isInfinite() && abs(my - prevY) > viewportHeight) {
                drawing = false
            }
        }

        val sx = ((mx - vp.xMin) / (vp.xMax - vp.xMin) * canvasWidth).toFloat()
        val sy = ((vp.yMax - my) / (vp.yMax - vp.yMin) * canvasHeight).toFloat()

        if (!drawing) {
            path.moveTo(sx, sy)
            drawing = true
        } else {
            path.lineTo(sx, sy)
        }
    }

    drawPath(
        path = path,
        color = curveColor,
        style = Stroke(width = 3f),
    )
}

/**
 * Formats a tick label: show integers without decimals; otherwise, trim
 * trailing zeros for a clean display.
 */
private fun formatTickLabel(value: Double): String {
    return if (value == value.roundToInt().toDouble()) {
        value.roundToInt().toString()
    } else {
        val s = "%.2f".format(value)
        s.trimEnd('0').trimEnd('.')
    }
}
