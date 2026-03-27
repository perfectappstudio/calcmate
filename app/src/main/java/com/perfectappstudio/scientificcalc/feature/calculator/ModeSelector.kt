package com.perfectappstudio.scientificcalc.feature.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.perfectappstudio.scientificcalc.ui.theme.*

enum class CalculatorMode(val label: String) {
    COMP("COMP"),
    CMPLX("CMPLX"),
    SD("SD"),
    REG("REG"),
    BASE("BASE"),
    MAT("MAT"),
    VCT("VCT"),
}

private fun CalculatorMode.accentColor(): Color = when (this) {
    CalculatorMode.COMP -> PurpleAccent
    CalculatorMode.CMPLX -> PurpleAccent
    CalculatorMode.SD -> PinkAccent
    CalculatorMode.REG -> PinkAccent
    CalculatorMode.BASE -> MintGreen
    CalculatorMode.MAT -> AmberAccent
    CalculatorMode.VCT -> AmberAccent
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
            val accent = mode.accentColor()
            FilterChip(
                selected = selected,
                onClick = { onModeSelect(mode) },
                label = {
                    Text(
                        text = mode.label,
                        fontSize = 11.sp,
                        color = if (selected) Color.Black else Color.White,
                    )
                },
                shape = RoundedCornerShape(20.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = GlassBorder,
                    selectedBorderColor = Color.Transparent,
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = GlassLight,
                    selectedContainerColor = accent,
                    labelColor = Color.White,
                    selectedLabelColor = Color.Black,
                ),
            )
        }
    }
}
