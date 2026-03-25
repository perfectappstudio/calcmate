package com.calcmate.scientificcalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

// ──────────────────────────────────────────────────
// Option enums
// ──────────────────────────────────────────────────

enum class ThemeOption(val label: String) {
    System("System"),
    Light("Light"),
    Dark("Dark"),
}

enum class DisplayFormat(val label: String) {
    Decimal("Decimal"),
    Fraction("Fraction"),
    Scientific("Scientific"),
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
    currentDisplayFormat: DisplayFormat,
    onDisplayFormatChange: (DisplayFormat) -> Unit,
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

                // ── Display format ──
                SectionHeader("Display Format")
                RadioGroup(
                    options = DisplayFormat.entries,
                    selected = currentDisplayFormat,
                    labelOf = { it.label },
                    onSelected = onDisplayFormatChange,
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
