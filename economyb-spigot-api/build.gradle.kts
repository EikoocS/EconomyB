plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta13"
}

group = "tech.cookiepower"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "Minecraft Libraries"
        url = uri("https://libraries.minecraft.net")
    }
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    implementation(project(":economyb-api"))
    compileOnly("com.mojang:brigadier:1.0.18")

    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.withType<ProcessResources>().configureEach {
    filteringCharset = "UTF-8"
    doLast {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}

tasks.shadowJar {
    archiveClassifier = ""
}

tasks.build {
    dependsOn(tasks.shadowJar)
}