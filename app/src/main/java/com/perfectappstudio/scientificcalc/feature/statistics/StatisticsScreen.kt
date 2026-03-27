package com.perfectappstudio.scientificcalc.feature.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perfectappstudio.scientificcalc.core.model.RegressionType
import com.perfectappstudio.scientificcalc.core.model.StatType
import com.perfectappstudio.scientificcalc.core.model.StatisticsMode
import com.perfectappstudio.scientificcalc.ui.theme.*

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
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // --- Mode toggle: SD / REG (glass pills) ---
        val modeOptions = listOf(StatisticsMode.SD to "SD", StatisticsMode.REG to "REG")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            modeOptions.forEach { (mode, label) ->
                FilterChip(
                    selected = state.mode == mode,
                    onClick = { viewModel.setMode(mode) },
                    label = {
                        Text(
                            text = label,
                            color = if (state.mode == mode) Color.Black else Color.White,
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = state.mode == mode,
                        borderColor = GlassBorder,
                        selectedBorderColor = Color.Transparent,
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = GlassLight,
                        selectedContainerColor = PinkAccent,
                        labelColor = Color.White,
                        selectedLabelColor = Color.Black,
                    ),
                )
            }
        }

        // --- Result display ---
        AnimatedVisibility(visible = state.lastResult.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(
                        color = GlassMedium,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .border(
                        width = 1.dp,
                        color = GlassBorder,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(16.dp),
            ) {
                Text(
                    text = state.lastResult,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = PinkAccent,
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
            color = Color.White,
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
                GlassStatChip(
                    label = statType.label,
                    onClick = { viewModel.computeStatistic(statType) },
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
                color = Color.White,
            )
            if (state.sdData.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearSDData() }) {
                    Icon(
                        Icons.Outlined.DeleteSweep,
                        contentDescription = "Clear all",
                        tint = Color.White.copy(alpha = 0.6f),
                    )
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
                        .padding(vertical = 4.dp)
                        .background(
                            color = GlassLight,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${index + 1}. x = ${dp.value}" +
                            if (dp.frequency > 1) "  (freq: ${dp.frequency})" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                    )
                    IconButton(onClick = { viewModel.removeSDData(index) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.White.copy(alpha = 0.6f),
                        )
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
            label = { Text("x", color = Color.White.copy(alpha = 0.6f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PinkAccent,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PinkAccent,
            ),
        )
        OutlinedTextField(
            value = freqText,
            onValueChange = { freqText = it },
            label = { Text("Freq", color = Color.White.copy(alpha = 0.6f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(72.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PinkAccent,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PinkAccent,
            ),
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
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
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
            color = Color.White,
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
                GlassStatChip(
                    label = statType.label,
                    onClick = { viewModel.computeStatistic(statType) },
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
                GlassStatChip(
                    label = statType.label,
                    onClick = { viewModel.computeStatistic(statType) },
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
                GlassStatChip(
                    label = statType.label,
                    onClick = { viewModel.computeStatistic(statType) },
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
                color = Color.White,
            )
            if (state.regData.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearREGData() }) {
                    Icon(
                        Icons.Outlined.DeleteSweep,
                        contentDescription = "Clear all",
                        tint = Color.White.copy(alpha = 0.6f),
                    )
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
                        .padding(vertical = 4.dp)
                        .background(
                            color = GlassLight,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${index + 1}. (${dp.x}, ${dp.y})" +
                            if (dp.frequency > 1) "  freq: ${dp.frequency}" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                    )
                    IconButton(onClick = { viewModel.removeREGData(index) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.White.copy(alpha = 0.6f),
                        )
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
            label = { Text("x", color = Color.White.copy(alpha = 0.6f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PinkAccent,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PinkAccent,
            ),
        )
        OutlinedTextField(
            value = yText,
            onValueChange = { yText = it },
            label = { Text("y", color = Color.White.copy(alpha = 0.6f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PinkAccent,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PinkAccent,
            ),
        )
        OutlinedTextField(
            value = freqText,
            onValueChange = { freqText = it },
            label = { Text("F", color = Color.White.copy(alpha = 0.6f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PinkAccent,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PinkAccent,
            ),
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
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}

// ---------------------------------------------------------------
// Glass Stat Chip
// ---------------------------------------------------------------

@Composable
private fun GlassStatChip(
    label: String,
    onClick: () -> Unit,
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                color = Color.White,
            )
        },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, PinkAccent),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = GlassLight,
            labelColor = Color.White,
        ),
    )
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
            label = { Text("Regression type", color = Color.White.copy(alpha = 0.6f)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PinkAccent,
                unfocusedBorderColor = GlassBorder,
                cursorColor = PinkAccent,
            ),
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
        Text(
            text = "DISTR (P/Q/R)",
            color = PinkAccent,
        )
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
