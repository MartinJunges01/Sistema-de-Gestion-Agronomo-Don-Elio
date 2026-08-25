package com.itec.donelio.presentation.ui.screen.reportes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class DoubleBarIndicatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun doubleBarIndicator_doesNotCrashWithZeroMax() {
        composeTestRule.setContent {
            DoubleBarIndicator(
                labelA = "Campania A",
                valueA = 0f,
                maxA = 0f,
                colorA = Color.Green,
                labelB = "Campania B",
                valueB = 0f,
                maxB = 0f,
                colorB = Color.Blue
            )
        }

        // Si llega hasta aca y se renderiza, no hubo division por cero que crashee Compose.
        composeTestRule.onNodeWithText("Campania A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Campania B").assertIsDisplayed()
    }
}
