plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "id.demo.googledicecontroller"
    compileSdk = 35
    defaultConfig {
        applicationId = "id.demo.googledicecontroller"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
