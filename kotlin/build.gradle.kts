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
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.nexus.publish) apply false
    id("amphibians.kotlin-common-conventions")
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:${libs.versions.agp}")

        classpath(libs.publish.plugin)
    }
}

allprojects {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") } // Required to download KtLint
        mavenCentral()
        maven { setUrl("https://s01.oss.sonatype.org/content/repositories/releases/") }
        mavenLocal()
    }
}
