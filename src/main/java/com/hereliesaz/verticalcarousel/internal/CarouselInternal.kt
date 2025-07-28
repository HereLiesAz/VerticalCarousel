package com.hereliesaz.verticalcarousel.internal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.mutableStateOf // <-- This import is correct
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

    // FIX: Corrected the typo from `mutableStateof` to `mutableStateOf`
    var scrollOffset = mutableStateOf(0f)
    val keylines: List<Keyline> = strategy.calculateKeylines(this)

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
        val itemScrollOffset = (itemHeightPx + itemSpacingPx) * index + scroll

        val lowerKeylineIndex =
            keylines.indexOfLast { (it.offset.value * densityValue) <= itemScrollOffset }
                .coerceAtLeast(0)
        val upperKeylineIndex = (lowerKeylineIndex + 1).coerceAtMost(keylines.lastIndex)

        val lowerKeyline = keylines[lowerKeylineIndex]
        val upperKeyline = keylines[upperKeylineIndex]

        val lowerKeylineOffsetPx = lowerKeyline.offset.value * densityValue
        val upperKeylineOffsetPx = upperKeyline.offset.value * densityValue

        val progress = (itemScrollOffset - lowerKeylineOffsetPx) /
                (upperKeylineOffsetPx - lowerKeylineOffsetPx).coerceAtLeast(1f)

        val size = lerp(lowerKeyline.size, upperKeyline.size, progress)
        val mask = lerp(lowerKeyline.mask, upperKeyline.mask, progress)

        return CarouselItemInfo(size, mask)
    }

    internal fun getSnapStep(): Float {
        return (itemHeight.value + itemSpacing.value) * density.density
    }
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
                val smallItemSize = state.itemHeight * 0.7f
                listOf(
                    Keyline(
                        offset = (-smallItemSize - state.itemSpacing),
                        size = smallItemSize,
                        mask = 0f
                    ),
                    Keyline(offset = 0.dp, size = smallItemSize, mask = 0.5f),
                    Keyline(
                        offset = smallItemSize + state.itemSpacing,
                        size = largeItemSize,
                        mask = 1f
                    ),
                    Keyline(
                        offset = smallItemSize + largeItemSize + (state.itemSpacing * 2),
                        size = smallItemSize,
                        mask = 0.5f
                    ),
                    Keyline(
                        offset = smallItemSize + largeItemSize + smallItemSize + (state.itemSpacing * 3),
                        size = smallItemSize,
                        mask = 0f
                    )
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
        if (abs(initialVelocity) < 0.1f) return 0f
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

    val yOffsetPx = (keylineState.keylines[index].offset.value * densityValue)
    val y = yOffsetPx + keylineState.scrollOffset.value

    placeable.place(0, y.roundToInt())
}