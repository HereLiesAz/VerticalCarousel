package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalHeroCarousel(
    state: PagerState,
    modifier: Modifier = Modifier,
    content: @Composable (page: Int) -> Unit,
) {
    VerticalPager(
        state = state,
        modifier = modifier,
        pageSize = androidx.compose.foundation.pager.PageSize.Fill,
    ) { page ->
        val pageOffset = (state.currentPage - page) + state.currentPageOffsetFraction

        val alpha = lerp(
            start = 0f,
            stop = 1f,
            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
        )
        val translationY = pageOffset * (500f) // Parallax effect

        Box(
            modifier = Modifier
                .graphicsLayer {
                    this.alpha = alpha
                    this.translationY = translationY
                }
        ) {
            content(page)
        }
    }
}
