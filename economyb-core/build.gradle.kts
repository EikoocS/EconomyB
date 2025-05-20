plugins {
    id("java")
    id("io.freefair.lombok") version "8.13.1"
}

group = "tech.cookiepower"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":economyb-api"))

    implementation("io.quarkus:quarkus-hibernate-reactive-panache:3.22.3")
    implementation("io.quarkus:quarkus-reactive-mysql-client:3.22.3")

    testImplementation("org.slf4j:slf4j-nop:2.0.17")
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}