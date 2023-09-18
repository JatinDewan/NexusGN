buildscript{

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
    }

    repositories {
        mavenCentral()
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("org.jetbrains.kotlin.kapt") version "1.8.21" apply false
}