plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    targetHierarchy.default()

    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // put your multiplatform dependencies here
                implementation(project(":amphibians-api"))
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.kotlinx.coroutines.core)
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
                implementation("com.facebook.react:react-native:0.71.0-rc.0")
            }
        }
    }
}

android {
    namespace = "dev.yidafu.amphibinas"
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
    add("kspCommonMainMetadata", project(":amphibians-ksp"))
    add("kspAndroid", project(":amphibians-ksp"))
//    add("kspAndroidNativeX64Test", project(":amphibians-ksp"))
//    add("kspAndroidNativeArm64", project(":amphibians-ksp"))
//    add("kspAndroidNativeArm64Test", project(":amphibians-ksp"))
//    add("kspLinuxX64Test", project(":amphibians-ksp"))

    // The universal "ksp" configuration has performance issue and is deprecated on multiplatform since 1.0.1
    // ksp(project(":amphibians-ksp"))
}
