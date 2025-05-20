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