plugins {
	kotlin("jvm") version "1.9.21"
}

group = "dev.defvs.cafet"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
	testImplementation("io.kotest", "kotest-runner-junit5", "5.8.0")
	testImplementation("io.kotest", "kotest-assertions-core", "5.8.0")
	testImplementation("io.kotest", "kotest-property", "5.8.0")
	implementation("com.beust", "klaxon", "5.5")
	implementation("net.jthink", "jaudiotagger", "3.0.1")
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(17)
}