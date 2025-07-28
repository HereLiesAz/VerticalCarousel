package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
        content = { page -> content(page) }
    )
}
