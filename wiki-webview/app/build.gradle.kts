plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinKsp)
    id("kotlin-kapt")
    alias(libs.plugins.daggerHiltAndroid)
    alias(libs.plugins.protoBufPlugin)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "work.wander.wikiview"
    compileSdk = 34

    defaultConfig {
        applicationId = "work.wander.wikiview"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kapt {
        //TODO: Remove this once https://issuetracker.google.com/issues/202825622 is fixed
        generateStubs = true
        correctErrorTypes = true
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        // Added to resolve build error involving multiple TypeConverter declarations (https://github.com/google/ksp/issues/1700)
        arg("room.generateKotlin", "true")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.nav.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.dagger.android)
    implementation(libs.dagger.android.support)
    kapt(libs.dagger.android.processor)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Proto DataStore
    implementation(libs.proto.datastore)
    implementation(libs.proto.javalite)
    implementation(libs.proto.kotlinlite)

    // Logging/Timber
    implementation(libs.timber)

    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)
    androidTestImplementation(libs.androidx.work.testing)

    // Kotlin Serialization
    implementation(libs.kotlin.serialization.json)

    // OkHttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    testImplementation(libs.okhttp.mockwebserver)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.serialization)
    implementation(libs.retrofit.scalars)

    // Picasso
    implementation(libs.picasso)

    // Coil
    implementation(libs.coil.core)
    implementation(libs.coil.compose)

    // Google Fonts
    implementation(libs.androidx.ui.text.google.fonts)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // Material Extended Icons
    implementation(libs.material.icons.extended)

    // Adaptive Material 3
    implementation(libs.androidx.material3.adaptive.navigation.suite.core)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.androidx.material3.adaptive.core)
    implementation(libs.androidx.material3.adaptive.android)
    implementation(libs.androidx.material3.adaptive.navigation)

    // Testing Core
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Truth
    testImplementation(libs.truth.assert)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.18.0"
    }

    // Generates the java (and kotlin) Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().forEach() { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}