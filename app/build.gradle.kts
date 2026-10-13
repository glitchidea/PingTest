import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.pingtest.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pingtest.app"
        minSdk = 24
        targetSdk = 36
        // Sürüm bilgisi; CI/CD `-PversionNameOverride` / `-PversionCodeOverride`
        // ile etiketten (tag) geçersiz kılabilir. Varsayılan: 1.0.0.1
        val vName = (project.findProperty("versionNameOverride") as? String)
            ?.takeIf { it.isNotBlank() } ?: "1.0.0.1"
        val vCode = (project.findProperty("versionCodeOverride") as? String)
            ?.toIntOrNull() ?: 2
        versionCode = vCode
        versionName = vName
        vectorDrawables { useSupportLibrary = true }
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

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")

    val compose = "1.7.6"
    implementation("androidx.compose.ui:ui:$compose")
    implementation("androidx.compose.ui:ui-graphics:$compose")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose")
    implementation("androidx.compose.foundation:foundation:$compose")
    implementation("androidx.compose.material:material-icons-core:$compose")
    implementation("androidx.compose.material3:material3:1.3.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    debugImplementation("androidx.compose.ui:ui-tooling:$compose")
}
