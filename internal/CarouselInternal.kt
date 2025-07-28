package com.hereliesaz.verticalcarousel.internal

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class Keyline(
    val offset: Dp,
    val size: Dp,
    val isAnchor: Boolean = false
)

internal class KeylineState(
    val itemHeight: Dp,
    val itemSpacing: Dp,
    val itemCount: Int
) {
    val keylines: List<Keyline> = calculateKeylines()

    private fun calculateKeylines(): List<Keyline> {
        val keylines = mutableListOf<Keyline>()
        var currentOffset = 0.dp
        for (i in 0 until itemCount) {
            keylines.add(Keyline(currentOffset, itemHeight, i == 0))
            currentOffset += itemHeight + itemSpacing
        }
        return keylines
    }
}

internal fun Placeable.PlacementScope.place(
    index: Int,
    measurable: Measurable,
    constraints: Constraints,
    keylineState: KeylineState
) {
    val itemConstraints = constraints.copy(
        minHeight = keylineState.itemHeight.roundToPx(),
        maxHeight = keylineState.itemHeight.roundToPx()
    )
    val placeable = measurable.measure(itemConstraints)
    placeable.place(0, keylineState.keylines[index].offset.roundToPx())
}
