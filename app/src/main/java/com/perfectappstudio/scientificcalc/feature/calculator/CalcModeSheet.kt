package com.perfectappstudio.scientificcalc.feature.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet for the CALC function (R31).
 *
 * Scans [expression] for single-letter variables (A-F, X, Y),
 * presents input fields for each, and evaluates with the substituted values.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcModeSheet(
    expression: String,
    onDismiss: () -> Unit,
    onEvaluate: (Map<Char, Double>) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Scan expression for variables A-F, X, Y
    val variables: List<Char> = remember(expression) {
        val allowed = "ABCDEFXY".toSet()
        expression
            .filter { it in allowed }
            .toSet()
            .sorted()
    }

    val values = remember(expression) {
        mutableStateMapOf<Char, String>().apply {
            variables.forEach { putIfAbsent(it, "") }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "CALC - Enter Variable Values",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp),
            )

            Text(
                text = expression,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            if (variables.isEmpty()) {
                Text(
                    text = "No variables found in expression.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                ) {
                    items(variables) { variable ->
                        OutlinedTextField(
                            value = values[variable] ?: "",
                            onValueChange = { values[variable] = it },
                            label = { Text("$variable =") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val parsed = values.mapValues { (_, v) ->
                            v.toDoubleOrNull() ?: 0.0
                        }
                        onEvaluate(parsed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Calculate")
                }

                OutlinedButton(
                    onClick = {
                        variables.forEach { values[it] = "" }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                ) {
                    Text("Reset Values")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
