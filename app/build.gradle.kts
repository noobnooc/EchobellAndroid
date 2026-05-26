import java.io.File
import java.util.Properties
import org.gradle.api.GradleException

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
val localSigningPropertiesFile = rootProject.file(".local-signing/release.properties")
val localSigningProperties = Properties().apply {
    if (localSigningPropertiesFile.isFile) {
        localSigningPropertiesFile.inputStream().use(::load)
    }
}

fun signingValue(name: String, localName: String) =
    providers.gradleProperty(name)
        .orElse(providers.environmentVariable(name))
        .orElse(providers.provider { localSigningProperties.getProperty(localName) })

val releaseStoreFile = signingValue("ECHOBELL_RELEASE_STORE_FILE", "storeFile")
val releaseStorePassword = signingValue("ECHOBELL_RELEASE_STORE_PASSWORD", "storePassword")
val releaseKeyAlias = signingValue("ECHOBELL_RELEASE_KEY_ALIAS", "keyAlias")
val releaseKeyPassword = signingValue("ECHOBELL_RELEASE_KEY_PASSWORD", "keyPassword")
val hasReleaseSigning = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { it.isPresent }

fun resolvedReleaseStoreFile(): File {
    val configuredStoreFile = File(releaseStoreFile.get())
    return if (configuredStoreFile.isAbsolute) configuredStoreFile else rootProject.file(configuredStoreFile.path)
}

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
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = resolvedReleaseStoreFile()
                storePassword = releaseStorePassword.get()
                keyAlias = releaseKeyAlias.get()
                keyPassword = releaseKeyPassword.get()
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"${debugApiBaseUrl.get()}\"")
            buildConfigField("String", "HOOK_BASE_URL", "\"${debugHookBaseUrl.get()}\"")
            buildConfigField("String", "EMAIL_TRIGGER_DOMAIN", "\"${emailTriggerDomain.get()}\"")
        }
        release {
            isMinifyEnabled = false
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            buildConfigField("String", "API_BASE_URL", "\"https://api.echobell.one\"")
            buildConfigField("String", "HOOK_BASE_URL", "\"https://hook.echobell.one\"")
            buildConfigField("String", "EMAIL_TRIGGER_DOMAIN", "\"${emailTriggerDomain.get()}\"")
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
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

tasks.register("checkReleaseSigning") {
    group = "verification"
    description = "Checks that release upload signing credentials are configured for Play publishing."

    doLast {
        val missing = listOf(
            "storeFile" to releaseStoreFile,
            "storePassword" to releaseStorePassword,
            "keyAlias" to releaseKeyAlias,
            "keyPassword" to releaseKeyPassword,
        ).filterNot { (_, provider) -> provider.isPresent }

        if (missing.isNotEmpty()) {
            throw GradleException(
                "Missing release signing values: ${missing.joinToString { it.first }}. " +
                    "Set them in ${localSigningPropertiesFile.path}, Gradle properties, or environment variables before publishing.",
            )
        }

        val store = resolvedReleaseStoreFile()
        if (!store.isFile) {
            throw GradleException("Release keystore does not exist: ${store.absolutePath}")
        }
    }
}

tasks.register("checkPublishRelease") {
    group = "verification"
    description = "Runs the release checks expected before uploading the Android App Bundle to Google Play."
    dependsOn("checkReleaseSigning", "lintRelease", "bundleRelease")
}

tasks.matching { it.name == "lintRelease" }.configureEach {
    mustRunAfter("checkReleaseSigning")
}

tasks.matching { it.name == "bundleRelease" }.configureEach {
    mustRunAfter("lintRelease")
}
