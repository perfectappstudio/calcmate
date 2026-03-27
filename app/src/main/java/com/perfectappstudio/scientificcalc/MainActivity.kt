package com.perfectappstudio.scientificcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.perfectappstudio.scientificcalc.ads.AdManager
import com.perfectappstudio.scientificcalc.ui.navigation.AppNavigation
import com.perfectappstudio.scientificcalc.ui.theme.CalcMateTheme

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
