plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 33

    namespace = "com.dewerro.measurer"

    defaultConfig {
        applicationId = "com.dewerro.measurer"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

val androidx_core_version = "1.16.0"
val appcompat_version = "1.7.1"
val material_version = "1.12.0"
val constraintlayout_version = "2.2.1"
val navigation_fragment_version = "2.9.0"
val navigation_ui_version = "2.9.0"
val junit_version = "4.13.2"
val android_junit_version = "1.2.1"
val espresso_core_version = "3.6.1"
val firebase_auth_version = "23.2.1"
val firebase_firestore_version = "25.1.4"
val ar_sceneform_ux_version = "1.17.1"
val ar_sceneform_core_version = "1.17.1"
val ar_core_version = "1.49.0"

dependencies {
    // Default dependencies
    implementation("androidx.core:core-ktx:$androidx_core_version")
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("com.google.android.material:material:$material_version")
    implementation("androidx.constraintlayout:constraintlayout:$constraintlayout_version")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigation_fragment_version")
    implementation("androidx.navigation:navigation-ui-ktx:$navigation_ui_version")
    testImplementation("junit:junit:$junit_version")
    androidTestImplementation("androidx.test.ext:junit:$android_junit_version")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espresso_core_version")

    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    // AR Core
    implementation("com.google.ar.sceneform.ux:sceneform-ux:$ar_sceneform_ux_version")
    implementation("com.google.ar.sceneform:core:$ar_sceneform_core_version")
    implementation("com.google.ar:core:$ar_core_version")
}