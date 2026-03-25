package com.calcmate.scientificcalculator.feature.graphing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.calcmate.scientificcalculator.core.model.GraphFunction

/**
 * Function input controls: a scrollable list of expression entries with
 * colored indicators and an "Add function" button at the bottom.
 */
@Composable
fun GraphControls(
    functions: List<GraphFunction>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    onUpdate: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(functions, key = { it.id }) { func ->
            FunctionEntry(
                function = func,
                onExpressionChange = { expr -> onUpdate(func.id, expr) },
                onDelete = { onRemove(func.id) },
            )
        }

        // "+ Add function" button (limit to 8)
        if (functions.size < 8) {
            item {
                TextButton(
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("+ Add function")
                }
            }
        }
    }
}

@Composable
private fun FunctionEntry(
    function: GraphFunction,
    onExpressionChange: (String) -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Colored circle indicator
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(function.color)),
        )

        // Expression text field
        OutlinedTextField(
            value = function.expression,
            onValueChange = onExpressionChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("e.g. sin(x)") },
            singleLine = true,
            isError = !function.isValid,
            textStyle = MaterialTheme.typography.bodyMedium,
        )

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove function",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
