plugins {
}

android {
    namespace = "com.hereliesaz.verticalcarouselsample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hereliesaz.verticalcarouselsample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

}