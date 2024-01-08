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
	implementation("com.beust", "klaxon", "5.5")
	implementation("net.jthink", "jaudiotagger", "3.0.1")
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(8)
}