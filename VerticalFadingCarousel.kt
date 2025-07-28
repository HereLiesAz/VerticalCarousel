package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalFadingCarousel(
    state: PagerState,
    modifier: Modifier = Modifier,
    itemHeight: androidx.compose.ui.unit.Dp,
    content: @Composable (page: Int) -> Unit,
) {
    val gradientColor = MaterialTheme.colorScheme.surface
    val gradientHeight = itemHeight / 2

    Box(modifier = modifier) {
        VerticalPager(
            state = state,
            pageSize = androidx.compose.foundation.pager.PageSize.Fixed(itemHeight),
            content = { page -> content(page) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphics.drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(gradientColor, Color.Transparent),
                            startY = 0f,
                            endY = gradientHeight.toPx()
                        )
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, gradientColor),
                            startY = size.height - gradientHeight.toPx(),
                            endY = size.height
                        )
                    )
                }
        )
    }
}
