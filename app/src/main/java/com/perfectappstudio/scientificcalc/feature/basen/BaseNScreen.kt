package com.perfectappstudio.scientificcalc.feature.basen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perfectappstudio.scientificcalc.core.model.BaseNState
import com.perfectappstudio.scientificcalc.core.parser.NumberBase
import com.perfectappstudio.scientificcalc.feature.calculator.ExpressionDisplay

@Composable
fun BaseNScreen(
    viewModel: BaseNViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
    ) {
        // Base selector chips
        BaseSelectorRow(
            currentBase = state.currentBase,
            onBaseChange = viewModel::onBaseChange,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        // Display area
        Spacer(modifier = Modifier.weight(1f))
        ExpressionDisplay(
            expression = state.expression,
            result = state.result,
            error = state.error,
            hasEvaluated = false,
            modifier = Modifier.fillMaxWidth(),
        )

        // Keypad
        BaseNKeypad(
            currentBase = state.currentBase,
            onDigit = viewModel::onDigit,
            onOperator = viewModel::onOperator,
            onEquals = viewModel::onEquals,
            onClear = viewModel::onClear,
            onDelete = viewModel::onDelete,
            onOpenParen = viewModel::onOpenParen,
            onCloseParen = viewModel::onCloseParen,
            modifier = Modifier.padding(bottom = 8.dp),
        )
    }
}

@Composable
private fun BaseSelectorRow(
    currentBase: NumberBase,
    onBaseChange: (NumberBase) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        NumberBase.entries.forEach { base ->
            FilterChip(
                selected = currentBase == base,
                onClick = { onBaseChange(base) },
                label = {
                    Text(
                        text = base.name,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}
