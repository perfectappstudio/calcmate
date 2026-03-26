package com.calcmate.scientificcalculator.feature.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calcmate.scientificcalculator.core.model.RegressionType
import com.calcmate.scientificcalculator.core.model.StatType
import com.calcmate.scientificcalculator.core.model.StatisticsMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // --- Mode toggle: SD / REG ---
        val modeOptions = listOf(StatisticsMode.SD to "SD", StatisticsMode.REG to "REG")

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            modeOptions.forEachIndexed { index, (mode, label) ->
                SegmentedButton(
                    selected = state.mode == mode,
                    onClick = { viewModel.setMode(mode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = modeOptions.size,
                    ),
                ) {
                    Text(label)
                }
            }
        }

        // --- Result display ---
        AnimatedVisibility(visible = state.lastResult.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Text(
                    text = state.lastResult,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        when (state.mode) {
            StatisticsMode.SD -> SDPanel(viewModel = viewModel)
            StatisticsMode.REG -> REGPanel(viewModel = viewModel)
        }
    }
}

// ---------------------------------------------------------------
// SD Mode Panel
// ---------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SDPanel(viewModel: StatisticsViewModel) {
    val state by viewModel.state.collectAsState()

    Column {
        // --- Stat recall chips ---
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            val sdStats = listOf(
                StatType.N, StatType.MEAN_X, StatType.POPULATION_STD_DEV_X,
                StatType.SAMPLE_STD_DEV_X, StatType.SUM_X, StatType.SUM_X2,
            )
            sdStats.forEach { statType ->
                AssistChip(
                    onClick = { viewModel.computeStatistic(statType) },
                    label = { Text(statType.label) },
                )
            }
        }

        // --- Normal distribution ---
        NormalDistributionSection(viewModel = viewModel)

        Spacer(modifier = Modifier.height(8.dp))

        // --- Data entry ---
        SDDataEntry(viewModel = viewModel)

        Spacer(modifier = Modifier.height(8.dp))

        // --- Data list + clear ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Data (${state.sdData.size} entries)",
                style = MaterialTheme.typography.titleSmall,
            )
            if (state.sdData.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearSDData() }) {
                    Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear all")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
        ) {
            itemsIndexed(state.sdData) { index, dp ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${index + 1}. x = ${dp.value}" +
                            if (dp.frequency > 1) "  (freq: ${dp.frequency})" else "",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    IconButton(onClick = { viewModel.removeSDData(index) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun SDDataEntry(viewModel: StatisticsViewModel) {
    var valueText by rememberSaveable { mutableStateOf("") }
    var freqText by rememberSaveable { mutableStateOf("1") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        OutlinedTextField(
            value = valueText,
            onValueChange = { valueText = it },
            label = { Text("x") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = freqText,
            onValueChange = { freqText = it },
            label = { Text("Freq") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(72.dp),
        )
        FilledTonalButton(
            onClick = {
                val v = valueText.toDoubleOrNull() ?: return@FilledTonalButton
                val f = freqText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                viewModel.addSDData(v, f)
                valueText = ""
                freqText = "1"
            },
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

// ---------------------------------------------------------------
// REG Mode Panel
// ---------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun REGPanel(viewModel: StatisticsViewModel) {
    val state by viewModel.state.collectAsState()

    Column {
        // --- Regression type selector ---
        RegressionTypeSelector(
            selected = state.regressionType,
            onSelect = viewModel::setRegressionType,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Stat recall chips ---
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 4.dp),
        ) {
            val xStats = listOf(
                StatType.N, StatType.MEAN_X, StatType.POPULATION_STD_DEV_X,
                StatType.SAMPLE_STD_DEV_X, StatType.SUM_X, StatType.SUM_X2,
            )
            xStats.forEach { statType ->
                AssistChip(
                    onClick = { viewModel.computeStatistic(statType) },
                    label = { Text(statType.label) },
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 4.dp),
        ) {
            val yStats = listOf(
                StatType.MEAN_Y, StatType.POPULATION_STD_DEV_Y,
                StatType.SAMPLE_STD_DEV_Y, StatType.SUM_Y, StatType.SUM_Y2,
                StatType.SUM_XY,
            )
            yStats.forEach { statType ->
                AssistChip(
                    onClick = { viewModel.computeStatistic(statType) },
                    label = { Text(statType.label) },
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            val regStats = if (state.regressionType == RegressionType.QUADRATIC) {
                listOf(StatType.REG_A, StatType.REG_B, StatType.REG_C)
            } else {
                listOf(StatType.REG_A, StatType.REG_B, StatType.REG_R)
            }
            regStats.forEach { statType ->
                AssistChip(
                    onClick = { viewModel.computeStatistic(statType) },
                    label = { Text(statType.label) },
                )
            }
        }

        // --- Data entry ---
        REGDataEntry(viewModel = viewModel)

        Spacer(modifier = Modifier.height(8.dp))

        // --- Data list + clear ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Data (${state.regData.size} pairs)",
                style = MaterialTheme.typography.titleSmall,
            )
            if (state.regData.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearREGData() }) {
                    Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear all")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
        ) {
            itemsIndexed(state.regData) { index, dp ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${index + 1}. (${dp.x}, ${dp.y})" +
                            if (dp.frequency > 1) "  freq: ${dp.frequency}" else "",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    IconButton(onClick = { viewModel.removeREGData(index) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun REGDataEntry(viewModel: StatisticsViewModel) {
    var xText by rememberSaveable { mutableStateOf("") }
    var yText by rememberSaveable { mutableStateOf("") }
    var freqText by rememberSaveable { mutableStateOf("1") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        OutlinedTextField(
            value = xText,
            onValueChange = { xText = it },
            label = { Text("x") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = yText,
            onValueChange = { yText = it },
            label = { Text("y") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = freqText,
            onValueChange = { freqText = it },
            label = { Text("F") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(56.dp),
        )
        FilledTonalButton(
            onClick = {
                val x = xText.toDoubleOrNull() ?: return@FilledTonalButton
                val y = yText.toDoubleOrNull() ?: return@FilledTonalButton
                val f = freqText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                viewModel.addREGData(x, y, f)
                xText = ""
                yText = ""
                freqText = "1"
            },
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

// ---------------------------------------------------------------
// Regression Type Selector
// ---------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegressionTypeSelector(
    selected: RegressionType,
    onSelect: (RegressionType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = "${selected.label}: ${selected.formula}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Regression type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            RegressionType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text("${type.label}: ${type.formula}") },
                    onClick = {
                        onSelect(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

// ---------------------------------------------------------------
// Normal Distribution Section
// ---------------------------------------------------------------

@Composable
private fun NormalDistributionSection(viewModel: StatisticsViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    FilledTonalButton(
        onClick = { showDialog = true },
        modifier = Modifier.padding(bottom = 4.dp),
    ) {
        Text("DISTR (P/Q/R)")
    }

    if (showDialog) {
        NormalDistributionDialog(
            onDismiss = { showDialog = false },
            onCompute = { type, t ->
                viewModel.computeNormal(type, t)
                showDialog = false
            },
        )
    }
}

@Composable
private fun NormalDistributionDialog(
    onDismiss: () -> Unit,
    onCompute: (StatType, Double) -> Unit,
) {
    var tText by rememberSaveable { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(StatType.NORMAL_P) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Normal Distribution") },
        text = {
            Column {
                Text(
                    text = "P(t): area from -\u221E to t\nQ(t): area from 0 to t\nR(t): area from t to \u221E",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                // Type selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    listOf(StatType.NORMAL_P, StatType.NORMAL_Q, StatType.NORMAL_R).forEach { type ->
                        AssistChip(
                            onClick = { selectedType = type },
                            label = {
                                Text(
                                    text = type.label,
                                    fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                        )
                    }
                }

                OutlinedTextField(
                    value = tText,
                    onValueChange = { tText = it },
                    label = { Text("t value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val t = tText.toDoubleOrNull() ?: return@TextButton
                    onCompute(selectedType, t)
                },
            ) {
                Text("Compute")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
