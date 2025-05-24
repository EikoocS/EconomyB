plugins {
    id("java")
}

group = "tech.cookiepower"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":economyb-api"))

    implementation("io.vertx:vertx-core:5.0.0")
    implementation("io.vertx:vertx-sql-client:5.0.0")
    implementation("io.vertx:vertx-mysql-client:5.0.0")

    testImplementation("org.slf4j:slf4j-nop:2.0.17")
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}