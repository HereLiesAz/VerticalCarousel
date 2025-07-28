package com.hereliesaz.verticalcarousel

import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarousel.internal.KeylineState
import org.junit.Assert.assertEquals
import org.junit.Test

class KeylineStateTest {

    @Test
    fun heroStrategy_calculatesCorrectKeylines() {
        assertEquals(3, state.keylines.size)
        assertEquals(300.dp, state.keylines[0].size)
        assertEquals(600.dp, state.keylines[2].offset)
    }

    // Corrected Test in KeylineStateTest.kt
    @Test
    fun multiBrowseStrategy_calculatesCorrectKeylines() {
        // Assuming a dummy Density object is available for the test
        // or the test is refactored into an instrumented test.
        // For this example, let's focus on the logic.
        val state = KeylineState(
            density =,
            itemHeight = 200.dp,
            itemSpacing = 8.dp,
            itemCount = 5,
            strategy = KeylineState.Strategy.MultiBrowse
        )

        val smallSize = 200.dp * 0.7f // 140.dp

        assertEquals(5, state.keylines.size) // Should be 5
        assertEquals(smallSize, state.keylines[0].size)     // Offscreen top
        assertEquals(smallSize, state.keylines[1].size)     // Top small item
        assertEquals(200.dp, state.keylines[2].size)        // Focused large item
        assertEquals(smallSize, state.keylines[3].size)     // Bottom small item
        assertEquals(smallSize, state.keylines[4].size)     // Offscreen bottom
    }
}