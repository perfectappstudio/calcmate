package com.calcmate.scientificcalculator.feature.converter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.calcmate.scientificcalculator.core.data.UnitDef

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitPicker(
    units: List<UnitDef>,
    selected: UnitDef,
    onSelect: (UnitDef) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var filterText by remember(selected) { mutableStateOf("") }

    val filteredUnits = if (filterText.isBlank()) {
        units
    } else {
        units.filter { unit ->
            unit.name.contains(filterText, ignoreCase = true) ||
                unit.symbol.contains(filterText, ignoreCase = true)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = if (expanded) filterText else "${selected.name} (${selected.symbol})",
            onValueChange = { filterText = it },
            readOnly = !expanded,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = if (expanded) {
                { Text("Search units...") }
            } else {
                null
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            singleLine = true,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                filterText = ""
            },
        ) {
            filteredUnits.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${unit.name} (${unit.symbol})",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                        filterText = ""
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
