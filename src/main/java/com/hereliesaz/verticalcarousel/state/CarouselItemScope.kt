package com.hereliesaz.verticalcarousel.state

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

interface CarouselItemScope {
    val carouselItemInfo: CarouselItemInfo
    fun Modifier.maskClip(shape: androidx.compose.ui.graphics.Shape): Modifier
}

data class CarouselItemInfo(
    val size: Dp,
    val mask: Float,
)

internal class CarouselItemScopeImpl(
    override val carouselItemInfo: CarouselItemInfo
) : CarouselItemScope {
    override fun Modifier.maskClip(shape: androidx.compose.ui.graphics.Shape): Modifier {
        return this
            .graphicsLayer(
                shape = shape,
                clip = true,
                alpha = carouselItemInfo.mask
            )
            .alpha(if (carouselItemInfo.mask < 0.1f) 0f else 1f) // Hide if fully masked
    }
}
