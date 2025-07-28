package com.hereliesaz.verticalcarousel.internal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
    // The fling behavior needs to be created to be used after a drag ends.
    // We can't use the FlingBehavior directly in the pointerInput, but we can replicate its logic.
    val flingSpec: AnimationSpec<Float> = spring()

    Layout(
        // Replace the .scrollable modifier with a more cooperative pointerInput.
        modifier = modifier
            .pointerInput(state) {
                detectDragGestures(
                    onDragEnd = {
                        // After the drag gesture ends, launch a coroutine to handle the fling (snap).
                        coroutineScope.launch {
                            val flingBehavior = CarouselFlingBehavior(
                                scrollableState = state.scrollableState,
                                keylineState = keylineState,
                                snapAnimationSpec = flingSpec
                            )
                            // We need a ScrollScope to call performFling, but we can't get one here.
                            // So, we will manually trigger the snapping logic.
                            // This logic is a simplified version of what performFling does.
                            val snapStep = keylineState.getSnapStep()
                            if (snapStep > 0) {
                                val targetIndex = (abs(keylineState.scrollOffset.value) / snapStep).roundToInt()
                                val targetValue = targetIndex * snapStep * -1
                                state.scrollableState.scroll(androidx.compose.foundation.MutatePriority.Default) {
                                    androidx.compose.animation.core.animate(
                                        initialValue = keylineState.scrollOffset.value,
                                        targetValue = targetValue,
                                        animationSpec = flingSpec
                                    ) { value, _ ->
                                        scrollBy(value - keylineState.scrollOffset.value)
                                    }
                                }
                            }
                        }
                    }
                ) { change, dragAmount ->
                    // This is the core logic.
                    // Check if the drag is more vertical than horizontal.
                    if (abs(dragAmount.y) > abs(dragAmount.x)) {
                        // If it's a vertical drag, consume the event to prevent children from seeing it.
                        change.consume()
                        // And manually scroll the carousel state.
                        state.scrollableState.dispatchRawDelta(dragAmount.y)
                    }
                    // If the drag is more horizontal, we DO NOT consume it. This allows the gesture
                    // to propagate down to the child HorizontalMultiBrowseCarousel.
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
