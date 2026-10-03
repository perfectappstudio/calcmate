package com.perfectappstudio.scientificcalc

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflinePersistenceTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun calculationAndSavedHistorySurviveActivityRestartWithoutNetwork() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val wifi = device.executeShellCommand("settings get global wifi_on").trim() == "1"
        val mobile = device.executeShellCommand("settings get global mobile_data").trim() == "1"
        device.executeShellCommand("svc wifi disable")
        device.executeShellCommand("svc data disable")
        try {
            val connectivity = composeRule.activity.getSystemService(ConnectivityManager::class.java)
            composeRule.waitUntil(10000) {
                connectivity.getNetworkCapabilities(connectivity.activeNetwork)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) != true
            }
            val number = (System.currentTimeMillis() % 100000 + 10000).toString()
            val expression = "${number}×3"
            (listOf("C") + number.map(Char::toString) + listOf("×", "3", "=")).forEach {
                composeRule.onAllNodes(hasContentDescription(it))[0].performClick()
            }
            composeRule.onNode(hasContentDescription("Result: ${number.toInt() * 3}", substring = true)).assertExists()
            composeRule.onNodeWithContentDescription("History").performClick()
            composeRule.waitUntil(10000) { composeRule.onAllNodesWithText(expression).fetchSemanticsNodes().isNotEmpty() }
            composeRule.activityRule.scenario.recreate()
            composeRule.onNodeWithContentDescription("History").performClick()
            composeRule.waitUntil(10000) { composeRule.onAllNodesWithText(expression).fetchSemanticsNodes().isNotEmpty() }
        } finally {
            if (wifi) device.executeShellCommand("svc wifi enable")
            if (mobile) device.executeShellCommand("svc data enable")
        }
    }
}
