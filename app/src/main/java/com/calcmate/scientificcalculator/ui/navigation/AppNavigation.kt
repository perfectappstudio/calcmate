package com.calcmate.scientificcalculator.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.calcmate.scientificcalculator.feature.calculator.CalculatorScreen
import com.calcmate.scientificcalculator.feature.converter.ConverterScreen
import com.calcmate.scientificcalculator.feature.graphing.GraphScreen
import com.calcmate.scientificcalculator.feature.solver.SolverScreen
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
                "calculator" -> CalculatorScreen()
                "graph" -> GraphScreen()
                "solver" -> SolverScreen()
                "converter" -> ConverterScreen()
            }
        }
    }
}
