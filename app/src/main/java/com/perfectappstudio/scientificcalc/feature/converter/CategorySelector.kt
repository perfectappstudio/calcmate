package com.perfectappstudio.scientificcalc.feature.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.data.UnitCategory
import com.perfectappstudio.scientificcalc.ui.theme.*

@Composable
fun CategorySelector(
    selected: UnitCategory,
    onSelect: (UnitCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(UnitCategory.entries) { category ->
            val isSelected = category == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(category) },
                label = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) Color.Black else Color.White,
                    )
                },
                shape = RoundedCornerShape(20.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = GlassBorder,
                    selectedBorderColor = Color.Transparent,
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = GlassLight,
                    selectedContainerColor = CyanAccent,
                    labelColor = Color.White,
                    selectedLabelColor = Color.Black,
                ),
            )
        }
    }
}
