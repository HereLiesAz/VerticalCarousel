package com.hereliesaz.verticalcarousel

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
        return this.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }.graphicsLayer(
            shape = shape,
            clip = true
        )
    }
}
