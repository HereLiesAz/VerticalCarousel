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
import com.hereliesaz.verticalcarousel.CarouselItemInfo
import kotlin.math.abs
import kotlin.math.sign

internal data class Keyline(
    val offset: Dp,
    val size: Dp,
    val mask: Float,
)

internal class KeylineState(
    val itemHeight: Dp,
    val itemSpacing: Dp,
    val itemCount: Int,
    val strategy: Strategy
) {
    enum class Strategy { MultiBrowse }

    var scrollOffset = mutableStateOf(0f)

    val keylines: List<Keyline> = strategy.calculateKeylines(this)

    fun scrollBy(delta: Float): Float {
        val newScrollOffset = (scrollOffset.value + delta)
        scrollOffset.value = newScrollOffset
        return delta
    }

    fun getItemInfo(index: Int): CarouselItemInfo {
        val keyline = keylines[index]
        return CarouselItemInfo(keyline.size, keyline.mask)
    }

    internal fun getSnapStep(): Float {
        return (itemHeight + itemSpacing).toPx()
    }
}

internal class CarouselScrollableState(val onDelta: (Float) -> Float) : ScrollableState {
    override val isScrollInProgress: Boolean = true
    override fun dispatchRawDelta(delta: Float): Float = onDelta(delta)
    override suspend fun scroll(
        scrollPriority: androidx.compose.foundation.MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        // No-op
    }
}

internal class CarouselFlingBehavior(
    val scrollableState: ScrollableState,
    val keylineState: KeylineState,
    val snapAnimationSpec: AnimationSpec<Float>
) : FlingBehavior {
    override suspend fun performFling(initialVelocity: Float): Float {
        val snapStep = keylineState.getSnapStep()
        val targetValue = (keylineState.scrollOffset.value / snapStep).toInt() * snapStep

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
