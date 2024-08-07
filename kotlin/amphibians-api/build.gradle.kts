plugins {
    alias(libs.plugins.kotlinMultiplatform)

    id("amphibians.kotlin-library-conventions")
}

group = "dev.yidafu.amphibians"
version = libs.versions.ampbibians

kotlin {

    jvm()
//
//    macosX64()
//    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

// publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            version = "1.0.0"
//
//            from(components["kotlin"])
//
//            pom {
//                name.set("amphibians-api")
//                description.set("amphibians api")
//            }
//        }
//    }
// }
