package com.perfectappstudio.scientificcalc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.StackedBarChart
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.ui.theme.AmberAccent
import com.perfectappstudio.scientificcalc.ui.theme.CyanAccent
import com.perfectappstudio.scientificcalc.ui.theme.MintGreen
import com.perfectappstudio.scientificcalc.ui.theme.PinkAccent
import com.perfectappstudio.scientificcalc.ui.theme.PurpleAccent
import com.perfectappstudio.scientificcalc.ui.theme.TextDim
import com.perfectappstudio.scientificcalc.ui.theme.TextPrimary

data class NavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val accentColor: Color,
)

val navDestinations = listOf(
    NavDestination("calculator", "Calculator", Icons.Outlined.Calculate, PurpleAccent),
    NavDestination("graph", "Graph", Icons.AutoMirrored.Outlined.ShowChart, MintGreen),
    NavDestination("solver", "Solver", Icons.Outlined.Functions, AmberAccent),
    NavDestination("converter", "Converter", Icons.Outlined.SwapHoriz, CyanAccent),
    NavDestination("statistics", "Stats", Icons.Outlined.StackedBarChart, PinkAccent),
)

@Composable
fun CalcMateNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            )
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navDestinations.forEach { destination ->
            val isSelected = currentRoute == destination.route
            val iconColor = if (isSelected) destination.accentColor else TextDim
            val interactionSource = remember { MutableInteractionSource() }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { onNavigate(destination.route) }
                    .padding(vertical = 6.dp)
                    .semantics { contentDescription = "${destination.label} tab" },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor,
                )

                // Selected underline glow
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(destination.accentColor),
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Show label only when selected
                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}
