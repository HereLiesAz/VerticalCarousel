package com.hereliesaz.verticalcarousel.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarousel.internal.BaseVerticalCarousel
import com.hereliesaz.verticalcarousel.internal.KeylineState
import com.hereliesaz.verticalcarousel.state.CarouselItemScope
import com.hereliesaz.verticalcarousel.state.CarouselState

@Composable
fun VerticalHeroCarousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit
) {
    val density = LocalDensity.current
    val keylineState = remember(state.itemCount(), density) {
        KeylineState(
            density = density,
            itemHeight = 0.dp, // Height is determined by the container in the layout phase
            itemSpacing = 0.dp,
            itemCount = state.itemCount(),
            strategy = KeylineState.Strategy.Hero
        )
    }

    BaseVerticalCarousel(
        state = state,
        modifier = modifier,
        keylineState = keylineState,
        content = content
    )
}