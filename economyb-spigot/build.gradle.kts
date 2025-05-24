plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
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
}

dependencies {
    implementation(project(":economyb-core"))
    compileOnly(project(":economyb-api"))

    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.named<xyz.jpenilla.runpaper.task.RunServer>("runServer") {
    // Configure the Minecraft version for our task.
    // This is the only required configuration besides applying the plugin.
    // Your plugin's jar (or shadowJar if present) will be used automatically.
    minecraftVersion("1.21")
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
    mergeServiceFiles()
    exclude("META-INF/io.netty.versions.properties")
    relocate("io.netty", "tech.cookiepower.shadow.netty")
    relocate("io.vertx", "tech.cookiepower.shadow.vertx")
    archiveClassifier = ""
}

tasks.build {
    dependsOn(tasks.shadowJar)
}