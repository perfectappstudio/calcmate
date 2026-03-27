package com.perfectappstudio.scientificcalc.feature.solver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.math.LinearResult
import com.perfectappstudio.scientificcalc.core.math.QuadraticResult
import com.perfectappstudio.scientificcalc.core.model.SolverResult
import com.perfectappstudio.scientificcalc.core.model.SolverState

// Discriminant color coding
private val GreenRoots = Color(0xFF4CAF50)
private val AmberRepeated = Color(0xFFFFC107)
private val RedComplex = Color(0xFFEF5350)

@Composable
fun QuadraticSolverPanel(
    state: SolverState,
    onCoefficientChange: (Int, String) -> Unit,
    onSolve: () -> Unit,
    onToggleSteps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quadratic Equation: ax\u00B2 + bx + c = 0",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // --- Three coefficient inputs in a row ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = state.quadA,
                onValueChange = { onCoefficientChange(0, it) },
                label = { Text("a") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = state.quadB,
                onValueChange = { onCoefficientChange(1, it) },
                label = { Text("b") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = state.quadC,
                onValueChange = { onCoefficientChange(2, it) },
                label = { Text("c") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSolve,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Solve")
        }

        Spacer(Modifier.height(16.dp))

        // --- Result card ---
        state.result?.let { result ->
            QuadraticResultCard(
                result = result,
                showSteps = state.showSteps,
                onToggleSteps = onToggleSteps,
            )
        }
    }
}

@Composable
private fun QuadraticResultCard(
    result: SolverResult,
    showSteps: Boolean,
    onToggleSteps: () -> Unit,
) {
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
                is SolverResult.Quadratic -> QuadraticBody(result.result, showSteps, onToggleSteps)
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
private fun QuadraticBody(
    qr: QuadraticResult,
    showSteps: Boolean,
    onToggleSteps: () -> Unit,
) {
    when (qr) {
        is QuadraticResult.TwoRealRoots -> {
            DiscriminantBadge(
                label = "\u0394 = ${formatNumber(qr.discriminant)}",
                description = "Two distinct real roots",
                color = GreenRoots,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "x\u2081 = ${formatNumber(qr.x1)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "x\u2082 = ${formatNumber(qr.x2)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            StepsSection(qr.steps, showSteps, onToggleSteps)
        }

        is QuadraticResult.OneRepeatedRoot -> {
            DiscriminantBadge(
                label = "\u0394 = 0",
                description = "One repeated root",
                color = AmberRepeated,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "x = ${formatNumber(qr.x)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            StepsSection(qr.steps, showSteps, onToggleSteps)
        }

        is QuadraticResult.ComplexRoots -> {
            DiscriminantBadge(
                label = "\u0394 = ${formatNumber(qr.discriminant)}",
                description = "Complex conjugate roots",
                color = RedComplex,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "x = ${formatNumber(qr.realPart)} \u00B1 ${formatNumber(qr.imaginaryPart)}i",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            StepsSection(qr.steps, showSteps, onToggleSteps)
        }

        is QuadraticResult.DegenerateLinear -> {
            Text(
                text = "Degenerates to linear (a = 0)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            when (val lr = qr.linearResult) {
                is LinearResult.Solution -> {
                    Text(
                        text = "x = ${formatNumber(lr.x)}",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                is LinearResult.NoSolution -> {
                    Text(
                        text = "No solution",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                is LinearResult.InfiniteSolutions -> {
                    Text(
                        text = "Infinite solutions",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscriminantBadge(label: String, description: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
        Text(
            text = "\u2014 $description",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StepsSection(
    steps: List<String>,
    showSteps: Boolean,
    onToggleSteps: () -> Unit,
) {
    Spacer(Modifier.height(8.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    TextButton(
        onClick = onToggleSteps,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(if (showSteps) "Hide steps" else "Show steps")
    }
    AnimatedVisibility(
        visible = showSteps,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
        ) {
            steps.forEach { step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
