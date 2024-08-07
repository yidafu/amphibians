import groovy.lang.Closure



pluginManagement {
    // Include 'plugins build' to define convention plugins.
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")

        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "exmpale"

// applyNativeModulesSettingsGradle(settings)
apply(from = file("../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"))
val applyNativeModulesSettingsGradle = extra.get("applyNativeModulesSettingsGradle") as Closure<*>
applyNativeModulesSettingsGradle(settings)

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

includeBuild("../../kotlin")
include(":app")
include(":rn-plugin")

includeBuild("../node_modules/@react-native/gradle-plugin")
