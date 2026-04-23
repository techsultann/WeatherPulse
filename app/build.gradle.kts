import java.io.FileInputStream
import java.util.Properties
import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val mockitoAgent = configurations.create("mockitoAgent")
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}


android {
    namespace = "com.techsultan.weatherapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.techsultan.weatherapp"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val baseUrl = localProperties.getProperty("WEATHER_BASE_URL") ?: "https://api.openweathermap.org/"
        val apiKey = localProperties.getProperty("API_KEY") ?: ""
        
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.withType(Test::class.java).configureEach {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Coroutine
    implementation(libs.kotlinx.coroutines.android)

    //hilt
    implementation(libs.hilt)
    implementation(libs.hilt.compose.navigation)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.hilt.android.compiler)

    // icon
    implementation(libs.androidx.material.icon)
    implementation(libs.androidx.material.icons.extended)

    //work manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    androidTestImplementation(libs.androidx.work.testing)

    // -- Navigation
    implementation(libs.navigation.compose)
    implementation(libs.navigation.ui.ktx)

    //Network
    implementation(libs.retrofit)
    implementation(libs.square.retrofit.gson)
    implementation(libs.logging.interceptor)
    testImplementation(libs.okhttp.test)

    //splash screen
    implementation(libs.core.splashscreen)

    //serialization
    implementation(libs.kotlinx.serialization)

    //room
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.coroutine)
    implementation(libs.androidx.room.paging)

    // test implementation
    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    mockitoAgent(libs.mockito.core) { isTransitive = false }

}
