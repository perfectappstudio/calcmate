package com.perfectappstudio.scientificcalc.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.perfectappstudio.scientificcalc.feature.calculator.CalculatorScreen
import com.perfectappstudio.scientificcalc.feature.converter.ConverterScreen
import com.perfectappstudio.scientificcalc.feature.graphing.GraphScreen
import com.perfectappstudio.scientificcalc.feature.solver.SolverScreen
import com.perfectappstudio.scientificcalc.feature.statistics.StatisticsScreen
import com.perfectappstudio.scientificcalc.ui.components.CalcMateNavigationBar
import com.perfectappstudio.scientificcalc.ui.components.deepSpaceBackground
import com.perfectappstudio.scientificcalc.ui.components.navDestinations
import com.perfectappstudio.scientificcalc.ui.theme.DeepSpaceBase

private fun routeIndex(route: String): Int =
    navDestinations.indexOfFirst { it.route == route }.coerceAtLeast(0)

@Composable
fun AppNavigation() {
    var currentRoute by rememberSaveable { mutableStateOf("calculator") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .deepSpaceBackground(),
    ) {
        Scaffold(
            modifier = Modifier.weight(1f),
            containerColor = DeepSpaceBase.copy(alpha = 0f), // transparent so gradient shows through
            bottomBar = {
                CalcMateNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route -> currentRoute = route },
                )
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentRoute,
                label = "screen_transition",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                transitionSpec = {
                    val direction = if (routeIndex(targetState) > routeIndex(initialState)) 1 else -1
                    val slideAmount = 30 // subtle offset in px

                    (fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { direction * slideAmount },
                        )) togetherWith
                        (fadeOut(animationSpec = tween(300)) +
                            slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { -direction * slideAmount },
                            ))
                },
            ) { route ->
                when (route) {
                    "calculator" -> CalculatorScreen()
                    "graph" -> GraphScreen()
                    "solver" -> SolverScreen()
                    "converter" -> ConverterScreen()
                    "statistics" -> StatisticsScreen()
                }
            }
        }
    }
}
