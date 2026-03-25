package com.calcmate.scientificcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.calcmate.scientificcalculator.ads.AdManager
import com.calcmate.scientificcalculator.ui.navigation.AppNavigation
import com.calcmate.scientificcalculator.ui.theme.CalcMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize ad manager and preload interstitial / rewarded ads.
        AdManager.initialize(this)

        setContent {
            CalcMateTheme {
                AppNavigation()
            }
        }
    }
}
