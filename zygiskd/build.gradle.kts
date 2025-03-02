plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.rust.android)
}

val minKsuVersion = rootProject.extra["minKsuVersion"].toString().toInt()
val maxKsuVersion = rootProject.extra["maxKsuVersion"].toString().toInt()
val minMagiskVersion = rootProject.extra["minMagiskVersion"].toString().toInt()
val verCode = rootProject.extra["verCode"].toString().toInt()
val verName: String by rootProject.extra
val commitHash: String by rootProject.extra

android.buildFeatures {
    androidResources = false
    buildConfig = false
}

cargo {
    module = "."
    libname = "zygiskd"
    targetIncludes = arrayOf("zygiskd")
    targets = listOf("arm64", "arm", "x86", "x86_64")
    targetDirectory = "build/intermediates/rust"
    val isDebug = gradle.startParameter.taskNames.any { it.toLowerCase().contains("debug") }
    profile = if (isDebug) "debug" else "release"
    exec = { spec, _ ->
        spec.environment("ANDROID_NDK_HOME", android.ndkDirectory.path)
        spec.environment("MIN_KSU_VERSION", minKsuVersion)
        spec.environment("MAX_KSU_VERSION", maxKsuVersion)
        spec.environment("MIN_MAGISK_VERSION", minMagiskVersion)
        spec.environment("ZKSU_VERSION", "$verName-$verCode-$commitHash-$profile")
    }
}

afterEvaluate {
    task<Task>("buildAndStrip") {
        dependsOn(":zygiskd:cargoBuild")
        val isDebug = gradle.startParameter.taskNames.any { it.toLowerCase().contains("debug") }
        doLast {
            val dir = File(buildDir, "rustJniLibs/android")
            val prebuilt = File(android.ndkDirectory, "toolchains/llvm/prebuilt").listFiles()!!.first()
            val binDir = File(prebuilt, "bin")
            val symbolDir = File(buildDir, "symbols/${if (isDebug) "debug" else "release"}")
            symbolDir.mkdirs()
            val suffix = if (prebuilt.name.contains("windows")) ".exe" else ""
            val strip = File(binDir, "llvm-strip$suffix")
            val objcopy = File(binDir, "llvm-objcopy$suffix")
            dir.listFiles()!!.forEach {
                if (!it.isDirectory) return@forEach
                val symbolPath = File(symbolDir, "${it.name}/zygiskd.debug")
                symbolPath.parentFile.mkdirs()
                exec {
                    workingDir = it
                    commandLine(objcopy, "--only-keep-debug", "zygiskd", symbolPath)
                }
                exec {
                    workingDir = it
                    commandLine(strip, "--strip-all", "zygiskd")
                }
                exec {
                    workingDir = it
                    commandLine(objcopy, "--add-gnu-debuglink", symbolPath, "zygiskd")
                }
            }
        }
    }
}

android {
    defaultConfig {
        val abi = System.getenv("ZYGISK_ABI")?.lowercase() ?: "arm64-v8a"
    }
    sourceSets {
        getByName("main") {
            val abi = System.getenv("ZYGISK_ABI")?.lowercase() ?: "arm64-v8a"
        }
    }
}
