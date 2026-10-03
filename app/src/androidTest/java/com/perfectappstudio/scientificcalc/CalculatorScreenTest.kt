package com.perfectappstudio.scientificcalc

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for the Calculator screen.
 *
 * CalcButton exposes its [text] parameter as both visible text and
 * [contentDescription] (via semantics). ExpressionDisplay merges its
 * descendants and sets a combined content description of the form:
 *   "Expression: <expr>. Result: <result>"
 *   "Empty expression. No result"
 */
@RunWith(AndroidJUnit4::class)
class CalculatorScreenTest {

    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val failureArtifacts = object : TestWatcher() {
        override fun failed(e: Throwable, description: Description) {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val device = UiDevice.getInstance(instrumentation)
            val directory = instrumentation.targetContext.getExternalFilesDir(null)
            device.takeScreenshot(File(directory, "failed-${description.methodName}.png"))
            device.dumpWindowHierarchy(File(directory, "failed-${description.methodName}.xml"))
        }
    }

    @Before
    fun clearInput() {
        tapButtons("C")
    }

    @Test
    fun exampleReplacesInputAndCanBeCalculated() {
        tapButtons("9")
        composeRule.onNodeWithContentDescription("More tools").performClick()
        composeRule.onNodeWithText("Examples & help").performClick()
        composeRule.onNodeWithText("Choose an example to replace the current expression, then press =. Your saved history stays available.").assertExists()
        composeRule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        device.takeScreenshot(File(instrumentation.targetContext.getExternalFilesDir(null), "release-help.png"))
        composeRule.onNodeWithText("Powers: 2³ + √(16)").performClick()
        assertExpressionContains("2^3+sqrt(16)")
        tapButtons("=")
        assertResultContains("12")
        device.takeScreenshot(File(instrumentation.targetContext.getExternalFilesDir(null), "release-calculator.png"))
    }

    // -- Helpers ----------------------------------------------------------

    /** Unicode symbols used by the keypad buttons. */
    private val multiply = "\u00D7"  // multiplication sign
    private val minus = "\u2212"     // minus sign
    private val divide = "\u00F7"    // division sign
    private val backspace = "\u232B" // erase-to-left symbol

    /** Tap a sequence of buttons identified by their contentDescription. Uses first match. */
    private fun tapButtons(vararg labels: String) {
        labels.forEach { label ->
            composeRule.onAllNodes(hasContentDescription(label))[0].performClick()
            composeRule.waitForIdle()
        }
    }

    /**
     * Assert that the merged ExpressionDisplay content description
     * contains the expected result string.
     */
    private fun assertResultContains(expected: String) {
        composeRule.waitForIdle()
        composeRule
            .onNode(hasContentDescription("Result: $expected", substring = true))
            .assertExists()
    }

    /**
     * Assert that the merged ExpressionDisplay content description
     * contains the expected expression string.
     */
    private fun assertExpressionContains(expected: String) {
        composeRule.waitForIdle()
        composeRule
            .onNode(hasContentDescription("Expression: $expected", substring = true))
            .assertExists()
    }

    /**
     * Assert that the display shows an empty expression.
     */
    private fun assertExpressionEmpty() {
        composeRule.waitForIdle()
        composeRule
            .onNode(hasContentDescription("Empty expression", substring = true))
            .assertExists()
    }

    // -- Tests ------------------------------------------------------------

    @Test
    fun basicAddition_2Plus3Equals5() {
        tapButtons("2", "+", "3", "=")
        assertResultContains("5")
    }

    @Test
    fun scientificFunction_sin90InDegEquals1() {
        // Default mode is DEG. The scientific panel is expanded by default.
        tapButtons("sin", "9", "0", ")", "=")
        assertResultContains("1")
    }

    @Test
    fun clear_removesExpression() {
        tapButtons("1", "2", "3")
        assertExpressionContains("123")

        tapButtons("C")
        assertExpressionEmpty()
    }

    @Test
    fun backspace_removesLastCharacter() {
        tapButtons("1", "2", "3")
        assertExpressionContains("123")

        tapButtons(backspace)
        assertExpressionContains("12")
    }

    @Test
    fun operatorPrecedence_2Plus3Times4Equals14() {
        tapButtons("2", "+", "3", multiply, "4", "=")
        assertResultContains("14")
    }

    @Test
    fun parentheses_groupingWorks() {
        tapButtons("(", "2", "+", "3", ")", multiply, "4", "=")
        assertResultContains("20")
    }

    @Test
    fun degRadToggle_chipIsDisplayedAndToggles() {
        // Default is DEG mode, chip shows "DEG"
        composeRule.onNodeWithText("DEG").assertExists()

        // Tap the chip to switch to RAD
        composeRule.onNodeWithText("DEG").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("RAD").assertExists()

        // The angle selector also supports gradians.
        composeRule.onNodeWithText("RAD").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("GRAD").assertExists()
        composeRule.onNodeWithText("GRAD").performClick()
        composeRule.onNodeWithText("DEG").assertExists()
    }

    @Test
    fun subtraction_5Minus2Equals3() {
        tapButtons("5", minus, "2", "=")
        assertResultContains("3")
    }

    @Test
    fun division_8DividedBy4Equals2() {
        tapButtons("8", divide, "4", "=")
        assertResultContains("2")
    }

    @Test
    fun scientificPanel_canBeCollapsedAndRestored() {
        composeRule.onNodeWithText("Scientific").performClick()
        composeRule.onNodeWithText("sin").assertDoesNotExist()
        composeRule.onNodeWithText("Basic").performClick()
        composeRule.onNodeWithText("sin").assertExists()
    }

    @Test
    fun additionalTools_areReachable() {
        composeRule.onNodeWithContentDescription("More tools").performClick()
        composeRule.onNodeWithText("Matrix").performClick()
        composeRule.onNodeWithText("Back to calculator").assertExists().performClick()
        composeRule.onNodeWithContentDescription("More tools").assertExists()
    }

    @Test
    fun incompleteExpression_doesNotExposeStaleResult() {
        tapButtons("2", "+")
        composeRule.onNodeWithContentDescription("Copy result").assertIsNotEnabled()
        tapButtons("3")
        composeRule.onNodeWithContentDescription("Copy result").assertIsEnabled()
        assertResultContains("5")
    }

    @Test
    fun repeatedDecimal_doesNotMakeInvalidNumber() {
        tapButtons("1", ".", "2", ".", "3", "=")
        assertResultContains("1.23")
    }

    @Test
    fun fractionDisplay_supportsMixedImproperAndDecimal() {
        tapButtons("C", "7", divide, "2", "=")
        assertResultContains("3.5")
        composeRule.onNodeWithText("S↔D").performClick()
        assertResultContains("3 1/2")
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Improper  d/c").performScrollTo().performClick()
        composeRule.onNodeWithText("Done").performClick()
        assertResultContains("7/2")
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        UiDevice.getInstance(instrumentation).takeScreenshot(
            File(instrumentation.targetContext.getExternalFilesDir(null), "fraction-result.png"),
        )
        composeRule.onNodeWithText("S↔D").performClick()
        assertResultContains("3.5")
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Mixed  ab/c").performScrollTo().performClick()
        composeRule.onNodeWithText("Done").performClick()
    }

    @Test
    fun invalidDomain_doesNotPoisonAnswerMemory() {
        tapButtons("C", "2", "=", "C", "1", divide, "0", "=")
        composeRule.onNodeWithContentDescription("Copy result").assertIsNotEnabled()
        composeRule.onNode(hasContentDescription("Error:", substring = true)).assertExists()
        tapButtons("C", "Ans", "+", "1", "=")
        assertResultContains("3")
    }

    @Test
    fun completedAnsResult_isStableAcrossFormattingAndMemory() {
        tapButtons("C", "MR", "=", "M−", "C", "1", "=", "+", "1", "=")
        assertResultContains("2")
        composeRule.onNodeWithText("S↔D").performClick()
        assertResultContains("2")
        composeRule.onNodeWithText("S↔D").performClick()
        assertResultContains("2")
        tapButtons("M+", "C", "MR", "=")
        assertResultContains("2")
        tapButtons("C", "1", "=", "+", "1", "=", "±", "=")
        assertResultContains("-2")
    }

    @Test
    fun constantEntryAndUnaryPowers_matchCalculatorConventions() {
        tapButtons("C", "2", "e", "+", "3", "=")
        assertResultContains("8.436563657")
        tapButtons("C", minus, "2", "xʸ", "2", "=")
        assertResultContains("-4")
    }
}
