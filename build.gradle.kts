plugins {
    kotlin("android") version "2.1.0" apply false
    kotlin("kapt") version "2.1.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("com.android.application") version "8.10.0" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}
