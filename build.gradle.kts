plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.hereliesaz.verticalcarousel"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
}

// This block is essential for JitPack to know what to publish.
publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.hereliesaz"
            artifactId = "VerticalCarousel"
            version = "1.0.0" // Start with a clean, new version

            // This tells the plugin to publish the release artifact of this library.
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
