package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarousel.component.VerticalHeroCarousel
import com.hereliesaz.verticalcarousel.component.VerticalMultiBrowseCarousel
import com.hereliesaz.verticalcarousel.state.rememberCarouselState
import org.junit.Rule
import org.junit.Test

class VerticalCarouselTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verticalHeroCarousel_displaysContent() {
        composeTestRule.setContent {
            val state = rememberCarouselState(itemCount = { 1 })
            VerticalHeroCarousel(state = state, modifier = Modifier.fillMaxSize()) {
                TestItem(text = "Item 0")
            }
        }
        composeTestRule.onNodeWithTag("item_0").assertIsDisplayed()
    }

    @Test
    fun verticalMultiBrowseCarousel_displaysContent() {
        composeTestRule.setContent {
            val state = rememberCarouselState(itemCount = { 3 })
            VerticalMultiBrowseCarousel(
                state = state,
                modifier = Modifier.fillMaxSize(),
                preferredItemHeight = 200.dp
            ) { index ->
                TestItem(text = "Item $index")
            }
        }
        composeTestRule.onNodeWithTag("item_1").assertIsDisplayed()
    }
}

@Composable
private fun TestItem(text: String) {
    Text(text, modifier = Modifier.height(100.dp).testTag("item_${text.last()}"))
}