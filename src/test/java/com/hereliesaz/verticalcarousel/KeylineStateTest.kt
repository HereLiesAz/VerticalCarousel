package com.hereliesaz.verticalcarousel

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarousel.internal.KeylineState
import org.junit.Assert.assertEquals
import org.junit.Test

class KeylineStateTest {

    // Create a fake density for testing purposes. On the JVM, there's no screen context.
    // A density of 1f makes conversions easy: 1.dp = 1px.
    private val testDensity = Density(density = 1f, fontScale = 1f)

    @Test
    fun heroStrategy_calculatesCorrectKeylines() {
        // FIX: Provide the required density object to the constructor.
        val state = KeylineState(
            density = testDensity,
            itemHeight = 300.dp,
            itemSpacing = 0.dp,
            itemCount = 3,
            strategy = KeylineState.Strategy.Hero
        )
        assertEquals(3, state.keylines.size)
        assertEquals(300.dp, state.keylines[0].size)
        // Offset of item at index 2 should be (itemHeight * 2)
        assertEquals(600.dp, state.keylines[2].offset)
    }

    @Test
    fun multiBrowseStrategy_calculatesCorrectKeylines() {
        // FIX: Provide the required density object to the constructor.
        val state = KeylineState(
            density = testDensity,
            itemHeight = 200.dp,
            itemSpacing = 8.dp,
            itemCount = 5, // This value doesn't affect this strategy's keyline count
            strategy = KeylineState.Strategy.MultiBrowse
        )

        val largeSize = 200.dp
        val smallSize = largeSize * 0.7f // 140.dp

        // FIX: The MultiBrowse strategy ALWAYS creates 5 keylines, not 3.
        assertEquals(5, state.keylines.size)

        // FIX: Assert the correct sizes at the correct indices.
        assertEquals(smallSize, state.keylines[0].size) // Off-screen top
        assertEquals(smallSize, state.keylines[1].size) // Top small item
        assertEquals(largeSize, state.keylines[2].size) // Focused large item
        assertEquals(smallSize, state.keylines[3].size) // Bottom small item
        assertEquals(smallSize, state.keylines[4].size) // Off-screen bottom
    }
}