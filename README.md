# VerticalCarousel for Jetpack Compose
[![](https://jitpack.io/v/HereLiesAz/VerticalCarousel.svg)](https://jitpack.io/#HereLiesAz/VerticalCarousel)

A library providing the vertical Material 3 carousels that Google hasn't yet. This provides a suite of vertical carousel components that mirror the API and behavior of the official horizontal M3 carousels, built around a spec-compliant Keyline-based engine.

## Installation

To add this library to your project, add the following to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

Then, add the dependency to your app's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation('com.github.hereliesaz.VerticalCarousel:verticalcarousel:0.7.8')
}
```

## Usage

Each carousel is a composable that takes a `CarouselState` and a content lambda that provides a `CarouselItemScope`.

### rememberCarouselState

All carousels require a `CarouselState`.

```kotlin
import com.hereliesaz.verticalcarousel.state.rememberCarouselState

val state = rememberCarouselState(itemCount = { /* ... */ })
```

### VerticalMultiBrowseCarousel

A carousel that shows smaller, de-emphasized items above and below the main, focused item.

```kotlin
import com.hereliesaz.verticalcarousel.component.VerticalMultiBrowseCarousel

VerticalMultiBrowseCarousel(
    state = state,
    preferredItemHeight = 250.dp,
    itemSpacing = 8.dp
) { itemIndex ->
    // Your item content, using carouselItemInfo from the scope
    // ex: Modifier.maskClip(MaterialTheme.shapes.extraLarge)
}
```

### VerticalHeroCarousel

A simple, full-height carousel for displaying one large item at a time. Ideal for hero images or full-screen content.

```kotlin
import com.hereliesaz.verticalcarousel.component.VerticalHeroCarousel

VerticalHeroCarousel(state = state) { itemIndex ->
    // Your item content
}
```

### VerticalUncontainedCarousel

A carousel with fixed-size items that are not masked by the container and scroll freely.

```kotlin
import com.hereliesaz.verticalcarousel.component.VerticalUncontainedCarousel

VerticalUncontainedCarousel(
    state = state,
    itemHeight = 200.dp,
    itemSpacing = 8.dp
) { itemIndex ->
    // Your item content
}
```


### VerticalCenteredCarousel

A carousel that keeps the current item centered in the viewport, allowing portions of the previous and next items to be visible.

```kotlin
import com.hereliesaz.verticalcarousel.component.VerticalCenteredCarousel

VerticalCenteredCarousel(
    state = state,
    itemHeight = 200.dp,
    itemSpacing = 8.dp,
    contentPadding = PaddingValues(vertical = 32.dp)
) { itemIndex ->
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

