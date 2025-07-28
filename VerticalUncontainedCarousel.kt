package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalUncontainedCarousel(
    state: PagerState,
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    itemSpacing: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable (page: Int) -> Unit,
) {
    VerticalPager(
        state = state,
        modifier = modifier,
        pageSize = PageSize.Fixed(itemHeight),
        pageSpacing = itemSpacing,
        contentPadding = contentPadding,
    ) { page ->
        val pageOffset = (
            (state.currentPage - page) + state.currentPageOffsetFraction
            ).absoluteValue

        val scale = lerp(1f, 0.9f, pageOffset.coerceIn(0f, 1f))
        val alpha = lerp(1f, 0.5f, pageOffset.coerceIn(0f, 1f))

        Box(
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
        ) {
            content(page)
        }
    }
}
