package com.calcmate.scientificcalculator.feature.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calcmate.scientificcalculator.core.data.UnitData

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val availableUnits = UnitData.unitsFor(state.selectedCategory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    ) {
        // ── Category selector ───────────────────────────────────────
        CategorySelector(
            selected = state.selectedCategory,
            onSelect = viewModel::onCategoryChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Conversion card ─────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // FROM section
                Text(
                    text = "From",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                UnitPicker(
                    units = availableUnits,
                    selected = state.fromUnit,
                    onSelect = viewModel::onFromUnitChange,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))

                BasicTextField(
                    value = state.inputValue,
                    onValueChange = viewModel::onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Swap button
                IconButton(
                    onClick = viewModel::onSwapUnits,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap units",
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TO section
                Text(
                    text = "To",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                UnitPicker(
                    units = availableUnits,
                    selected = state.toUnit,
                    onSelect = viewModel::onToUnitChange,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = state.result.ifEmpty { "---" },
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Conversion formula hint
                if (state.result.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "1 ${state.fromUnit.symbol} = ${
                            formatFormulaValue(
                                UnitData.convert(1.0, state.fromUnit, state.toUnit),
                            )
                        } ${state.toUnit.symbol}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Quick conversions ───────────────────────────────────────
        if (state.quickConversions.isNotEmpty()) {
            Text(
                text = "More conversions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.quickConversions) { (label, value) ->
                    QuickConversionCard(label = label, value = value)
                }
            }
        }
    }
}

@Composable
private fun QuickConversionCard(
    label: String,
    value: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun formatFormulaValue(value: Double): String {
    if (value.isNaN() || value.isInfinite()) return "---"
    val str = "%.8g".format(value)
    return str.trimEnd('0').trimEnd('.')
}
