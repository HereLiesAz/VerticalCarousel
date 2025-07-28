plugins {
    id("com.android.library") version "8.2.2"
    id("org.jetbrains.kotlin.android") version "1.9.22"
    id("maven-publish")
}

android {
    namespace = "com.hereliesaz.verticalcarousel"
    compileSdk = 34

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
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
}

// This block explicitly defines the library's coordinates for publishing.
publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.hereliesaz"
            artifactId = "VerticalCarousel"
            version = "0.6.1" 

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
