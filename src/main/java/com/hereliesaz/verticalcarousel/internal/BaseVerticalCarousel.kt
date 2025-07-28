package com.hereliesaz.verticalcarousel.internal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import com.hereliesaz.verticalcarousel.state.CarouselItemScope
import com.hereliesaz.verticalcarousel.state.CarouselItemScopeImpl
import com.hereliesaz.verticalcarousel.state.CarouselState
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
internal fun BaseVerticalCarousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    keylineState: KeylineState,
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit,
) {
    state.keylineState = keylineState
    val coroutineScope = rememberCoroutineScope()
    val flingSpec: AnimationSpec<Float> = spring()

    Layout(
        modifier = modifier
            .pointerInput(state) {
                // VelocityTracker is needed to calculate the fling velocity when the drag ends.
                val velocityTracker = VelocityTracker()
                detectDragGestures(
                    onDragStart = { velocityTracker.resetTracking() },
                    onDragEnd = {
                        coroutineScope.launch {
                            val flingBehavior = CarouselFlingBehavior(
                                scrollableState = state.scrollableState,
                                keylineState = keylineState,
                                snapAnimationSpec = flingSpec
                            )
                            // Use the velocityTracker to get the final velocity of the gesture.
                            val velocity = velocityTracker.calculateVelocity()

                            // performFling requires a ScrollScope, which we create an instance of.
                            val scrollScope = object : androidx.compose.foundation.gestures.ScrollScope {
                                override fun scrollBy(pixels: Float): Float {
                                    return state.scrollableState.dispatchRawDelta(pixels)
                                }
                            }
                            // Call the original fling behavior with the correct scope and velocity.
                            scrollScope.performFling(velocity.y)
                        }
                    }
                ) { change, dragAmount ->
                    if (abs(dragAmount.y) > abs(dragAmount.x)) {
                        // It's a vertical drag, handle it.
                        change.consume()
                        // Add the pointer event to the velocity tracker to track fling speed.
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        // Manually scroll the carousel state.
                        state.scrollableState.dispatchRawDelta(dragAmount.y)
                    }
                    // If horizontal, do nothing and let the event propagate.
                }
            },
        content = {
            for (i in 0 until state.itemCount()) {
                Box {
                    val scope = CarouselItemScopeImpl(
                        carouselItemInfo = state.keylineState.getItemInfo(i)
                    )
                    scope.content(i)
                }
            }
        }
    ) { measurables, constraints ->
        if (state.keylineState.strategy == KeylineState.Strategy.Hero) {
            state.keylineState.itemHeight = constraints.maxHeight.toDp()
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            measurables.forEachIndexed { index, measurable ->
                place(
                    index = index,
                    measurable = measurable,
                    constraints = constraints,
                    keylineState = state.keylineState
                )
            }
        }
    }
}
