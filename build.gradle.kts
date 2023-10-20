plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.safeargs) apply false
    alias(libs.plugins.hilt) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}
