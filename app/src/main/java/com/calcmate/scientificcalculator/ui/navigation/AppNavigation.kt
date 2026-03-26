package com.calcmate.scientificcalculator.ui.navigation

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
import com.calcmate.scientificcalculator.feature.calculator.CalculatorScreen
import com.calcmate.scientificcalculator.feature.converter.ConverterScreen
import com.calcmate.scientificcalculator.feature.graphing.GraphScreen
import com.calcmate.scientificcalculator.feature.solver.SolverScreen
import com.calcmate.scientificcalculator.feature.statistics.StatisticsScreen
import com.calcmate.scientificcalculator.ads.BannerAdComposable
import com.calcmate.scientificcalculator.ui.components.CalcMateNavigationBar
import com.calcmate.scientificcalculator.ui.components.navDestinations

private fun routeIndex(route: String): Int =
    navDestinations.indexOfFirst { it.route == route }.coerceAtLeast(0)

@Composable
fun AppNavigation() {
    var currentRoute by rememberSaveable { mutableStateOf("calculator") }

    // Column wraps Scaffold + BannerAd so the banner lives BELOW
    // the Scaffold and never overlaps buttons or navigation (R24).
    Column(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.weight(1f),
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

        // Banner ad anchored below the Scaffold (R24, R32).
        BannerAdComposable()
    }
}
