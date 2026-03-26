plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.calcmate.scientificcalculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.calcmate.scientificcalculator"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
    }
}

// Force-upgrade Espresso pulled transitively by compose-ui-test-junit4.
// Espresso <= 3.6.1 crashes on Android 16 (API 36) with:
//   NoSuchMethodException: android.hardware.input.InputManager.getInstance []
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.test.espresso") {
            useVersion("3.7.0")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.ui.text.google.fonts)

    // Navigation
    implementation(libs.navigation.compose)

    // Lifecycle / ViewModel
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // AdMob
    implementation(libs.play.services.ads)

    // Google Play Billing
    implementation(libs.billing.ktx)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.test.ext.junit)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
