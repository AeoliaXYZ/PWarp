plugins {
  kotlin("jvm") version "2.2.0"
  id("com.gradleup.shadow") version "8.3.0"
  id("xyz.jpenilla.run-paper") version "2.3.1"
  kotlin("plugin.serialization") version "2.2.0"
}

group = "xyz.aeolia"
version = "1.0"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/") {
    name = "papermc-repo"
  }
  maven("https://repo.asheiou.cymru/releases/") {
    name = "asheiou-repo"
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
  compileOnly("xyz.aeolia:AeoliaLib:2.1.9")
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

tasks {
  runServer {
    // Configure the Minecraft version for our task.
    // This is the only required configuration besides applying the plugin.
    // Your plugin's jar (or shadowJar if present) will be used automatically.
    minecraftVersion("1.21")
  }
}

val targetJavaVersion = 21
kotlin {
  jvmToolchain(targetJavaVersion)
}

tasks.build {
  dependsOn("shadowJar")
}

tasks.processResources {
  val props = mapOf("version" to version)
  inputs.properties(props)
  filteringCharset = "UTF-8"
  filesMatching("plugin.yml") {
    expand(props)
  }
}
