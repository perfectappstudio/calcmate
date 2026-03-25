package com.calcmate.scientificcalculator.feature.graphing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Main graphing screen that composes the top bar, function input controls,
 * and the graph canvas with optional trace overlay.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(
    viewModel: GraphViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // ---- Top app bar ----
        TopAppBar(
            title = { Text("Graph") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            actions = {
                IconButton(onClick = { viewModel.resetZoom() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset zoom",
                    )
                }
                IconButton(onClick = { /* settings placeholder */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                    )
                }
            },
        )

        // ---- Function input controls (~30% of screen) ----
        GraphControls(
            functions = state.functions,
            onAdd = { viewModel.addFunction() },
            onRemove = { id -> viewModel.removeFunction(id) },
            onUpdate = { id, expr -> viewModel.updateFunction(id, expr) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 260.dp),
        )

        // ---- Graph canvas (remaining space) + trace overlay ----
        var canvasWidth by remember { mutableStateOf(0f) }
        var canvasHeight by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onSizeChanged { size ->
                    canvasWidth = size.width.toFloat()
                    canvasHeight = size.height.toFloat()
                },
        ) {
            GraphCanvas(
                state = state,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
            )

            // Trace overlay
            if (state.isTracing && state.tracePoint != null) {
                TraceOverlay(
                    tracePoint = state.tracePoint!!,
                    viewport = state.viewport,
                    functions = state.functions,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
