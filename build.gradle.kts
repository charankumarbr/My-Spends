// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.android.library) apply false
}