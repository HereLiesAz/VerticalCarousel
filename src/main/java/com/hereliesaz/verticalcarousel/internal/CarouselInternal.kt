package com.hereliesaz.verticalcarousel.internal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.hereliesaz.verticalcarousel.state.CarouselItemInfo
import kotlin.math.abs
import kotlin.math.sign

internal data class Keyline(
    val offset: Dp,
    val size: Dp,
    val mask: Float,
)

internal class KeylineState(
    var itemHeight: Dp,
    val itemSpacing: Dp,
    val itemCount: Int,
    val strategy: Strategy
) {
    enum class Strategy {
        Hero,
        MultiBrowse,
        Uncontained
    }

    var scrollOffset = mutableStateOf(0f)
    val keylines: List<Keyline> = strategy.calculateKeylines(this)

    fun scrollBy(delta: Float): Float {
        val newScrollOffset = (scrollOffset.value + delta)
        scrollOffset.value = newScrollOffset
        return delta
    }

    /**
     * Calculates the interpolated size and mask for an item at a given index
     * based on the current scroll offset.
     */
    fun getItemInfo(index: Int): CarouselItemInfo {
        val scroll = scrollOffset.value
        val itemScrollOffset = (itemHeight.toPx() + itemSpacing.toPx()) * index + scroll
        
        // Find the keylines that the current item is between
        val lowerKeylineIndex = keylines.indexOfLast { it.offset.toPx() <= itemScrollOffset }.coerceAtLeast(0)
        val upperKeylineIndex = (lowerKeylineIndex + 1).coerceAtMost(keylines.lastIndex)
        
        val lowerKeyline = keylines[lowerKeylineIndex]
        val upperKeyline = keylines[upperKeylineIndex]

        // Calculate the progress between the two keylines
        val progress = (itemScrollOffset - lowerKeyline.offset.toPx()) /
                (upperKeyline.offset.toPx() - lowerKeyline.offset.toPx()).coerceAtLeast(1f)

        // Interpolate the size and mask
        val size = lerp(lowerKeyline.size, upperKeyline.size, progress)
        val mask = lerp(lowerKeyline.mask, upperKeyline.mask, progress)

        return CarouselItemInfo(size, mask)
    }


    internal fun getSnapStep(): Float {
        return (itemHeight + itemSpacing).toPx()
    }
}

// Keyline calculation strategies
internal fun Strategy.calculateKeylines(state: KeylineState): List<Keyline> {
    return when (this) {
        KeylineState.Strategy.Hero -> {
            (0 until state.itemCount).map {
                Keyline(offset = state.itemHeight * it, size = state.itemHeight, mask = 1f)
            }
        }
        KeylineState.Strategy.Uncontained -> {
            (0 until state.itemCount).map {
                Keyline(offset = (state.itemHeight + state.itemSpacing) * it, size = state.itemHeight, mask = 1f)
            }
        }
        KeylineState.Strategy.MultiBrowse -> {
            val largeItemSize = state.itemHeight
            val smallItemSize = state.itemHeight * 0.7f
            listOf(
                Keyline(offset = (-smallItemSize - state.itemSpacing).toPx().dp, size = smallItemSize, mask = 0f), // Offscreen top
                Keyline(offset = 0.dp, size = smallItemSize, mask = 0.5f), // Top small item
                Keyline(offset = smallItemSize + state.itemSpacing, size = largeItemSize, mask = 1f), // Focused item
                Keyline(offset = smallItemSize + largeItemSize + (state.itemSpacing * 2), size = smallItemSize, mask = 0.5f), // Bottom small item
                Keyline(offset = smallItemSize + largeItemSize + smallItemSize + (state.itemSpacing * 3), size = smallItemSize, mask = 0f) // Offscreen bottom
            )
        }
    }
}


internal class CarouselScrollableState(val onDelta: (Float) -> Float) : ScrollableState {
    override val isScrollInProgress: Boolean = true
    override fun dispatchRawDelta(delta: Float): Float = onDelta(delta)
    override suspend fun scroll(
        scrollPriority: androidx.compose.foundation.MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        // No-op for this implementation
    }
}

internal class CarouselFlingBehavior(
    val scrollableState: ScrollableState,
    val keylineState: KeylineState,
    val snapAnimationSpec: AnimationSpec<Float>
) : FlingBehavior {
    override suspend fun performFling(initialVelocity: Float): Float {
        val snapStep = keylineState.getSnapStep()
        if (abs(initialVelocity) < 0.1f) return 0f

        val targetIndex = (abs(keylineState.scrollOffset.value) / snapStep).toInt() +
                if (initialVelocity > 0) -1 else 1

        val targetValue = (targetIndex * snapStep) * sign(keylineState.scrollOffset.value)


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
    val itemConstraints = constraints.copy(
        minHeight = itemInfo.size.roundToPx(),
        maxHeight = itemInfo.size.roundToPx()
    )
    val placeable = measurable.measure(itemConstraints)

    val y = keylineState.keylines[index].offset.toPx() + keylineState.scrollOffset.value

    placeable.place(0, y.toInt())
}