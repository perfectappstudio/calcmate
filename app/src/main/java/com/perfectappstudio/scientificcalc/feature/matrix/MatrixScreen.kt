package com.perfectappstudio.scientificcalc.feature.matrix

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perfectappstudio.scientificcalc.core.model.MatrixData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MatrixScreen(
    modifier: Modifier = Modifier,
    viewModel: MatrixViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = "Matrix",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // --- Tab to select editing matrix (A/B/C) ---
        val matrixOptions = listOf('A', 'B', 'C')
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            matrixOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = state.editingMatrix == label,
                    onClick = { viewModel.selectMatrix(label) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = matrixOptions.size,
                    ),
                ) {
                    Text("Mat$label")
                }
            }
        }

        // --- Grid editor for the selected matrix ---
        val editing = state.editingMatrix
        if (editing != null) {
            val (rows, cols, cells) = when (editing) {
                'A' -> Triple(state.dimRowsA, state.dimColsA, state.cellsA)
                'B' -> Triple(state.dimRowsB, state.dimColsB, state.cellsB)
                'C' -> Triple(state.dimRowsC, state.dimColsC, state.cellsC)
                else -> Triple(2, 2, state.cellsA)
            }

            // Dimension pickers
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                Text("Size: ", style = MaterialTheme.typography.bodyLarge)
                DimensionDropdown(
                    value = rows,
                    onValueChange = { viewModel.setDimensions(editing, it, cols) },
                    label = "Rows",
                )
                Text(" × ", style = MaterialTheme.typography.bodyLarge)
                DimensionDropdown(
                    value = cols,
                    onValueChange = { viewModel.setDimensions(editing, rows, it) },
                    label = "Cols",
                )
            }

            // Cell grid
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                for (r in 0 until rows) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 2.dp),
                    ) {
                        for (c in 0 until cols) {
                            OutlinedTextField(
                                value = cells[r][c],
                                onValueChange = { viewModel.setCell(editing, r, c, it) },
                                modifier = Modifier.width(72.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }

            FilledTonalButton(
                onClick = { viewModel.storeMatrix(editing) },
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Text("Store Mat$editing")
            }
        }

        // --- Expression input ---
        OutlinedTextField(
            value = state.expression,
            onValueChange = viewModel::onExpressionChange,
            label = { Text("Expression") },
            placeholder = { Text("e.g. MatA × MatB") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
        )

        // --- Operation buttons ---
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            listOf("MatA", "MatB", "MatC").forEach { label ->
                OutlinedButton(onClick = { viewModel.appendToExpression(label) }) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            }
            listOf("+" to "+", "-" to "-", "×" to "×").forEach { (label, op) ->
                OutlinedButton(onClick = { viewModel.appendToExpression(" $op ") }) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("Det ") }) {
                Text("Det", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("Trn ") }) {
                Text("Trn", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("⁻¹") }) {
                Text("x⁻¹", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("²") }) {
                Text("x²", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("³") }) {
                Text("x³", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("Abs ") }) {
                Text("Abs", style = MaterialTheme.typography.labelMedium)
            }
        }

        // --- Evaluate / Clear ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Button(onClick = { viewModel.evaluate() }) {
                Text("=")
            }
            OutlinedButton(onClick = { viewModel.clearExpression() }) {
                Text("AC")
            }
        }

        // --- Error ---
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        // --- Result display ---
        state.matAns?.let { ans ->
            Text(
                text = "MatAns",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            MatrixResultGrid(matrixData = ans)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DimensionDropdown(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .width(72.dp)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            (1..3).forEach { dim ->
                DropdownMenuItem(
                    text = { Text(dim.toString()) },
                    onClick = {
                        onValueChange(dim)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun MatrixResultGrid(matrixData: MatrixData) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(bottom = 8.dp),
    ) {
        for (r in 0 until matrixData.rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (c in 0 until matrixData.cols) {
                    Text(
                        text = formatValue(matrixData.data[r][c]),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(72.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private fun formatValue(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        "%.6g".format(value)
    }
}
