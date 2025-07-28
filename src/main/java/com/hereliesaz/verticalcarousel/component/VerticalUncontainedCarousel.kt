package com.hereliesaz.verticalcarousel.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarousel.internal.BaseVerticalCarousel
import com.hereliesaz.verticalcarousel.internal.KeylineState
import com.hereliesaz.verticalcarousel.state.CarouselItemScope
import com.hereliesaz.verticalcarousel.state.CarouselState

@Composable
fun VerticalUncontainedCarousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    itemSpacing: Dp = 0.dp,
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit
) {
    val density = LocalDensity.current
    val keylineState = remember(itemHeight, itemSpacing, state.itemCount(), density) {
        KeylineState(
            density = density,
            itemHeight = itemHeight,
            itemSpacing = itemSpacing,
            itemCount = state.itemCount(),
            strategy = KeylineState.Strategy.Uncontained
        )
    }

    BaseVerticalCarousel(
        state = state,
        modifier = modifier,
        keylineState = keylineState,
        content = content
    )
}