plugins {
    java
    id("io.freefair.lombok") version "9.2.0"
    id("com.gradleup.shadow") version "9.3.2"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

val apiVersion = project.property("plugin.api.version") as String
val frameworkVersion = project.property("framework.version") as String

version = project.property("project.version") as String
group = project.property("project.group") as String

tasks.runServer {
    minecraftVersion(project.property("minecraft.version") as String)
    downloadPlugins {
        url("https://ci.codemc.io/job/RTUStudio/job/RSFramework/lastSuccessfulBuild/artifact/builds/plugin/RSFramework-$frameworkVersion.jar")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(project.property("java.version") as String))
}

repositories {
    mavenCentral()
    maven {
        name = "Sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        name = "SpigotMC"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "PaperMC"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }

    maven {
        name = "RTUStudio"
        url = uri("https://repo.codemc.io/repository/rtustudio/")
    }

    maven {
        name = "PlaceholderAPI"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    // Bukkit API
    val bukkitAPI = if (project.property("plugin.paper").toString().toBoolean()) {
        "io.papermc.paper:paper-api:${apiVersion}-R0.1-SNAPSHOT"
    } else {
        "org.spigotmc:spigot-api:${apiVersion}-R0.1-SNAPSHOT"
    }
    compileOnly(bukkitAPI)
    // RSFramework
    compileOnly("kr.rtustudio:framework-api:$frameworkVersion")
    compileOnly(fileTree("libs").include("*.jar"))

    // Adventure
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.16.0")

    // Utility
    compileOnly("com.google.code.gson:gson:2.13.1")
    compileOnly("com.google.guava:guava:33.4.8-jre")
    compileOnly("org.apache.commons:commons-lang3:3.18.0")
    compileOnly("it.unimi.dsi:fastutil:8.5.15")

    // Integration
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.retrooper:packetevents-spigot:2.11.2")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.jar {
    finalizedBy("shadowJar")
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveBaseName.set(project.name)
    doLast {
        var plugin = archiveFile.get().asFile
        val target = file("$rootDir/run/plugins")
        if (target.exists() && target.isDirectory) {
            copy {
                from(plugin)
                into(target)
            }
        }
    }
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "name" to project.name,
        "main" to project.property("plugin.main"),
        "api_version" to apiVersion.substringBeforeLast("."),
        "author" to project.property("project.author")
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
