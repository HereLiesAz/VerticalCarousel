package com.hereliesaz.verticalcarousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        content = { page -> content(page) }
    )
}
