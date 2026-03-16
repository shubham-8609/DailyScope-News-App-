plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply true
}

android {
    namespace = "com.codeleg.dailyscope"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.codeleg.dailyscope"
        minSdk = 28
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    /* -------------------- Core -------------------- */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    /* -------------------- Coroutines -------------------- */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    /* -------------------- Retrofit -------------------- */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    /* -------------------- OkHttp -------------------- */
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    /* -------------------- Glide -------------------- */
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    /* -------------------- Navigation -------------------- */
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    /* -------------------- Preferences -------------------- */
    implementation(libs.androidx.preference)
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    /* -------------------- UI Utils -------------------- */
    implementation("com.intuit.sdp:sdp-android:1.1.1")

    /* -------------------- Room -------------------- */
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")

    /* -------------------- Paging 3 -------------------- */
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.room:room-paging:2.6.1")

    /* -------------------- UI Components -------------------- */
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.airbnb.android:lottie:6.7.1")

    /* -------------------- Testing -------------------- */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.fragment)
}