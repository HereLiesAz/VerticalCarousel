package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
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
    internal lateinit var scrollableState: ScrollableState
    internal lateinit var flingBehavior: FlingBehavior
    internal lateinit var density: Density

}
