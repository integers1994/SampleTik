plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.photex.tiktok"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.photex.tiktok"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(project(":toro-core"))
    implementation(project(":toro-mopub"))
    implementation(project(":catloadinglibrary"))
    implementation(project(":videotrimmer"))



    var  glideVersion = "4.9.0"



    var retrofit2 = "2.4.0"
    var  retrofit2ConverterGson = "2.4.0"



    var   okhttpVersion = "3.11.0"
    var   okhttpLoggingInterceptor = "3.11.0"
    var   okhttpUrlconnection = "2.7.5"
    var  okio = "2.0.0"


    var   gsonVersion = "2.8.5"


    var  circularImageView = "3.0.0"

    implementation ("pub.devrel:easypermissions:3.0.0")

    implementation ("com.google.android.exoplayer:exoplayer:r2.4.4")

    implementation ("com.writingminds:FFmpegAndroid:0.3.2")
    // to get ExoPlayer support

    implementation ("com.mindorks.android:prdownloader:0.5.0")
    implementation ("com.github.jlmd:AnimatedCircleLoadingView:1.1.5@aar")

    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth:17.0.0")

    // Google Sign In SDK (only required for Google Sign In)
    implementation ("com.google.android.gms:play-services-auth:16.0.1")
    // Firebase
    implementation ("com.google.firebase:firebase-core:16.0.9")
    implementation ("com.google.firebase:firebase-iid:18.0.0")
    implementation ("com.google.firebase:firebase-messaging:18.0.0")
    implementation ("com.google.firebase:firebase-dynamic-links:17.0.0")

    // Network
    implementation ("com.squareup.retrofit2:retrofit:$retrofit2")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofit2ConverterGson")
    implementation ("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation ("com.squareup.okhttp3:logging-interceptor:$okhttpLoggingInterceptor")
    implementation ("com.squareup.okhttp:okhttp-urlconnection:$okhttpUrlconnection")
    implementation ("com.google.code.gson:gson:$gsonVersion")
    implementation ("com.squareup.okio:okio:$okio")


    // RX java and RX android
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.0")
    //it is recommended to keep the same version of rxAndroid
    implementation ("io.reactivex.rxjava2:rxjava:2.2.2")


    // Others
//    implementation ("com.github.bumptech.glide:glide:3.7.0")
    implementation ("com.github.bumptech.glide:glide:$glideVersion")
    implementation ("de.hdodenhof:circleimageview:$circularImageView")
    implementation ("com.anupcowkur:reservoir:3.1.0")

    //noinspection GradleDependency
    implementation ("com.otaliastudios:cameraview:2.0.0")
    implementation ("net.alhazmy13.Gota:libary:1.4.1")
    // For developers using AndroidX in their applications
    implementation ("pub.devrel:easypermissions:3.0.0")

    implementation ("com.googlecode.mp4parser:isoparser:1.1.22")
    implementation ("com.github.yalantis:ucrop:2.2.3-native")
    implementation ("com.github.ybq:Android-SpinKit:1.2.0")
    implementation ("com.wang.avi:library:2.1.3")
    implementation ("com.akexorcist:RoundCornerProgressBar:2.0.3")
    implementation ("com.pnikosis:materialish-progress:1.7")
}