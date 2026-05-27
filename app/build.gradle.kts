import java.io.File
import java.util.Properties
import javax.imageio.ImageIO
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
val playBillingCatalogFile = project.file("src/main/play/billing-products.properties")
val playListingDir = project.file("src/main/play")
val localSigningProperties = Properties().apply {
    if (localSigningPropertiesFile.isFile) {
        localSigningPropertiesFile.inputStream().use(::load)
    }
}
val playBillingCatalogProperties = Properties().apply {
    if (playBillingCatalogFile.isFile) {
        playBillingCatalogFile.inputStream().use(::load)
    }
}
val playAssetAltTextFile = playListingDir.resolve("asset-alt-text.properties")
val playAssetAltTextProperties = Properties().apply {
    if (playAssetAltTextFile.isFile) {
        playAssetAltTextFile.inputStream().use(::load)
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

fun playBillingCatalogValue(name: String): String =
    playBillingCatalogProperties.getProperty(name)?.trim()?.takeIf { it.isNotBlank() }
        ?: throw GradleException("Missing Play Billing catalog value '$name' in ${playBillingCatalogFile.path}.")

val playBillingPackageName = playBillingCatalogValue("packageName")
val monthlySubscriptionProductId = playBillingCatalogValue("monthlyProductId")
val annualSubscriptionProductId = playBillingCatalogValue("annualProductId")
val monthlySubscriptionBasePlanId = playBillingCatalogValue("monthlyBasePlanId")
val annualSubscriptionBasePlanId = playBillingCatalogValue("annualBasePlanId")

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
        versionCode = 4
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GOOGLE_PLAY_PACKAGE_NAME", "\"$playBillingPackageName\"")
        buildConfigField("String", "GOOGLE_PLAY_MONTHLY_SUBSCRIPTION_ID", "\"$monthlySubscriptionProductId\"")
        buildConfigField("String", "GOOGLE_PLAY_ANNUAL_SUBSCRIPTION_ID", "\"$annualSubscriptionProductId\"")
        buildConfigField("String", "GOOGLE_PLAY_MONTHLY_BASE_PLAN_ID", "\"$monthlySubscriptionBasePlanId\"")
        buildConfigField("String", "GOOGLE_PLAY_ANNUAL_BASE_PLAN_ID", "\"$annualSubscriptionBasePlanId\"")
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

tasks.register("checkPlayBillingCatalog") {
    group = "verification"
    description = "Checks local Play Billing catalog metadata needed for Google Play subscription publishing."

    doLast {
        if (!playBillingCatalogFile.isFile) {
            throw GradleException("Missing Play Billing catalog file: ${playBillingCatalogFile.path}")
        }

        val productIdPattern = Regex("^[a-z0-9][a-z0-9_.]{0,39}$")
        val basePlanIdPattern = Regex("^[a-z0-9][a-z0-9-]*$")
        val expectedPackage = android.defaultConfig.applicationId

        if (playBillingPackageName != expectedPackage) {
            throw GradleException("Play Billing packageName must match applicationId '$expectedPackage'.")
        }

        listOf(
            "monthlyProductId" to monthlySubscriptionProductId,
            "annualProductId" to annualSubscriptionProductId,
        ).forEach { (name, value) ->
            if (!productIdPattern.matches(value)) {
                throw GradleException("$name '$value' is not a valid Google Play subscription product ID.")
            }
        }

        listOf("monthlyBasePlanId", "annualBasePlanId").forEach { name ->
            val value = playBillingCatalogValue(name)
            if (!basePlanIdPattern.matches(value)) {
                throw GradleException("$name '$value' is not a valid Google Play base plan ID.")
            }
        }

        val periods = mapOf(
            "monthlyBillingPeriod" to "P1M",
            "annualBillingPeriod" to "P1Y",
        )
        periods.forEach { (name, expected) ->
            val value = playBillingCatalogValue(name)
            if (value != expected) {
                throw GradleException("$name must be '$expected' for the current Android paywall copy and sorting.")
            }
        }
    }
}

tasks.register("checkPlayListingAssets") {
    group = "verification"
    description = "Checks local Google Play listing text and graphic assets before publishing."

    doLast {
        fun requireFile(file: File, label: String) {
            if (!file.isFile) {
                throw GradleException("Missing Google Play listing asset: $label at ${file.path}")
            }
        }

        fun readImage(file: File, label: String) =
            ImageIO.read(file) ?: throw GradleException("$label must be a readable PNG or JPEG image: ${file.path}")

        fun requireExtension(file: File, label: String, allowed: Set<String>) {
            val extension = file.extension.lowercase()
            if (extension !in allowed) {
                throw GradleException("$label must use one of ${allowed.joinToString()} formats: ${file.path}")
            }
        }

        fun requireTextLength(file: File, label: String, min: Int, max: Int) {
            requireFile(file, label)
            val text = file.readText().trim()
            val length = text.codePointCount(0, text.length)
            if (length !in min..max) {
                throw GradleException("$label must be $min-$max characters; found $length in ${file.path}.")
            }
        }

        fun requireAltText(key: String, label: String) {
            requireFile(playAssetAltTextFile, "Play listing asset alt text")
            val text = playAssetAltTextProperties.getProperty(key)?.trim().orEmpty()
            val length = text.codePointCount(0, text.length)
            if (length !in 1..140) {
                throw GradleException("$label alt text must be 1-140 characters; found $length for '$key'.")
            }
        }

        val listingIcon = playListingDir.resolve("listing-icon.png")
        requireFile(listingIcon, "High-res app icon")
        requireExtension(listingIcon, "High-res app icon", setOf("png"))
        val iconImage = readImage(listingIcon, "High-res app icon")
        if (iconImage.width != 512 || iconImage.height != 512) {
            throw GradleException("High-res app icon must be 512x512 px; found ${iconImage.width}x${iconImage.height}.")
        }
        if (!iconImage.colorModel.hasAlpha()) {
            throw GradleException("High-res app icon must be a 32-bit PNG with alpha.")
        }
        if (listingIcon.length() > 1024L * 1024L) {
            throw GradleException("High-res app icon must be at most 1024 KB; found ${listingIcon.length()} bytes.")
        }
        requireAltText("listingIcon", "High-res app icon")

        val featureGraphic = playListingDir.resolve("feature-graphic.jpg")
        requireFile(featureGraphic, "Feature graphic")
        requireExtension(featureGraphic, "Feature graphic", setOf("jpg", "jpeg", "png"))
        val featureImage = readImage(featureGraphic, "Feature graphic")
        if (featureImage.width != 1024 || featureImage.height != 500) {
            throw GradleException("Feature graphic must be 1024x500 px; found ${featureImage.width}x${featureImage.height}.")
        }
        if (featureImage.colorModel.hasAlpha()) {
            throw GradleException("Feature graphic must be JPEG or 24-bit PNG without alpha.")
        }
        requireAltText("featureGraphic", "Feature graphic")

        val screenshots = playListingDir.resolve("screenshots")
            .listFiles { file -> file.isFile && file.extension.lowercase() in setOf("jpg", "jpeg", "png") }
            ?.sortedBy { it.name }
            .orEmpty()
        if (screenshots.size !in 2..8) {
            throw GradleException("Google Play listing must include 2-8 phone screenshots; found ${screenshots.size}.")
        }
        screenshots.forEach { screenshot ->
            val image = readImage(screenshot, "Screenshot ${screenshot.name}")
            val minDimension = minOf(image.width, image.height)
            val maxDimension = maxOf(image.width, image.height)
            if (minDimension < 320 || maxDimension > 3840 || maxDimension > minDimension * 2) {
                throw GradleException(
                    "Screenshot ${screenshot.name} must be 320-3840 px with max side no more than twice min side; " +
                        "found ${image.width}x${image.height}.",
                )
            }
            if (image.colorModel.hasAlpha()) {
                throw GradleException("Screenshot ${screenshot.name} must be JPEG or 24-bit PNG without alpha.")
            }
            requireAltText(screenshot.nameWithoutExtension, "Screenshot ${screenshot.name}")
        }

        val listingTextDir = playListingDir.resolve("listings/en-US")
        requireTextLength(listingTextDir.resolve("title.txt"), "Store listing title", 1, 30)
        requireTextLength(listingTextDir.resolve("short-description.txt"), "Store listing short description", 1, 80)
        requireTextLength(listingTextDir.resolve("full-description.txt"), "Store listing full description", 1, 4000)

        val releaseNotes = playListingDir.resolve("release-notes/en-US/default.txt")
        requireTextLength(releaseNotes, "Release notes", 1, 500)
    }
}

tasks.register("checkPublishRelease") {
    group = "verification"
    description = "Runs the release checks expected before uploading the Android App Bundle to Google Play."
    dependsOn("checkReleaseSigning", "checkPlayBillingCatalog", "checkPlayListingAssets", "lintRelease", "bundleRelease")
}

tasks.matching { it.name == "lintRelease" }.configureEach {
    mustRunAfter("checkReleaseSigning", "checkPlayBillingCatalog", "checkPlayListingAssets")
}

tasks.matching { it.name == "bundleRelease" }.configureEach {
    mustRunAfter("lintRelease")
}
