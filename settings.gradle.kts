pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven("https://jitpack.io")
		maven("https://maven.fabricmc.net")
		maven("https://maven.architectury.dev/")
		maven("https://maven.minecraftforge.net")
		maven("https://repo.essential.gg/repository/maven-public")
	}
	plugins {
    val kotlinVersion = "1.8.21"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
		id("gg.essential.multi-version.root") version "0.3.0"
	}
  /*
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id.startsWith("gg.essential.")) {
        useModule("com.github.Skytils.essential-gradle-toolkit:essential-gradle-toolkit:a243c8ed83")
      }
    }
  }
  */
}

rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.19.4-fabric",
    "1.20.1-fabric",
    "1.20.2-fabric",
    "1.20.4-fabric"
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
