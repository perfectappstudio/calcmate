package com.calcmate.scientificcalculator

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for the Solver screen.
 *
 * The solver is reached via the "Solver" nav tab. It shows:
 *   - "Equation Solver" title
 *   - Segmented buttons: "Linear" | "Quadratic" | "System"
 *   - Quadratic is the default selected type
 *   - Each panel has coefficient inputs (OutlinedTextField with labels a, b, c)
 *     and a "Solve" button
 */
@RunWith(AndroidJUnit4::class)
class SolverScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navigateToSolver() {
        composeRule.onNodeWithText("Solver").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun defaultState_quadraticIsSelected() {
        composeRule.onNodeWithText("Equation Solver").assertExists()
        composeRule.onNodeWithText("Quadratic").assertExists()

        // The quadratic panel should show the equation description
        composeRule.onNodeWithText("Quadratic Equation: ax\u00B2 + bx + c = 0")
            .assertExists()
    }

    @Test
    fun tapLinear_showsLinearPanel() {
        composeRule.onNodeWithText("Linear").performClick()
        composeRule.waitForIdle()

        // Linear panel should show the equation description
        composeRule.onNodeWithText("Linear Equation: ax + b = 0")
            .assertExists()

        // Should have a "Solve" button
        composeRule.onNodeWithText("Solve").assertExists()
    }

    @Test
    fun tapSystem_showsSystemPanel() {
        composeRule.onNodeWithText("System").performClick()
        composeRule.waitForIdle()

        // System panel should show the title
        composeRule.onNodeWithText("System of Linear Equations")
            .assertExists()

        // Should have 2x2 and 3x3 chips
        composeRule.onNodeWithText("2 \u00D7 2").assertExists()
        composeRule.onNodeWithText("3 \u00D7 3").assertExists()
    }

    @Test
    fun quadraticSolve_enterCoefficientsAndSolve() {
        // Quadratic is already selected. Enter a=1, b=-3, c=2.
        // Labels are "a", "b", "c" in OutlinedTextFields.

        // Find the text field labeled "a" and enter "1"
        composeRule.onNodeWithText("a").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("a").performTextClearance()
        composeRule.onNodeWithText("a").performTextInput("1")

        // Find the text field labeled "b" and enter "-3"
        composeRule.onNodeWithText("b").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("b").performTextClearance()
        composeRule.onNodeWithText("b").performTextInput("-3")

        // Find the text field labeled "c" and enter "2"
        composeRule.onNodeWithText("c").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("c").performTextClearance()
        composeRule.onNodeWithText("c").performTextInput("2")

        // Tap the Solve button (may need scroll since it could be below fold)
        composeRule.onNodeWithText("Solve").performScrollTo().performClick()
        composeRule.waitForIdle()

        // For a=1, b=-3, c=2: discriminant = 9-8 = 1 > 0 => two real roots
        // x1 = (3+1)/2 = 2, x2 = (3-1)/2 = 1
        // The result card should show "Result" and the root values
        composeRule.onNodeWithText("Result").assertExists()
    }

    @Test
    fun switchBetweenTabs_maintainsFunctionality() {
        // Start on Quadratic (default)
        composeRule.onNodeWithText("Quadratic Equation: ax\u00B2 + bx + c = 0")
            .assertExists()

        // Switch to Linear
        composeRule.onNodeWithText("Linear").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Linear Equation: ax + b = 0")
            .assertExists()

        // Switch to System
        composeRule.onNodeWithText("System").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("System of Linear Equations")
            .assertExists()

        // Switch back to Quadratic
        composeRule.onNodeWithText("Quadratic").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Quadratic Equation: ax\u00B2 + bx + c = 0")
            .assertExists()
    }
}
