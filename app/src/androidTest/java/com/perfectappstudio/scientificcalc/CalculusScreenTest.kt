package com.perfectappstudio.scientificcalc

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculusScreenTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    private fun openCalculus() {
        composeRule.onNodeWithContentDescription("More tools").performClick()
        composeRule.onNodeWithText("Calculus", useUnmergedTree = true).performClick()
    }

    private fun calculate() {
        composeRule.onNodeWithText("Calculate", useUnmergedTree = true).performScrollTo().performClick()
        composeRule.waitUntil(15000) { composeRule.onAllNodesWithTag("calculus-result").fetchSemanticsNodes().isNotEmpty() ||
            composeRule.onAllNodesWithTag("calculus-error").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun derivativeCanBeUsedInTheMainCalculator() {
        openCalculus()
        calculate()
        composeRule.onNodeWithTag("calculus-result").assertTextEquals("≈ 4")
        composeRule.onNodeWithText("Use result").performScrollTo().performClick()
        composeRule.onNodeWithContentDescription("×").performClick()
        composeRule.onNodeWithContentDescription("3").performClick()
        composeRule.onNodeWithContentDescription("=").performClick()
        composeRule.onNode(hasContentDescription("Result: 12", substring = true)).assertExists()
    }

    @Test fun integralAcceptsPiAndShowsARealResult() {
        openCalculus()
        composeRule.onNodeWithText("Integral", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("calculus-function").performTextReplacement("sin(x)")
        composeRule.onNodeWithTag("calculus-upper").performTextReplacement("pi")
        calculate()
        composeRule.onNodeWithTag("calculus-result").assertTextEquals("≈ 2")
        composeRule.onNodeWithTag("calculus-result").performScrollTo()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        UiDevice.getInstance(instrumentation).takeScreenshot(File(instrumentation.targetContext.getExternalFilesDir(null), "calculus-integral.png"))
    }

    @Test fun invalidDerivativeCanBeCorrectedWithoutStaleOutput() {
        openCalculus()
        composeRule.onNodeWithTag("calculus-function").performTextReplacement("abs(x)")
        composeRule.onNodeWithTag("calculus-point").performTextReplacement("0")
        calculate()
        composeRule.onNodeWithTag("calculus-error").assertTextContains("did not converge", substring = true)
        composeRule.onNodeWithTag("calculus-result").assertDoesNotExist()
        composeRule.onNodeWithTag("calculus-function").performTextReplacement("x^2")
        composeRule.onNodeWithTag("calculus-point").performTextReplacement("3")
        calculate()
        composeRule.onNodeWithTag("calculus-result").assertTextEquals("≈ 6")
        composeRule.onNodeWithTag("calculus-error").assertDoesNotExist()
    }

    @Test fun constantsCanBeSearchedAndInsertedAtStoredPrecision() {
        composeRule.onNodeWithContentDescription("More tools").performClick()
        composeRule.onNodeWithText("Constants").performClick()
        composeRule.onNodeWithText("Search by name or symbol").performTextInput("Speed of Light")
        composeRule.onNode(hasText("Speed of Light") and !hasSetTextAction()).performScrollTo().performClick()
        composeRule.onNode(hasContentDescription("Result: 299792458", substring = true)).assertExists()
    }
}
