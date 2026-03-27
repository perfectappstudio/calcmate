package com.perfectappstudio.scientificcalc

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for bottom navigation.
 *
 * Navigation items are rendered by [CalcMateNavigationBar] with:
 *   - label text: "Calculator", "Graph", "Solver", "Converter"
 *   - icon content description: "Calculator tab", "Graph tab", etc.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun initialState_calculatorScreenIsShown() {
        // Calculator is the initial route. Verify the "C" button (near top, always visible).
        composeRule.onNodeWithContentDescription("C").assertExists()
    }

    @Test
    fun navigateToGraph_showsGraphScreen() {
        composeRule.onAllNodesWithText("Graph")[0].performClick()
        composeRule.waitForIdle()
        // Also verify the "Reset zoom" icon button is present
        composeRule.onNodeWithContentDescription("Reset zoom").assertExists()
    }

    @Test
    fun navigateToSolver_showsSolverScreen() {
        composeRule.onNodeWithText("Solver").performClick()
        composeRule.waitForIdle()

        // SolverScreen shows "Equation Solver" title and segmented buttons
        composeRule.onNodeWithText("Equation Solver").assertExists()
        composeRule.onNodeWithText("Quadratic").assertExists()
        composeRule.onNodeWithText("Linear").assertExists()
        composeRule.onNodeWithText("System").assertExists()
    }

    @Test
    fun navigateToConverter_showsConverterScreen() {
        composeRule.onNodeWithText("Converter").performClick()
        composeRule.waitForIdle()

        // ConverterScreen shows category chips; Length is selected by default
        composeRule.onNodeWithText("Length").assertExists()
        composeRule.onNodeWithText("Weight").assertExists()
        // Also verify "From" / "To" labels
        composeRule.onNodeWithText("From").assertExists()
        composeRule.onNodeWithText("To").assertExists()
    }

    @Test
    fun navigateBackToCalculator_showsCalculatorScreen() {
        // Navigate away first
        composeRule.onNodeWithText("Graph").performClick()
        composeRule.waitForIdle()

        // Navigate back to Calculator
        composeRule.onNodeWithText("Calculator").performClick()
        composeRule.waitForIdle()

        // Verify the calculator keypad is visible (C button near top)
        composeRule.onNodeWithContentDescription("C").assertExists()
    }

    @Test
    fun navigateAllTabs_noExceptions() {
        val tabs = listOf("Graph", "Solver", "Converter", "Calculator")
        tabs.forEach { tab ->
            composeRule.onNodeWithText(tab).performClick()
            composeRule.waitForIdle()
        }
        // If we got here without a crash, the test passes.
        composeRule.onNodeWithContentDescription("C").assertExists()
    }
}
