package com.perfectappstudio.scientificcalc

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoreScreenshotsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun captureReleaseFlowsAtStoreSize() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        val directory = instrumentation.targetContext.getExternalFilesDir(null)!!
        fun capture(name: String) {
            composeRule.waitForIdle()
            device.waitForIdle()
            assertTrue(device.takeScreenshot(File(directory, "store-$name.png")))
        }
        device.executeShellCommand("wm size 1080x1920")
        try {
            listOf("C", "7", "×", "8", "=").forEach {
                composeRule.onAllNodes(hasContentDescription(it))[0].performClick()
            }
            composeRule.onNode(hasContentDescription("Result: 56", substring = true)).assertIsDisplayed()
            composeRule.onNodeWithContentDescription("=").assertIsDisplayed()
            capture("calculator")
            composeRule.onNodeWithContentDescription("Graph tab").performClick()
            composeRule.onNodeWithContentDescription("Reset zoom").assertExists()
            composeRule.onNodeWithText("+ Add function").performClick()
            composeRule.onNodeWithText("e.g. sin(x)").performTextInput("sin(x)")
            composeRule.runOnUiThread {
                WindowCompat.getInsetsController(composeRule.activity.window, composeRule.activity.window.decorView)
                    .hide(WindowInsetsCompat.Type.ime())
            }
            capture("graph")
            composeRule.onNodeWithContentDescription("Converter tab").performClick()
            composeRule.onNodeWithText("From").assertExists()
            capture("converter")
            composeRule.onNodeWithContentDescription("Calculator tab").performClick()
            composeRule.onNodeWithContentDescription("More tools").performClick()
            composeRule.onNodeWithText("Calculus").performClick()
            composeRule.onNodeWithText("Calculate").performScrollTo().performClick()
            composeRule.waitForIdle()
            composeRule.waitUntil(10000) {
                composeRule.onAllNodes(hasTestTag("calculus-result")).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithTag("calculus-result").assertTextEquals("≈ 4")
            composeRule.onNodeWithTag("calculus-result").performScrollTo()
            capture("calculus")
        } finally {
            device.executeShellCommand("wm size reset")
        }
    }
}
