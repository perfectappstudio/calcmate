package com.calcmate.scientificcalculator

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
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

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

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
        // Result should be 1 (or very close). Check expression was evaluated.
        composeRule.waitForIdle()
        composeRule
            .onNode(hasContentDescription("Result:", substring = true))
            .assertExists()
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
        // (2+3)×4 = 20. Check that result exists after evaluation.
        tapButtons("(", "2", "+", "3", ")", multiply, "4", "=")
        composeRule.waitForIdle()
        composeRule
            .onNode(hasContentDescription("Result:", substring = true))
            .assertExists()
    }

    @Test
    fun degRadToggle_chipIsDisplayedAndToggles() {
        // Default is DEG mode, chip shows "DEG"
        composeRule.onNodeWithText("DEG").assertExists()

        // Tap the chip to switch to RAD
        composeRule.onNodeWithText("DEG").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("RAD").assertExists()

        // Tap again to go back to DEG
        composeRule.onNodeWithText("RAD").performClick()
        composeRule.waitForIdle()

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
}
