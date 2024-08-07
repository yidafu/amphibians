buildscript {
    extra.apply {
        set("buildToolsVersion", "34.0.0")
        set("minSdkVersion", "23")
        set("compileSdkVersion", "34")
        set("targetSdkVersion", "34")
        set("ndkVersion", "26.1.10909125")
        set("kotlinVersion", "2.0.0")
    }
    repositories {
        maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        google()
        mavenCentral()
    }
    dependencies {

        getAllprojects().forEach {
            println("project name ===> ${it.displayName}")
        }
        classpath("com.facebook.react:react-native-gradle-plugin")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin")
        classpath("com.android.tools.build:gradle:${libs.versions.agp}")
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ktlint) apply false
}

apply(plugin = "com.facebook.react.rootproject")

allprojects {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven("https://s01.oss.sonatype.org/content/repositories/releases/")
        google()
        mavenCentral()
    }
}
