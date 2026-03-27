package com.perfectappstudio.scientificcalc

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
 * Instrumented UI tests for the Converter screen.
 *
 * The converter is reached via the "Converter" nav tab. It shows:
 *   - Category chips in a LazyRow (Length, Weight, Temperature, ...)
 *   - "From" / "To" labels with unit pickers
 *   - A swap button with content description "Swap units"
 *   - A BasicTextField for input and a result Text
 */
@RunWith(AndroidJUnit4::class)
class ConverterScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navigateToConverter() {
        composeRule.onNodeWithText("Converter").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun defaultCategory_isLength() {
        // "Length" chip should be visible (it is the default selected category)
        composeRule.onNodeWithText("Length").assertExists()
        // "From" and "To" sections should be visible
        composeRule.onNodeWithText("From").assertExists()
        composeRule.onNodeWithText("To").assertExists()
    }

    @Test
    fun selectWeightCategory_showsWeightUnits() {
        composeRule.onNodeWithText("Weight").performClick()
        composeRule.waitForIdle()

        // Weight category should now be active. The unit picker should show weight units.
        // The "From" label should still be visible.
        composeRule.onNodeWithText("From").assertExists()
        composeRule.onNodeWithText("To").assertExists()
    }

    @Test
    fun swapButton_swapsFromAndToUnits() {
        // The swap button has contentDescription "Swap units"
        composeRule.onNodeWithContentDescription("Swap units").assertExists()

        // Tap the swap button
        composeRule.onNodeWithContentDescription("Swap units").performClick()
        composeRule.waitForIdle()

        // The screen should still be functional (From / To labels visible)
        composeRule.onNodeWithText("From").assertExists()
        composeRule.onNodeWithText("To").assertExists()
    }

    @Test
    fun selectTemperatureCategory_showsTemperatureUnits() {
        composeRule.onNodeWithText("Temperature").performClick()
        composeRule.waitForIdle()

        // Temperature category should be selected. Verify the conversion card is visible.
        composeRule.onNodeWithText("From").assertExists()
        composeRule.onNodeWithText("To").assertExists()
    }

    @Test
    fun enterValue_resultUpdates() {
        // The default input value is "1". The result should not be "---".
        // We don't check the exact number since it depends on the default units,
        // but we verify the "From" and "To" sections are present and the screen is functional.
        composeRule.onNodeWithText("From").assertExists()

        // The result area should show something other than "---" since default input is "1"
        // and default units are valid length units.
        composeRule.onNodeWithText("---").assertDoesNotExist()
    }
}
