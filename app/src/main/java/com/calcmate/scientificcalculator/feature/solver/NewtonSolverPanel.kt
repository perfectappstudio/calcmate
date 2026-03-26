package com.calcmate.scientificcalculator.feature.solver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.calcmate.scientificcalculator.core.math.NewtonResult
import com.calcmate.scientificcalculator.core.model.SolverResult
import com.calcmate.scientificcalculator.core.model.SolverState

@Composable
fun NewtonSolverPanel(
    state: SolverState,
    onExpressionChange: (String) -> Unit,
    onGuessChange: (String) -> Unit,
    onSolve: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Solve f(x) = 0",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "Enter an expression using X as the variable",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        OutlinedTextField(
            value = state.newtonExpression,
            onValueChange = onExpressionChange,
            label = { Text("f(X)") },
            placeholder = { Text("e.g. X^2 - 2") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.newtonGuess,
            onValueChange = onGuessChange,
            label = { Text("Initial guess") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSolve,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Solve")
        }

        Spacer(Modifier.height(16.dp))

        // Result card
        state.result?.let { result ->
            NewtonResultCard(result = result)
        }
    }
}

@Composable
private fun NewtonResultCard(result: SolverResult) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Result",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            when (result) {
                is SolverResult.Newton -> NewtonBody(result.result)
                is SolverResult.Error -> {
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                else -> { /* not applicable */ }
            }
        }
    }
}

@Composable
private fun NewtonBody(nr: NewtonResult) {
    when (nr) {
        is NewtonResult.Solution -> {
            Text(
                text = "x = ${formatNumber(nr.x)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Converged in ${nr.iterations} iteration${if (nr.iterations != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        is NewtonResult.CannotSolve -> {
            Text(
                text = nr.reason,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
