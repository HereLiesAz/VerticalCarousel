package com.hereliesaz.verticalcarousel

import com.hereliesaz.verticalcarousel.internal.KeylineState
import org.junit.Assert.assertEquals
import org.junit.Test

class KeylineStateTest {

    @Test
    fun heroStrategy_calculatesCorrectKeylines() {
        val state = KeylineState(
            itemHeight = 300.dp,
            itemSpacing = 0.dp,
            itemCount = 3,
            strategy = KeylineState.Strategy.Hero
        )
        assertEquals(3, state.keylines.size)
        assertEquals(300.dp, state.keylines[0].size)
        assertEquals(600.dp, state.keylines[2].offset)
    }

    @Test
    fun multiBrowseStrategy_calculatesCorrectKeylines() {
        val state = KeylineState(
            itemHeight = 200.dp,
            itemSpacing = 8.dp,
            itemCount = 3,
            strategy = KeylineState.Strategy.MultiBrowse
        )
        // This is a simplified test. A real test would be more thorough.
        assertEquals(3, state.keylines.size)
        assertEquals(200.dp, state.keylines[1].size) // Focused item
        assertEquals(140.dp, state.keylines[0].size) // Small item (200 * 0.7)
    }
}