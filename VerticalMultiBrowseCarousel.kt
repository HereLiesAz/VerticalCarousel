package com.hereliesaz.verticalcarousel

import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.orientation.Vertical
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarousel.internal.KeylineState
import com.hereliesaz.verticalcarousel.internal.place

@Composable
fun VerticalMultiBrowseCarousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    preferredItemHeight: Dp,
    itemSpacing: Dp = 0.dp,
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit
) {
    val flingBehavior = state.fling(0f, spring())
    state.keylineState = remember(preferredItemHeight, itemSpacing, state.itemCount()) {
        KeylineState(
            itemHeight = preferredItemHeight,
            itemSpacing = itemSpacing,
            itemCount = state.itemCount(),
            strategy = KeylineState.Strategy.MultiBrowse
        )
    }

    Layout(
        modifier = modifier
            .scrollable(
                orientation = Vertical,
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
