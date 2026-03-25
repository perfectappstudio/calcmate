package com.calcmate.scientificcalculator.feature.calculator

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calcmate.scientificcalculator.core.model.CalculatorAction
import com.calcmate.scientificcalculator.core.model.CalculatorState

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (isLandscape) {
                LandscapeLayout(
                    state = state,
                    onAction = viewModel::onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                PortraitLayout(
                    state = state,
                    onAction = viewModel::onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // History icon button in top-right corner
            IconButton(
                onClick = { viewModel.onAction(CalculatorAction.ShowHistory) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    // History bottom sheet
    if (state.showHistory) {
        HistorySheet(
            entries = state.historyEntries,
            onDismiss = { viewModel.onAction(CalculatorAction.HideHistory) },
            onEntryClick = { entry ->
                viewModel.onAction(CalculatorAction.ReuseHistoryEntry(entry))
            },
            onDeleteEntry = { entry ->
                viewModel.onAction(CalculatorAction.DeleteHistoryEntry(entry))
            },
            onClearAll = { viewModel.onAction(CalculatorAction.ClearHistory) },
        )
    }
}

@Composable
private fun PortraitLayout(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ExpressionDisplay(
            expression = state.expression,
            result = state.result,
            error = state.error,
            hasEvaluated = state.hasEvaluated,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        ScientificKeypad(
            state = state,
            onAction = onAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LandscapeLayout(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ExpressionDisplay(
            expression = state.expression,
            result = state.result,
            error = state.error,
            hasEvaluated = state.hasEvaluated,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        ScientificKeypadLand(
            state = state,
            onAction = onAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
