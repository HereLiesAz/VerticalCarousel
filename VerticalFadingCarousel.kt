package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalFadingCarousel(
    state: PagerState,
    modifier: Modifier = Modifier,
    itemHeight: androidx.compose.ui.unit.Dp,
    content: @Composable (page: Int) -> Unit,
) {
    VerticalPager(
        state = state,
        modifier = modifier,
        pageSize = androidx.compose.foundation.pager.PageSize.Fixed(itemHeight),
    ) { page ->
        val pageOffset = (
            (state.currentPage - page) + state.currentPageOffsetFraction
            ).absoluteValue

        // A sharp alpha fade with no scaling
        val alpha = lerp(
            start = 1f,
            stop = 0f,
            fraction = pageOffset.coerceIn(0f, 1f)
        )

        Box(
            modifier = Modifier.graphicsLayer { this.alpha = alpha }
        ) {
            content(page)
        }
    }
}
