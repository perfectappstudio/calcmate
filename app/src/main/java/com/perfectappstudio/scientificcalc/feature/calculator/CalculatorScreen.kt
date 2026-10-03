package com.perfectappstudio.scientificcalc.feature.calculator

import android.content.res.Configuration
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.perfectappstudio.scientificcalc.ui.components.ConstantsSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch
import com.perfectappstudio.scientificcalc.BuildConfig
import com.perfectappstudio.scientificcalc.ads.AdManager
import com.perfectappstudio.scientificcalc.core.data.PreferencesManager
import com.perfectappstudio.scientificcalc.ui.components.SettingsDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perfectappstudio.scientificcalc.core.model.CalculatorAction
import com.perfectappstudio.scientificcalc.core.model.CalculatorState

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    onOpenTool: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val preferences = remember { PreferencesManager(context.applicationContext) }
    val hapticEnabled by preferences.hapticEnabled.collectAsStateWithLifecycle(initialValue = true)
    val scope = rememberCoroutineScope()
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showPrivacy by rememberSaveable { mutableStateOf(false) }
    var showTools by remember { mutableStateOf(false) }
    var showConstants by rememberSaveable { mutableStateOf(false) }
    var showExamples by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("CalcMate", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = { viewModel.onAction(CalculatorAction.ToggleScientific) }) {
                Text(if (state.isScientificExpanded) "Scientific" else "Basic")
            }
            IconButton(onClick = { viewModel.onAction(CalculatorAction.ShowHistory) }) {
                Icon(Icons.Default.History, contentDescription = "History")
            }
            IconButton(onClick = { showSettings = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${state.angleUnit.name.lowercase().replaceFirstChar(Char::uppercase)} · ${if (state.displaySettings.showFractions) "Fraction" else state.displaySettings.mode.name.replace('_', ' ')}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            TextButton(
                onClick = { viewModel.onAction(CalculatorAction.ToggleFractionDisplay) },
                modifier = Modifier.semantics { contentDescription = "Decimal or fraction" },
            ) { Text("S↔D") }
            Box {
                IconButton(onClick = { showTools = true }) {
                    Icon(Icons.Default.Apps, contentDescription = "More tools")
                }
                DropdownMenu(expanded = showTools, onDismissRequest = { showTools = false }) {
                    listOf("calculus" to "Calculus", "matrix" to "Matrix", "vector" to "Vector", "basen" to "Base-N").forEach { (route, label) ->
                        DropdownMenuItem(text = { Text(label) }, onClick = {
                            showTools = false
                            onOpenTool(route)
                        })
                    }
                    DropdownMenuItem(text = { Text("Constants") }, onClick = {
                        showTools = false
                        showConstants = true
                    })
                    DropdownMenuItem(text = { Text("Examples & help") }, onClick = {
                        showTools = false
                        showExamples = true
                    })
                }
            }
            IconButton(
                enabled = state.result.isNotBlank() && state.error == null,
                onClick = { clipboard.setText(AnnotatedString(state.result)) },
            ) { Icon(Icons.Default.ContentCopy, contentDescription = "Copy result") }
            IconButton(
                enabled = state.result.isNotBlank() && state.error == null,
                onClick = {
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${state.expression} = ${state.result}")
                    }, "Share calculation"))
                },
            ) { Icon(Icons.Default.Share, contentDescription = "Share calculation") }
        }
        Box(modifier = Modifier.weight(1f)) {
            if (isLandscape) {
                LandscapeLayout(
                    state = state,
                    onAction = viewModel::onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                PortraitLayout(
                    state = state,
                    onAction = viewModel::onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    if (showExamples) {
        AlertDialog(
            onDismissRequest = { showExamples = false },
            title = { Text("Examples & help") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Choose an example to replace the current expression, then press =. Your saved history stays available.")
                    listOf(
                        "Fractions: 1/3 + 1/6" to "1/3+1/6",
                        "Powers: 2³ + √(16)" to "2^3+sqrt(16)",
                        "Natural logarithm: ln(e)" to "ln(e)",
                        "Combinations: nCr(5, 2)" to "nCr(5,2)",
                    ).forEach { (label, expression) ->
                        TextButton(onClick = {
                            viewModel.onAction(CalculatorAction.Clear)
                            viewModel.onAction(CalculatorAction.Constant(expression))
                            showExamples = false
                        }) { Text(label) }
                    }
                    Text("Use S↔D for decimal or fraction results. Set Deg, Rad, or Grad before trigonometry. Graph, Solver, Converter, and Stats are in the bottom navigation. Calculus, Matrix, Vector, Base-N, and Constants are in More tools.")
                    Text("Calculations use floating-point precision. Calculus is numerical; fraction approximations are marked ≈.", modifier = Modifier.padding(top = 12.dp))
                }
            },
            confirmButton = { TextButton(onClick = { showExamples = false }) { Text("Done") } },
        )
    }
    if (showConstants) {
        ConstantsSheet(onDismiss = { showConstants = false }, onConstantSelected = {
            viewModel.onAction(CalculatorAction.Constant(it.value.toString()))
            showConstants = false
        })
    }
    if (showSettings) {
        SettingsDialog(
            currentDisplaySettings = state.displaySettings,
            onDisplaySettingsChange = viewModel::setDisplaySettings,
            currentAngleUnit = state.angleUnit,
            onAngleUnitChange = viewModel::setAngleUnit,
            hapticFeedbackEnabled = hapticEnabled,
            onHapticFeedbackChange = { enabled -> scope.launch { preferences.setHapticEnabled(enabled) } },
            onPrivacyPolicyClick = { showPrivacy = true },
            onTermsClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://perfectappstudio.com/calcmate/terms"))) },
            privacyOptionsRequired = AdManager.privacyOptionsRequired,
            onPrivacyOptionsClick = { (context as? Activity)?.let(AdManager::showPrivacyOptions) },
            appVersion = BuildConfig.VERSION_NAME,
            onDismiss = { showSettings = false },
        )
    }
    if (showPrivacy) {
        AlertDialog(
            onDismissRequest = { showPrivacy = false },
            title = { Text("Privacy policy") },
            text = {
                Text(
                    context.assets.open("privacy-policy.txt").bufferedReader().use { it.readText() },
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                )
            },
            confirmButton = { TextButton(onClick = { showPrivacy = false }) { Text("Done") } },
            dismissButton = {
                TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://perfectappstudio.com/calcmate/privacy"))) }) { Text("Online policy") }
            },
        )
    }

    // History bottom sheet
    if (state.showHistory) {
        HistorySheet(
            entries = state.historyEntries,
            onDismiss = { viewModel.onAction(CalculatorAction.HideHistory) },
            onEntryClick = { entry ->
                viewModel.onAction(CalculatorAction.ReuseHistoryEntry(entry))
            },
            onDeleteEntry = { entry ->
                viewModel.onAction(CalculatorAction.DeleteHistoryEntry(entry))
            },
            onClearAll = { viewModel.onAction(CalculatorAction.ClearHistory) },
        )
    }
}

@Composable
private fun PortraitLayout(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
      val compact = maxHeight < 600.dp
      val scrollAllKeys = maxHeight < 430.dp || LocalDensity.current.fontScale > 1.4f
      Column(modifier = Modifier.fillMaxSize()) {
        ExpressionDisplay(
            expression = state.expression,
            result = state.result,
            error = state.error,
            hasEvaluated = state.hasEvaluated,
            compact = compact,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (compact) Modifier.height(104.dp) else Modifier.weight(1f)),
        )
        ScientificKeypad(
            state = state,
            onAction = onAction,
            compact = compact && !scrollAllKeys,
            modifier = Modifier.fillMaxWidth()
                .then(if (compact) Modifier.weight(1f) else Modifier)
                .then(if (compact && scrollAllKeys) Modifier.verticalScroll(rememberScrollState()) else Modifier),
        )
      }
    }
}

@Composable
private fun LandscapeLayout(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ExpressionDisplay(
            expression = state.expression,
            result = state.result,
            error = state.error,
            hasEvaluated = state.hasEvaluated,
            compact = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp),
        )
        ScientificKeypadLand(
            state = state,
            onAction = onAction,
            modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
        )
    }
}
