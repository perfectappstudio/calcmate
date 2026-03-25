package com.calcmate.scientificcalculator.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.calcmate.scientificcalculator.ui.components.CalcMateNavigationBar

@Composable
fun AppNavigation() {
    var currentRoute by rememberSaveable { mutableStateOf("calculator") }

    Scaffold(
        bottomBar = {
            CalcMateNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route },
            )
        },
    ) { innerPadding ->
        Crossfade(
            targetState = currentRoute,
            label = "screen_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) { route ->
            when (route) {
                "calculator" -> PlaceholderScreen("Calculator")
                "graph" -> PlaceholderScreen("Graph")
                "solver" -> PlaceholderScreen("Solver")
                "converter" -> PlaceholderScreen("Converter")
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
