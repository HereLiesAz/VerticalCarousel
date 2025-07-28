package com.hereliesaz.verticalcarousel

import androidx.compose.runtime.Composable
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
    state.keylineState = KeylineState(
        itemHeight = preferredItemHeight,
        itemSpacing = itemSpacing,
        itemCount = state.itemCount()
    )

    Layout(
        modifier = modifier,
        content = {
            for (i in 0 until state.itemCount()) {
                val scope = CarouselItemScopeImpl(CarouselItemInfo(preferredItemHeight, 0f))
                scope.content(i)
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
