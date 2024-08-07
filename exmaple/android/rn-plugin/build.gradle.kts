plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    id("com.facebook.react")
}

kotlin {
    targetHierarchy.default()

    androidTarget {
        publishLibraryVariants("release")
//        namespace = "dev.yidafu.amphibians.calculator"
        compilations.all {
            kotlinOptions {
//                jvmTarget = "1.8"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            repositories {
                mavenLocal()
            }
            dependencies {
                // put your multiplatform dependencies here
//                implementation(project(":kotlin:amphibians-api"))
                implementation(libs.amphibians.api)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.startup.runtime)
                implementation("com.facebook.react:react-native:+")
            }
        }
    }
}

android {
    namespace = "dev.yidafu.amphibians.sample"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
}

dependencies {
    add("kspCommonMainMetadata", "dev.yidafu.amphibians:amphibians-ksp:${libs.versions.ampbibians}")
    add("kspAndroid", "dev.yidafu.amphibians:amphibians-ksp:${libs.versions.ampbibians}")
//    add("kspAndroidNativeX64Test", project(":amphibians-ksp"))
//    add("kspAndroidNativeArm64", project(":amphibians-ksp"))
//    add("kspAndroidNativeArm64Test", project(":amphibians-ksp"))
//    add("kspLinuxX64Test", project(":amphibians-ksp"))

    // The universal "ksp" configuration has performance issue and is deprecated on multiplatform since 1.0.1
     ksp(libs.amphibians.ksp)
}

repositories {
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/releases/")

//    maven {
//        // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
//        setUrl("$projectDir/../node_modules/react-native/android")
//    }
}
