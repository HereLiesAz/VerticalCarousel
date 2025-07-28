package com.hereliesaz.verticalcarousel.component

import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.orientation.Vertical
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarousel.internal.KeylineState
import com.hereliesaz.verticalcarousel.internal.place
import com.hereliesaz.verticalcarousel.state.CarouselItemScope
import com.hereliesaz.verticalcarousel.state.CarouselItemScopeImpl
import com.hereliesaz.verticalcarousel.state.CarouselState

/**
 * A vertically scrolling carousel that centers each item with optional spacing.
 *
 * @param state The state object to be used to control the carousel.
 * @param modifier The modifier to be applied to this layout.
 * @param itemHeight The height of each item in the carousel.
 * @param itemSpacing The spacing between items.
 * @param contentPadding The padding to be applied to the content.
 * @param content The composable content for each item in the carousel.
 */
@Composable
fun VerticalCenteredCarousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    itemSpacing: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit
) {
    val flingBehavior = state.fling(0f, spring())
    state.keylineState = remember(itemHeight, itemSpacing, state.itemCount()) {
        KeylineState(
            itemHeight = itemHeight,
            itemSpacing = itemSpacing,
            itemCount = state.itemCount(),
            strategy = KeylineState.Strategy.Uncontained, // Uses the same simple strategy
            contentPadding = contentPadding
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