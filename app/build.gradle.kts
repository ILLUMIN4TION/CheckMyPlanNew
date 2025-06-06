plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.a0401chkmyplan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.a0401chkmyplan"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding {
        enable = true
    }

    dependencies {
        implementation ("com.google.android.libraries.places:places:3.3.0")

        implementation ("androidx.room:room-runtime:2.7.1")
        kapt ("androidx.room:room-compiler:2.7.1")
        implementation ("androidx.room:room-ktx:2.7.1")

        implementation ("com.google.android.gms:play-services-maps:18.2.0")

        //Navigation draw를 사용하기위함
        implementation ("com.google.android.material:material:1.9.0")
        // drawerlayout
        implementation ("androidx.drawerlayout:drawerlayout:1.2.0")

        implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
        implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }
}
dependencies {
    implementation ("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
}
