package com.calcmate.scientificcalculator.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val navDestinations = listOf(
    NavDestination("calculator", "Calculator", Icons.Outlined.Calculate),
    NavDestination("graph", "Graph", Icons.AutoMirrored.Outlined.ShowChart),
    NavDestination("solver", "Solver", Icons.Outlined.Functions),
    NavDestination("converter", "Converter", Icons.Outlined.SwapHoriz),
)

@Composable
fun CalcMateNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    NavigationBar {
        navDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = "${destination.label} tab",
                    )
                },
                label = { Text(text = destination.label) },
            )
        }
    }
}
