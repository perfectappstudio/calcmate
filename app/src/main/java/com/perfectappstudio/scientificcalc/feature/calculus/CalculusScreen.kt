package com.perfectappstudio.scientificcalc.feature.calculus

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.parser.Formatter

@Composable
fun CalculusScreen(onUseResult: (Double) -> Unit, viewModel: CalculusViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focus = LocalFocusManager.current
    val keyboard = KeyboardOptions(autoCorrectEnabled = false)
    DisposableEffect(Unit) { onDispose { viewModel.cancel() } }
    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Calculus", style = MaterialTheme.typography.headlineMedium)
        Text("Derivatives at a point and definite integrals", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculusMode.entries.forEach { mode ->
                FilterChip(selected = state.mode == mode, onClick = { viewModel.edit { it.copy(mode = mode) } },
                    label = { Text(if (mode == CalculusMode.DERIVATIVE) "Derivative" else "Integral") })
            }
        }
        OutlinedTextField(
            value = state.function, onValueChange = { value -> viewModel.edit { it.copy(function = value) } },
            label = { Text("f(x)") }, supportingText = { Text("Use x, for example x^2 or sin(x)") },
            modifier = Modifier.fillMaxWidth().testTag("calculus-function"), singleLine = true, keyboardOptions = keyboard,
        )
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AngleUnit.entries.forEach { angle ->
                FilterChip(selected = state.angleUnit == angle, onClick = { viewModel.edit { it.copy(angleUnit = angle) } },
                    label = { Text(when (angle) {
                        AngleUnit.DEGREE -> "Degrees"
                        AngleUnit.RADIAN -> "Radians"
                        AngleUnit.GRADIAN -> "Gradians"
                    }) })
            }
        }
        if (state.mode == CalculusMode.DERIVATIVE) {
            OutlinedTextField(
                state.point, { value -> viewModel.edit { it.copy(point = value) } },
                label = { Text("At x =") }, singleLine = true,
                keyboardOptions = keyboard,
                modifier = Modifier.fillMaxWidth().testTag("calculus-point"),
            )
            OutlinedTextField(
                state.step, { value -> viewModel.edit { it.copy(step = value) } },
                label = { Text("Initial step (optional)") }, supportingText = { Text("Leave blank for automatic step selection") },
                singleLine = true, modifier = Modifier.fillMaxWidth().testTag("calculus-step"),
                keyboardOptions = keyboard,
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(state.lower, { value -> viewModel.edit { it.copy(lower = value) } },
                    label = { Text("Lower limit") }, singleLine = true,
                    keyboardOptions = keyboard,
                    modifier = Modifier.weight(1f).testTag("calculus-lower"))
                OutlinedTextField(state.upper, { value -> viewModel.edit { it.copy(upper = value) } },
                    label = { Text("Upper limit") }, singleLine = true,
                    keyboardOptions = keyboard,
                    modifier = Modifier.weight(1f).testTag("calculus-upper"))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { focus.clearFocus(); viewModel.calculate() }, enabled = !state.busy && state.function.isNotBlank()) {
                Text(if (state.busy) "Calculating…" else "Calculate")
            }
            if (state.busy) TextButton(onClick = viewModel::cancel) { Text("Cancel") }
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("calculus-error")) }
        state.result?.let { result ->
            val formatted = Formatter().format(result.value)
            Text("≈ $formatted", style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.testTag("calculus-result"), overflow = TextOverflow.Ellipsis)
            Text("Estimated stability: ${Formatter().format(result.estimatedError)} · ${result.evaluations} evaluations",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onUseResult(result.value) }) { Text("Use result") }
                TextButton(onClick = {
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                        .setPrimaryClip(ClipData.newPlainText("Calculus result", formatted))
                }) { Text("Copy") }
                TextButton(onClick = {
                    val description = if (state.mode == CalculusMode.DERIVATIVE) {
                        "d/dx(${state.function}) at x=${state.point}"
                    } else "Integral of ${state.function} from ${state.lower} to ${state.upper}"
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "$description ≈ $formatted (${state.angleUnit.name.lowercase()})")
                    }, "Share result"))
                }) { Text("Share") }
            }
        }
        HorizontalDivider()
        Text("About these estimates", style = MaterialTheme.typography.titleMedium)
        Text("Numerical methods assume the function is sufficiently smooth between sampled points. They do not give symbolic formulas or prove continuity. Sharp peaks, oscillations, singularities, and finite precision can limit accuracy; a convergence estimate is not a guaranteed error bound.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
