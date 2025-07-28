package com.hereliesaz.verticalcarousel.internal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
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
                val velocityTracker = VelocityTracker()
                detectDragGestures(
                    onDragStart = { velocityTracker.resetTracking() },
                    onDragEnd = {
                        coroutineScope.launch {
                            val velocity = velocityTracker.calculateVelocity()
                            val initialVelocity = velocity.y

                            // This is the logic from your CarouselFlingBehavior,
                            // now adapted to be used directly here.
                            val snapStep = keylineState.getSnapStep()
                            if (abs(initialVelocity) < 0.1f || snapStep <= 0f) return@launch

                            val targetIndex =
                                (abs(keylineState.scrollOffset.value) / snapStep).toInt() +
                                        if (initialVelocity > 0) -1 else 1
                            val targetValue = targetIndex * snapStep * -1

                            animate(
                                initialValue = keylineState.scrollOffset.value,
                                targetValue = targetValue,
                                initialVelocity = initialVelocity,
                                animationSpec = flingSpec
                            ) { value, _ ->
                                state.scrollableState.dispatchRawDelta(value - keylineState.scrollOffset.value)
                            }
                        }
                    }
                ) { change, dragAmount ->
                    if (abs(dragAmount.y) > abs(dragAmount.x)) {
                        change.consume()
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        state.scrollableState.dispatchRawDelta(dragAmount.y)
                    }
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