package com.hereliesaz.verticalcarousel.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalMultiBrowseCenteredCarousel(
    state: PagerState,
    modifier: Modifier = Modifier,
    preferredItemHeight: Dp,
    itemSpacing: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable (page: Int) -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        VerticalPager(
            state = state,
            modifier = Modifier.fillMaxSize(),
            pageSize = PageSize.Fixed(preferredItemHeight),
            contentPadding = contentPadding,
            pageSpacing = itemSpacing
        ) { page ->
            val pageOffset = (
                (state.currentPage - page) + state.currentPageOffsetFraction
                ).absoluteValue

            val scale = lerp(1f, 0.85f, pageOffset)
            val alpha = lerp(1f, 0.5f, pageOffset)

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            ) {
                content(page)
            }
        }
    }
}
