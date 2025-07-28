package com.hereliesaz.verticalcarouselsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState as rememberM3CarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.hereliesaz.verticalcarouselsample.ui.theme.VerticalCarouselSampleTheme
import com.hereliesaz.verticalcarousel.component.VerticalCenteredCarousel
import com.hereliesaz.verticalcarousel.component.VerticalHeroCarousel
import com.hereliesaz.verticalcarousel.component.VerticalMultiBrowseCarousel
import com.hereliesaz.verticalcarousel.component.VerticalMultiBrowseCenteredCarousel
import com.hereliesaz.verticalcarousel.component.VerticalUncontainedCarousel
import com.hereliesaz.verticalcarousel.state.rememberCarouselState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VerticalCarouselSampleTheme {
                MainScreen()
            }
        }
    }
}

data class SampleItem(val id: Int, val color: Color, val label: String)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainScreen() {
    val carouselTypes = listOf(
        "Multi-Browse",
        "Hero",
        "Centered",
        "Uncontained",
        "M3 Vertical Pager (Reference)"
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val items = remember {
        List(100) { index ->
            SampleItem(
                id = index,
                color = Color(
                    red = (index * 50 % 255),
                    green = (index * 70 % 255),
                    blue = (index * 90 % 255)
                ),
                label = "Item $index"
            )
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Vertical Carousel Sample") })
                TabRow(selectedTabIndex = selectedTabIndex) {
                    carouselTypes.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    slideInHorizontally(animationSpec = tween(500)) { it } togetherWith
                            slideOutHorizontally(animationSpec = tween(500)) { -it }
                }, label = ""
            ) { targetPage ->
                when (targetPage) {
                    0 -> MultiBrowseCarouselDemo(items = items)
                    1 -> HeroCarouselDemo(items = items)
                    2 -> CenteredCarouselDemo(items = items)
                    3 -> UncontainedCarouselDemo(items = items)
                    4 -> M3VerticalPagerDemo(items = items)
                }
            }
        }
    }
}

@Composable
fun CarouselItemDisplay(item: SampleItem, modifier: Modifier = Modifier, itemHeight: Dp? = null) {
    Card(
        modifier = modifier
            .background(item.color)
            .border(2.dp, MaterialTheme.colorScheme.onSurface)
            .padding(16.dp)
            .let { if (itemHeight != null) it.height(itemHeight) else it.fillMaxSize() }, // Apply fixed height or fill based on context
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(item.label, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun MultiBrowseCarouselDemo(items: List<SampleItem>) {
    val state = rememberCarouselState(itemCount = { items.size })
    VerticalMultiBrowseCarousel(
        state = state,
        modifier = Modifier.fillMaxSize(),
        preferredItemHeight = 250.dp,
        itemSpacing = 8.dp
    ) { index ->
        CarouselItemDisplay(items[index])
    }
}

@Composable
fun HeroCarouselDemo(items: List<SampleItem>) {
    val state = rememberCarouselState(itemCount = { items.size })
    VerticalHeroCarousel(
        state = state,
        modifier = Modifier.fillMaxSize()
    ) { index ->
        CarouselItemDisplay(items[index])
    }
}

@Composable
fun CenteredCarouselDemo(items: List<SampleItem>) {
    val state = rememberCarouselState(itemCount = { items.size })
    VerticalCenteredCarousel(
        state = state,
        modifier = Modifier.fillMaxSize(),
        itemHeight = 200.dp,
        itemSpacing = 16.dp
    ) { index ->
        CarouselItemDisplay(items[index])
    }
}

@Composable
fun UncontainedCarouselDemo(items: List<SampleItem>) {
    val state = rememberCarouselState(itemCount = { items.size })
    VerticalUncontainedCarousel(
        state = state,
        modifier = Modifier.fillMaxSize(),
        itemHeight = 150.dp,
        itemSpacing = 4.dp
    ) { index ->
        CarouselItemDisplay(items[index])
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun M3VerticalPagerDemo(items: List<SampleItem>) {
    // This uses the standard M3 VerticalPager for comparison
    val pagerState =
        androidx.compose.foundation.pager.rememberPagerState(pageCount = { items.size })

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.pager.VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSize = androidx.compose.foundation.pager.PageSize.Fixed(250.dp),
            contentPadding = PaddingValues(top = 100.dp, bottom = 100.dp),
            pageSpacing = 8.dp
        ) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            val scale = lerp(1f, 0.75f, pageOffset.coerceIn(0f, 1f))
            val alpha = lerp(1f, 0.5f, pageOffset.coerceIn(0f, 1f))

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            ) {
                CarouselItemDisplay(items[page])
            }
        }
    }
}

// Helper function for linear interpolation, as it might be needed by M3 Pager demos
@Composable
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}