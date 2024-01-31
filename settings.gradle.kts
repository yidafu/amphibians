/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 * For more detailed information on multi-project builds, please refer to https://docs.gradle.org/8.3/userguide/building_swift_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */

pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")

        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "amphibians"
include(":AmphibiansApp")
include(":library")
include(":amphibians-webview")
