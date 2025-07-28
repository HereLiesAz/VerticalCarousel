package com.hereliesaz.verticalcarousel.internal

import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable // <-- FIX: A simple, direct import is all we need.
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import com.hereliesaz.verticalcarousel.state.CarouselItemScope
import com.hereliesaz.verticalcarousel.state.CarouselItemScopeImpl
import com.hereliesaz.verticalcarousel.state.CarouselState

@Composable
internal fun BaseVerticalCarousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    keylineState: KeylineState,
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit,
) {
    state.keylineState = keylineState
    val flingBehavior: FlingBehavior = state.fling(0f, spring())

    Layout(
        // FIX: The .scrollable modifier from foundation.gestures handles accessibility automatically.
        // The custom .semantics block has been removed as it was incorrect and unnecessary.
        modifier = modifier
            .scrollable(
                orientation = Orientation.Vertical,
                state = state.scrollableState,
                flingBehavior = flingBehavior
            ),
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