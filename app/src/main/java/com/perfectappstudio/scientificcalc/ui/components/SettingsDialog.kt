package com.perfectappstudio.scientificcalc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.model.DisplayMode
import com.perfectappstudio.scientificcalc.core.model.DisplaySettings
import com.perfectappstudio.scientificcalc.core.model.FractionFormat

// ──────────────────────────────────────────────────
// Option enums
// ──────────────────────────────────────────────────

enum class ThemeOption(val label: String) {
    System("System"),
    Light("Light"),
    Dark("Dark"),
}

enum class AngleUnit(val label: String) {
    Degrees("Degrees"),
    Radians("Radians"),
}

// ──────────────────────────────────────────────────
// Dialog
// ──────────────────────────────────────────────────

@Composable
fun SettingsDialog(
    currentTheme: ThemeOption,
    onThemeChange: (ThemeOption) -> Unit,
    currentDisplaySettings: DisplaySettings,
    onDisplaySettingsChange: (DisplaySettings) -> Unit,
    currentAngleUnit: AngleUnit,
    onAngleUnitChange: (AngleUnit) -> Unit,
    hapticFeedbackEnabled: Boolean,
    onHapticFeedbackChange: (Boolean) -> Unit,
    isPremium: Boolean,
    onRemoveAdsClick: () -> Unit,
    appVersion: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        title = {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // ── Theme ──
                SectionHeader("Theme")
                RadioGroup(
                    options = ThemeOption.entries,
                    selected = currentTheme,
                    labelOf = { it.label },
                    onSelected = onThemeChange,
                )

                SectionDivider()

                // ── Display Mode ──
                SectionHeader("Display Mode")
                DisplayModeSection(
                    settings = currentDisplaySettings,
                    onSettingsChange = onDisplaySettingsChange,
                )

                SectionDivider()

                // ── Engineering ──
                SectionHeader("Engineering")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDisplaySettingsChange(
                                currentDisplaySettings.copy(
                                    engineeringOn = !currentDisplaySettings.engineeringOn,
                                ),
                            )
                        }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (currentDisplaySettings.engineeringOn) "Eng ON" else "Eng OFF",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Switch(
                        checked = currentDisplaySettings.engineeringOn,
                        onCheckedChange = { enabled ->
                            onDisplaySettingsChange(
                                currentDisplaySettings.copy(engineeringOn = enabled),
                            )
                        },
                    )
                }

                SectionDivider()

                // ── Fraction Format ──
                SectionHeader("Fraction Format")
                RadioGroup(
                    options = FractionFormat.entries,
                    selected = currentDisplaySettings.fractionFormat,
                    labelOf = { format ->
                        when (format) {
                            FractionFormat.MIXED -> "Mixed  ab/c"
                            FractionFormat.IMPROPER -> "Improper  d/c"
                        }
                    },
                    onSelected = { format ->
                        onDisplaySettingsChange(
                            currentDisplaySettings.copy(fractionFormat = format),
                        )
                    },
                )

                SectionDivider()

                // ── Angle unit ──
                SectionHeader("Angle Unit")
                RadioGroup(
                    options = AngleUnit.entries,
                    selected = currentAngleUnit,
                    labelOf = { it.label },
                    onSelected = onAngleUnitChange,
                )

                SectionDivider()

                // ── Haptic feedback ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHapticFeedbackChange(!hapticFeedbackEnabled) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Haptic Feedback",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Switch(
                        checked = hapticFeedbackEnabled,
                        onCheckedChange = onHapticFeedbackChange,
                    )
                }

                SectionDivider()

                // ── Remove Ads ──
                if (!isPremium) {
                    Button(
                        onClick = onRemoveAdsClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Remove Ads - \$2.99")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Text(
                        text = "Premium -- ad-free experience",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                SectionDivider()

                // ── About ──
                SectionHeader("About")
                Text(
                    text = "CalcMate v$appVersion",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "A modern scientific calculator.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

// ──────────────────────────────────────────────────
// Display Mode section with digit picker
// ──────────────────────────────────────────────────

@Composable
private fun DisplayModeSection(
    settings: DisplaySettings,
    onSettingsChange: (DisplaySettings) -> Unit,
) {
    Column(Modifier.selectableGroup()) {
        DisplayMode.entries.forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = mode == settings.mode,
                        onClick = {
                            val newDigits = when (mode) {
                                DisplayMode.FIX -> settings.digits.coerceIn(0, 9)
                                DisplayMode.SCI -> settings.digits.coerceIn(1, 10)
                                else -> settings.digits
                            }
                            onSettingsChange(settings.copy(mode = mode, digits = newDigits))
                        },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = mode == settings.mode,
                    onClick = null,
                )
                Text(
                    text = when (mode) {
                        DisplayMode.FIX -> "Fix"
                        DisplayMode.SCI -> "Sci"
                        DisplayMode.NORM_1 -> "Norm 1"
                        DisplayMode.NORM_2 -> "Norm 2"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }

    // Digit picker for Fix and Sci modes
    if (settings.mode == DisplayMode.FIX || settings.mode == DisplayMode.SCI) {
        val range = if (settings.mode == DisplayMode.FIX) 0..9 else 1..10
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Digits: ${settings.digits}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Slider(
                value = settings.digits.toFloat(),
                onValueChange = { value ->
                    onSettingsChange(settings.copy(digits = value.toInt()))
                },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                steps = range.last - range.first - 1,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ──────────────────────────────────────────────────
// Reusable private composables
// ──────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
    )
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun <T> RadioGroup(
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelected: (T) -> Unit,
) {
    Column(Modifier.selectableGroup()) {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = option == selected,
                        onClick = { onSelected(option) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = option == selected,
                    onClick = null, // handled by Row selectable
                )
                Text(
                    text = labelOf(option),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}
