package com.hereliesaz.verticalcarousel

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hereliesaz.verticalcarousel.internal.CarouselFlingBehavior
import com.hereliesaz.verticalcarousel.internal.CarouselScrollableState
import com.hereliesaz.verticalcarousel.internal.KeylineState

@Composable
fun rememberCarouselState(
    initialItem: Int = 0,
    itemCount: () -> Int,
): CarouselState {
    return remember {
        CarouselState(
            initialItem,
            itemCount,
        )
    }
}

class CarouselState(
    val initialItem: Int,
    val itemCount: () -> Int,
) {
    internal lateinit var keylineState: KeylineState
    internal val scrollableState: ScrollableState = CarouselScrollableState { delta ->
        keylineState.scrollBy(delta)
    }

    internal fun fling(velocity: Float, spec: AnimationSpec<Float>): FlingBehavior {
        return CarouselFlingBehavior(
            scrollableState = scrollableState,
            keylineState = keylineState,
            snapAnimationSpec = spec
        )
    }
}
