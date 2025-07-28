# VerticalCarousel for Jetpack Compose

A library providing the vertical Material 3 carousels that Google hasn't yet. This provides a suite of vertical carousel components that mirror the API and behavior of the official horizontal M3 carousels.

## Installation

To add this library to your project, add the following to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Then, add the dependency to your app's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation('com.github.hereliesaz.VerticalCarousel:verticalcarousel:0.5.0')
}
```

## Usage

Each carousel is a composable that takes a `PagerState` and content.

### VerticalMultiBrowseCarousel

A carousel that shows smaller, de-emphasized items above and below the main, focused item.

```kotlin
import com.hereliesaz.verticalcarousel.VerticalMultiBrowseCarousel

val pagerState = rememberPagerState { pageCount }
VerticalMultiBrowseCarousel(
    state = pagerState,
    preferredItemHeight = 450.dp,
    contentPadding = PaddingValues(vertical = 120.dp),
    itemSpacing = 16.dp
) { page ->
    // Your item content
}
```

### VerticalUncontainedCarousel

A carousel with fixed-size items that are not masked by the container.

```kotlin
import com.hereliesaz.verticalcarousel.VerticalUncontainedCarousel

val pagerState = rememberPagerState { pageCount }
VerticalUncontainedCarousel(
    state = pagerState,
    itemHeight = 200.dp,
    itemSpacing = 8.dp
) { page ->
    // Your item content
}
```

### VerticalHeroCarousel

A simple, full-height carousel for displaying one large item at a time.

```kotlin
import com.hereliesaz.verticalcarousel.VerticalHeroCarousel

val pagerState = rememberPagerState { pageCount }
VerticalHeroCarousel(state = pagerState) { page ->
    // Your item content
}
```

### VerticalMultiBrowseCenteredCarousel

A centered variant of the multi-browse carousel.

```kotlin
import com.hereliesaz.verticalcarousel.VerticalMultiBrowseCenteredCarousel

val pagerState = rememberPagerState { pageCount }
VerticalMultiBrowseCenteredCarousel(
    state = pagerState,
    preferredItemHeight = 450.dp,
    contentPadding = PaddingValues(vertical = 120.dp),
    itemSpacing = 16.dp
) { page ->
    // Your item content
}
```
