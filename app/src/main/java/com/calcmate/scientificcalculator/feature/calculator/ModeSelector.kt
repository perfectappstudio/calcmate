package com.calcmate.scientificcalculator.feature.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CalculatorMode(val label: String) {
    COMP("COMP"),
    CMPLX("CMPLX"),
    SD("SD"),
    REG("REG"),
    BASE("BASE"),
    MAT("MAT"),
    VCT("VCT"),
}

@Composable
fun ModeSelector(
    selectedMode: CalculatorMode,
    onModeSelect: (CalculatorMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        items(CalculatorMode.entries.toList()) { mode ->
            val selected = mode == selectedMode
            FilterChip(
                selected = selected,
                onClick = { onModeSelect(mode) },
                label = {
                    Text(
                        text = mode.label,
                        fontSize = 11.sp,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}
