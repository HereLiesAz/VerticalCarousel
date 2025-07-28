plugins {
    id("com.android.library") version "8.11.1"
    id("org.jetbrains.kotlin.android") version "2.2.0"
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" id()

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


}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation(platform("androidx.compose:compose-bom:2025.07.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-unit")
    testImplementation(kotlin("test"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")

}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.hereliesaz"
            artifactId = "VerticalCarousel"
            version = "0.7.86"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
