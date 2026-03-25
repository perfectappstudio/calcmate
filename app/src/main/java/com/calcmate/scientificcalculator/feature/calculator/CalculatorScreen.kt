package com.calcmate.scientificcalculator.feature.calculator

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
        if (isLandscape) {
            LandscapeLayout(
                state = state,
                onAction = viewModel::onAction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            PortraitLayout(
                state = state,
                onAction = viewModel::onAction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
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
