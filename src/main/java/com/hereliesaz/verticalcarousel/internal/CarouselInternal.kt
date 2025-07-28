package com.hereliesaz.verticalcarousel.internal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.hereliesaz.verticalcarousel.state.CarouselItemInfo
import kotlin.math.abs
import kotlin.math.roundToInt

internal data class Keyline(
    val offset: Dp,
    val size: Dp,
    val mask: Float,
)

internal class KeylineState(
    val density: Density,
    var itemHeight: Dp,
    val itemSpacing: Dp,
    val itemCount: Int,
    val strategy: Strategy,
    val contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(
        0.dp
    ),
) {
    enum class Strategy {
        Hero,
        MultiBrowse,
        Uncontained
    }

    var scrollOffset = mutableStateOf(0f)
    var containerSize: Dp = 0.dp // To be set by the layout
    val keylines: List<Keyline> by lazy { strategy.calculateKeylines(this) }

    fun scrollBy(delta: Float): Float {
        val newScrollOffset = (scrollOffset.value + delta)
        scrollOffset.value = newScrollOffset
        return delta
    }

    fun getItemInfo(index: Int): CarouselItemInfo {
        val densityValue = density.density
        val itemHeightPx = itemHeight.value * densityValue
        val itemSpacingPx = itemSpacing.value * densityValue

        val scroll = scrollOffset.value
        // The position of an item is its index multiplied by its full size (item + spacing),
        // adjusted by the current scroll offset.
        val itemScrollOffset = (itemHeightPx + itemSpacingPx) * index + scroll

        // Find the two keylines the item is currently between.
        val lowerKeylineIndex =
            keylines.indexOfLast { it.offset.toPx() <= itemScrollOffset }
                .coerceAtLeast(0)
        val upperKeylineIndex = (lowerKeylineIndex + 1).coerceAtMost(keylines.lastIndex)

        val lowerKeyline = keylines[lowerKeylineIndex]
        val upperKeyline = keylines[upperKeylineIndex]

        val lowerKeylineOffsetPx = lowerKeyline.offset.toPx()
        val upperKeylineOffsetPx = upperKeyline.offset.toPx()

        // Calculate the item's progress (0.0 to 1.0) between the two keylines.
        val progress = if (upperKeylineOffsetPx - lowerKeylineOffsetPx != 0f) {
            ((itemScrollOffset - lowerKeylineOffsetPx) / (upperKeylineOffsetPx - lowerKeylineOffsetPx)).coerceIn(0f, 1f)
        } else {
            0f
        }

        // Linearly interpolate the size and mask based on the progress.
        val size = lerp(lowerKeyline.size, upperKeyline.size, progress)
        val mask = lerp(lowerKeyline.mask, upperKeyline.mask, progress)

        return CarouselItemInfo(size, mask)
    }

    internal fun getSnapStep(): Float {
        return (itemHeight.value + itemSpacing.value) * density.density
    }

    private fun Keyline.toPx() = this.offset.value * density.density
}

internal fun KeylineState.Strategy.calculateKeylines(state: KeylineState): List<Keyline> {
    return with(state.density) {
        when (this@calculateKeylines) {
            KeylineState.Strategy.Hero -> {
                (0 until state.itemCount).map {
                    Keyline(offset = state.itemHeight * it, size = state.itemHeight, mask = 1f)
                }
            }

            KeylineState.Strategy.Uncontained -> {
                (0 until state.itemCount).map {
                    Keyline(
                        offset = (state.itemHeight + state.itemSpacing) * it,
                        size = state.itemHeight,
                        mask = 1f
                    )
                }
            }

            KeylineState.Strategy.MultiBrowse -> {
                val largeItemSize = state.itemHeight
                // The small items should be a fraction of the large item size.
                val smallItemSize = state.itemHeight * 0.5f
                val containerHeight = state.containerSize.toPx()

                // Calculate the center of the container.
                val center = containerHeight / 2f
                val largeItemCenter = center - largeItemSize.toPx() / 2f

                listOf(
                    // Off-screen top
                    Keyline(offset = (-largeItemSize).toDp(), size = smallItemSize, mask = 0f),
                    // Top small item
                    Keyline(offset = (largeItemCenter - smallItemSize.toPx() - state.itemSpacing.toPx()).toDp(), size = smallItemSize, mask = 0.5f),
                    // Focused large item (should be centered)
                    Keyline(offset = largeItemCenter.toDp(), size = largeItemSize, mask = 1f),
                    // Bottom small item
                    Keyline(offset = (largeItemCenter + largeItemSize.toPx() + state.itemSpacing.toPx()).toDp(), size = smallItemSize, mask = 0.5f),
                    // Off-screen bottom
                    Keyline(offset = containerHeight.toDp(), size = smallItemSize, mask = 0f)
                )
            }
        }
    }
}


internal class CarouselScrollableState(val onDelta: (Float) -> Float) : ScrollableState {
    override val isScrollInProgress: Boolean = true
    override fun dispatchRawDelta(delta: Float): Float = onDelta(delta)
    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit,
    ) {
    }
}

internal class CarouselFlingBehavior(
    val scrollableState: ScrollableState,
    val keylineState: KeylineState,
    val snapAnimationSpec: AnimationSpec<Float>
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        val snapStep = keylineState.getSnapStep()
        if (abs(initialVelocity) < 0.1f || snapStep <= 0f) return 0f
        val targetIndex = (abs(keylineState.scrollOffset.value) / snapStep).toInt() +
                if (initialVelocity > 0) -1 else 1
        val targetValue = targetIndex * snapStep * -1
        var remainingVelocity = initialVelocity
        animate(
            initialValue = keylineState.scrollOffset.value,
            targetValue = targetValue,
            initialVelocity = initialVelocity,
            animationSpec = snapAnimationSpec
        ) { value, velocity ->
            scrollableState.dispatchRawDelta(value - keylineState.scrollOffset.value)
            remainingVelocity = velocity
        }
        return remainingVelocity
    }
}


internal fun Placeable.PlacementScope.place(
    index: Int,
    measurable: Measurable,
    constraints: Constraints,
    keylineState: KeylineState
) {
    val itemInfo = keylineState.getItemInfo(index)

    val densityValue = keylineState.density.density
    val itemHeightPx = (itemInfo.size.value * densityValue).roundToInt()

    val itemConstraints = constraints.copy(
        minHeight = itemHeightPx,
        maxHeight = itemHeightPx
    )
    val placeable = measurable.measure(itemConstraints)

    // The y position is calculated based on the item's scroll offset relative to the keylines.
    val itemScrollOffset = (keylineState.itemHeight.toPx() + keylineState.itemSpacing.toPx()) * index + keylineState.scrollOffset.value

    val lowerKeylineIndex =
        keylineState.keylines.indexOfLast { it.offset.toPx() <= itemScrollOffset }
            .coerceAtLeast(0)
    val upperKeylineIndex = (lowerKeylineIndex + 1).coerceAtMost(keylineState.keylines.lastIndex)

    val lowerKeyline = keylineState.keylines[lowerKeylineIndex]
    val upperKeyline = keylineState.keylines[upperKeylineIndex]

    val progress = if (upperKeyline.offset.toPx() - lowerKeyline.offset.toPx() != 0f) {
        ((itemScrollOffset - lowerKeyline.offset.toPx()) / (upperKeyline.offset.toPx() - lowerKeyline.offset.toPx())).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Linearly interpolate the y-position based on the keyline offsets.
    val y = lerp(lowerKeyline.offset.toPx(), upperKeyline.offset.toPx(), progress)
    val centeredX = (constraints.maxWidth - placeable.width) / 2

    placeable.placeRelative(centeredX, y.roundToInt())
}

private fun Dp.toPx(density: Density = Density(1f)) = this.value * density.density