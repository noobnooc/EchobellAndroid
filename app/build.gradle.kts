plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

if (file("google-services.json").isFile) {
    apply(plugin = "com.google.gms.google-services")
}

val debugApiBaseUrl = providers.gradleProperty("ECHOBELL_DEBUG_API_BASE_URL").orElse("https://dev.echobell.one")
val debugHookBaseUrl = providers.gradleProperty("ECHOBELL_DEBUG_HOOK_BASE_URL").orElse(debugApiBaseUrl)
val emailTriggerDomain = providers.gradleProperty("ECHOBELL_EMAIL_TRIGGER_DOMAIN").orElse("echobell.one")

android {
    namespace = "one.echobell.echobellandroid"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "one.echobell.echobellandroid"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"${debugApiBaseUrl.get()}\"")
            buildConfigField("String", "HOOK_BASE_URL", "\"${debugHookBaseUrl.get()}\"")
            buildConfigField("String", "EMAIL_TRIGGER_DOMAIN", "\"${emailTriggerDomain.get()}\"")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"https://api.echobell.one\"")
            buildConfigField("String", "HOOK_BASE_URL", "\"https://hook.echobell.one\"")
            buildConfigField("String", "EMAIL_TRIGGER_DOMAIN", "\"${emailTriggerDomain.get()}\"")
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
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.firebase.messaging)
    implementation(libs.google.billing)
    implementation(libs.gson)
    implementation(libs.okhttp)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
