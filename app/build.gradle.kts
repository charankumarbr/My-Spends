plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "in.phoenix.myspends"
    compileSdk = 34

    defaultConfig {
        applicationId = "in.phoenix.myspends"
        minSdk = 29
        targetSdk = 34
        versionCode = 72
        versionName = "1.9.0"
        resourceConfigurations.addAll(listOf("en"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas".toString())
                argument("room.incremental", "true")
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            multiDexEnabled = true
            firebaseCrashlytics {
                // If you don't need crash reporting for your debug build,
                // you can speed up your build by disabling mapping file uploading.
                mappingFileUploadEnabled = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.flexbox)
    implementation(libs.xcardview)

    implementation(libs.xbrowser)

    implementation(libs.timberLog)

    implementation(libs.rxJava)
    implementation(libs.rxAndroid)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.realtime.db)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.performance)

    //implementation(libs.plugins.gms.google.services)

    implementation(libs.view.binding)

    implementation(libs.roomDb)
    implementation(libs.roomRuntime)
    implementation(libs.roomKotCorou)

    kapt(libs.room.compiler) {
        exclude(group = "com.intellij", module = "annotations")
    }

    implementation(libs.play.core)
    //implementation(libs.play.review)

    implementation(project(":ExpandableRecyclerview"))

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.roboelectric)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}