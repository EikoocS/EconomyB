plugins {
    id("java-library")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    }

    tasks.test {
        useJUnitPlatform()
    }
}

tasks.register<Copy>("collectJar") {
    val pluginsProject = listOf(
        project(":economyb-spigot"),
        project(":economyb-spigot-api"),
        project(":economyb-spigot-api2vault"),
    )

    dependsOn(pluginsProject.map { it.tasks.named("jar") })

    val libsDir  = layout.buildDirectory.dir("libs").get().asFile
    val tmpDir = layout.buildDirectory.dir("tmp").get().asFile

    doFirst {
        if (libsDir.exists()) { libsDir.deleteRecursively() }
        if (tmpDir.exists()) { tmpDir.deleteRecursively() }
        println("Clean up root project build directories")
    }

    from(pluginsProject.map { it.layout.buildDirectory.dir("libs") }) {
        include("*.jar")
    }

    into(libsDir )
}

tasks.named("build") {
    dependsOn("collectJar")
}