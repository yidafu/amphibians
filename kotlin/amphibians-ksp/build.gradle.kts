val kspVersion: String by project

plugins {
    alias(libs.plugins.kotlinJvm)
//    alias(libs.plugins.kotlinMultiplatform)

    id("amphibians.kotlin-library-conventions")
}

group = "dev.yidafu.amphibians"
version = libs.versions.ampbibians

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
}

dependencies {

    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.ksp.api)
    implementation(libs.outfoxx.typescriptpoet)

//    implementation(libs.amphibians.api)
    implementation(project(":amphibians-api"))
}

repositories {
    mavenLocal()
}

// kotlin {
//    jvm()
//
//    sourceSets {
//        val commonMain by getting {
//            dependencies {
//                // put your multiplatform dependencies here
//            }
//        }
//        val commonTest by getting {
//            dependencies {
//                implementation(libs.kotlin.test)
//            }
//        }
//
//        sourceSets {
//            val jvmMain by getting {
//                dependencies {
//                    implementation(libs.kotlinpoet)
//                    implementation(libs.kotlinpoet.ksp)
//                    implementation(libs.ksp.api)
//                    implementation(project(":amphibians-api"))
//                }
//                kotlin.srcDir("src/main/kotlin")
//                resources.srcDir("src/main/resources")
//            }
//        }
//    }
// }

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(tasks.kotlinSourcesJar)
            pom {
                name.set("amphibians-ksp")
                description.set("Amphibians: Native API Generator. React Native Plugin And WebView JSAPI Generator")
            }
        }
    }
}
