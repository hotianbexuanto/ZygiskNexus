import com.android.build.gradle.LibraryExtension
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.agp.lib) apply false
}

fun String.execute(currentWorkingDir: File = file("./")): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

val gitCommitCount = "git rev-list HEAD --count".execute().toInt()
val gitCommitHash = "git rev-parse --verify --short HEAD".execute()

val moduleId by extra("zygisknexus")
val moduleName by extra("Zygisk Nexus")
val verName by extra("v4-0.9.1.1")
val verCode by extra("497")
val commitHash by extra(gitCommitHash)
val minKsuVersion by extra(10940)
val minKsudVersion by extra(11425)
val maxKsuVersion by extra(20000)
val minMagiskVersion by extra(26402)

val androidMinSdkVersion by extra(24)
val androidTargetSdkVersion by extra(34)
val androidCompileSdkVersion by extra(34)
val androidBuildToolsVersion by extra("34.0.0")
val androidCompileNdkVersion by extra("26.0.10792818")
val androidSourceCompatibility by extra(JavaVersion.VERSION_17)
val androidTargetCompatibility by extra(JavaVersion.VERSION_17)

tasks.register("Delete", Delete::class) {
    delete(rootProject.buildDir)
}

fun Project.configureBaseExtension() {
    extensions.findByType(LibraryExtension::class)?.run {
        namespace = "com.github.zygisknexus"
        compileSdk = androidCompileSdkVersion
        ndkVersion = androidCompileNdkVersion
        buildToolsVersion = androidBuildToolsVersion

        defaultConfig {
            minSdk = androidMinSdkVersion
            targetSdk = androidTargetSdkVersion
            testOptions {
                targetSdk = androidTargetSdkVersion
            }
            lint {
                targetSdk = androidTargetSdkVersion
            }
        }

        lint {
            abortOnError = true
        }

        buildFeatures {
            buildConfig = true
        }

        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }

        compileOptions {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }

        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

subprojects {
    plugins.withId("com.android.library") {
        configureBaseExtension()
    }
}

tasks.register("generateLinkerWrapper") {
    doLast {
        val outputDir = layout.buildDirectory.dir("generated/linker").get().asFile
        // ... existing code ...
    }
}
