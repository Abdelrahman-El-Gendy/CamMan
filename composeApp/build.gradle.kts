import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.compose.resources.ResourcesExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleServices)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gndy.camman.resources"
    generateResClass = ResourcesExtension.ResourceClassGeneration.Always
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }

        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation(libs.navigation.compose)

            // DI - Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Network - Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // Kotlinx
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // Room Database
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // Image Loading - Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Firebase Auth (GitLive KMP SDK)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.common)

            // Image Picker
            implementation(libs.peekaboo.ui)
            implementation(libs.peekaboo.image.picker)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.gndy.camman"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gndy.camman"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        // Filter ABIs - only include 64-bit architectures
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }
    // ============== Signing Configuration ==============
    signingConfigs {
        create("release") {
            val props = project.rootProject.file("gradle.properties")
            if (props.exists()) {
                storeFile = file(project.property("RELEASE_STORE_FILE") as String)
                storePassword = project.property("RELEASE_STORE_PASSWORD") as String
                keyAlias = project.property("RELEASE_KEY_ALIAS") as String
                keyPassword = project.property("RELEASE_KEY_PASSWORD") as String
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
            excludes += "**/libimage_processing_util_jni.so"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // Enable build features
    buildFeatures {
        compose = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

// Exclude ML Kit dependencies for 16KB page size compatibility
// These libraries are not yet aligned for 16KB pages (required for Android 15+)
configurations.all {
    exclude(group = "com.google.mlkit", module = "image-labeling")
    exclude(group = "com.google.mlkit", module = "image-labeling-common")
    exclude(group = "com.google.mlkit", module = "vision-common")
    exclude(group = "com.google.mlkit", module = "common")
    exclude(group = "com.google.android.gms", module = "play-services-mlkit-image-labeling")
}
