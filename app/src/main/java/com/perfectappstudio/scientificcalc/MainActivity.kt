package com.perfectappstudio.scientificcalc

import android.os.Bundle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.perfectappstudio.scientificcalc.core.data.PreferencesManager
import com.perfectappstudio.scientificcalc.ui.components.LocalHapticEnabled
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import android.graphics.Color
import com.perfectappstudio.scientificcalc.ads.AdManager
import com.perfectappstudio.scientificcalc.ui.navigation.AppNavigation
import com.perfectappstudio.scientificcalc.ui.theme.CalcMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        AdManager.initialize(this)

        setContent {
            val preferences = remember { PreferencesManager(applicationContext) }
            val hapticEnabled by preferences.hapticEnabled.collectAsStateWithLifecycle(initialValue = true)
            CalcMateTheme {
                CompositionLocalProvider(LocalHapticEnabled provides hapticEnabled) {
                    AppNavigation()
                }
            }
        }
    }
}
