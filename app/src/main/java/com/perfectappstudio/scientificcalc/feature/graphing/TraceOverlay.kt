package com.perfectappstudio.scientificcalc.feature.graphing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.model.GraphFunction
import com.perfectappstudio.scientificcalc.core.model.TracePoint
import com.perfectappstudio.scientificcalc.core.model.Viewport

/**
 * Overlay drawn on top of the graph canvas when the user is tracing.
 * Shows a small filled circle at the trace point plus a floating label
 * with coordinates formatted to 2 decimal places.
 */
@Composable
fun TraceOverlay(
    tracePoint: TracePoint,
    viewport: Viewport,
    functions: List<GraphFunction>,
    canvasWidth: Float,
    canvasHeight: Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    // Convert math coords to screen coords.
    val sx = ((tracePoint.x - viewport.xMin) / (viewport.xMax - viewport.xMin) * canvasWidth).toFloat()
    val sy = ((viewport.yMax - tracePoint.y) / (viewport.yMax - viewport.yMin) * canvasHeight).toFloat()

    // Determine the dot color from the traced function.
    val func = functions.find { it.id == tracePoint.functionId }
    val dotColor = if (func != null) Color(func.color) else Color.White

    // Label positioning: show above the point normally, or below if too close to top.
    val labelOffsetY = if (sy > 80f) -60f else 30f
    val labelText = "(%.2f, %.2f)".format(tracePoint.x, tracePoint.y)

    Box(modifier = modifier.fillMaxSize()) {
        // Dot
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Outer ring
            drawCircle(
                color = Color.White,
                radius = 10f,
                center = Offset(sx, sy),
            )
            // Inner filled circle
            drawCircle(
                color = dotColor,
                radius = 7f,
                center = Offset(sx, sy),
            )
        }

        // Floating label
        val offsetXDp = with(density) { (sx - 50f).toDp() }
        val offsetYDp = with(density) { (sy + labelOffsetY).toDp() }

        // Clamp the label within canvas bounds.
        val clampedXPx = sx.coerceIn(10f, (canvasWidth - 110f).coerceAtLeast(10f))
        val clampedYPx = (sy + labelOffsetY).coerceIn(4f, (canvasHeight - 36f).coerceAtLeast(4f))

        Surface(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = clampedXPx.toInt() - 50,
                        y = clampedYPx.toInt(),
                    )
                },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.85f),
            shadowElevation = 4.dp,
        ) {
            Text(
                text = labelText,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
    }
}
