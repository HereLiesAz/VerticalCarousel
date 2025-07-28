plugins {
    alias(libs.plugins.androidApplication) apply false
    id("com.android.library")
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.composeCompiler)
}
group = "com.github.HereLiesAz"
version = "0.5.1" 
android {
    namespace = "com.hereliesaz.verticalcarousel"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.hereliesaz"
            artifactId = "VerticalCarousel"
            version = "0.5.5" // Use a new version

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
}
