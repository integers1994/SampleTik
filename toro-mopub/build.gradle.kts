plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "im.ene.toro.mopub"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(project(":toro-core"))


    // For native video. This will automatically also include native static
    implementation("com.mopub:mopub-sdk-native-video:4.20.0@aar")

    /*  implementation("com.google.android.exoplayer:exoplayer:r2.4.4") {
        exclude group: 'com.android.support'
      }*/
    implementation ("com.google.android.exoplayer:exoplayer:r2.4.4")

    implementation ("net.butterflytv.utils:rtmp-client:3.0.1")
}