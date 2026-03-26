package com.calcmate.scientificcalculator.feature.vector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VectorScreen(
    modifier: Modifier = Modifier,
    viewModel: VectorViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = "Vector",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // --- Tab to select editing vector (A/B/C) ---
        val vectorOptions = listOf('A', 'B', 'C')
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            vectorOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = state.editingVector == label,
                    onClick = { viewModel.selectVector(label) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = vectorOptions.size,
                    ),
                ) {
                    Text("Vct$label")
                }
            }
        }

        // --- Component editor for the selected vector ---
        val editing = state.editingVector
        if (editing != null) {
            val (dim, cells) = when (editing) {
                'A' -> state.dimA to state.cellsA
                'B' -> state.dimB to state.cellsB
                'C' -> state.dimC to state.cellsC
                else -> 2 to state.cellsA
            }

            // Dimension picker: 2D or 3D
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                Text("Dimension: ", style = MaterialTheme.typography.bodyLarge)
                SingleChoiceSegmentedButtonRow {
                    listOf(2, 3).forEachIndexed { index, d ->
                        SegmentedButton(
                            selected = dim == d,
                            onClick = { viewModel.setDimension(editing, d) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = 2,
                            ),
                        ) {
                            Text("${d}D")
                        }
                    }
                }
            }

            // Component input fields
            val componentLabels = listOf("x", "y", "z")
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                for (i in 0 until dim) {
                    OutlinedTextField(
                        value = cells[i],
                        onValueChange = { viewModel.setComponent(editing, i, it) },
                        label = { Text(componentLabels[i]) },
                        modifier = Modifier.width(88.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            FilledTonalButton(
                onClick = { viewModel.storeVector(editing) },
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Text("Store Vct$editing")
            }
        }

        // --- Expression input ---
        OutlinedTextField(
            value = state.expression,
            onValueChange = viewModel::onExpressionChange,
            label = { Text("Expression") },
            placeholder = { Text("e.g. VctA + VctB") },
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
            listOf("VctA", "VctB", "VctC").forEach { label ->
                OutlinedButton(onClick = { viewModel.appendToExpression(label) }) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            }
            OutlinedButton(onClick = { viewModel.appendToExpression(" + ") }) {
                Text("+", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression(" - ") }) {
                Text("-", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("Dot ") }) {
                Text("Dot(·)", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = { viewModel.appendToExpression("Cross ") }) {
                Text("Cross(×)", style = MaterialTheme.typography.labelMedium)
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
        state.vctAns?.let { ans ->
            Text(
                text = "VctAns",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val labels = listOf("x", "y", "z")
                for (i in ans.indices) {
                    Text(
                        text = "${labels.getOrElse(i) { "$i" }} = ${formatValue(ans[i])}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
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
