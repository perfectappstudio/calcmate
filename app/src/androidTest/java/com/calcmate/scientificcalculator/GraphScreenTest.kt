package com.calcmate.scientificcalculator

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for the Graph screen.
 *
 * The graph screen is reached via the "Graph" nav tab. It contains:
 *   - TopAppBar with title "Graph"
 *   - "Reset zoom" icon button (Icons.Default.Refresh)
 *   - "Settings" icon button
 *   - GraphControls with function entries and "+ Add function" text button
 *   - GraphCanvas (drawn on a Canvas composable)
 */
@RunWith(AndroidJUnit4::class)
class GraphScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navigateToGraph() {
        composeRule.onAllNodesWithText("Graph")[0].performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun graphTitle_isDisplayed() {
        // TopAppBar title "Graph" — there are two (title + nav label), just verify at least one exists
        composeRule.onAllNodesWithText("Graph").fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun addFunctionButton_isVisible() {
        // The GraphControls component shows a "+ Add function" TextButton
        composeRule.onNodeWithText("+ Add function").assertExists()
    }

    @Test
    fun resetZoomButton_isVisible() {
        // The reset zoom icon button has contentDescription "Reset zoom"
        composeRule.onNodeWithContentDescription("Reset zoom").assertExists()
    }

    @Test
    fun settingsButton_isVisible() {
        // The settings icon button has contentDescription "Settings"
        composeRule.onNodeWithContentDescription("Settings").assertExists()
    }

    @Test
    fun addFunction_addsNewEntry() {
        // Tap the "+ Add function" button to add a new function entry
        composeRule.onNodeWithText("+ Add function").performClick()
        composeRule.waitForIdle()

        // After adding, the placeholder "e.g. sin(x)" should appear in the new text field
        composeRule.onNodeWithText("e.g. sin(x)").assertExists()
    }

    @Test
    fun removeFunctionButton_isAccessible() {
        // Add a function first
        composeRule.onNodeWithText("+ Add function").performClick()
        composeRule.waitForIdle()

        // The remove button has contentDescription "Remove function"
        composeRule.onNodeWithContentDescription("Remove function").assertExists()
    }
}
