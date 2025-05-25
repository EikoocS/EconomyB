plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta13"
}

group = "tech.cookiepower"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(project(":economyb-api"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

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

var projectVersionToPrint = project.version
tasks.withType<ProcessResources>().configureEach {
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand("version" to projectVersionToPrint)
    }
}

tasks.shadowJar {
    archiveClassifier = ""
}

tasks.build {
    dependsOn(tasks.shadowJar)
}