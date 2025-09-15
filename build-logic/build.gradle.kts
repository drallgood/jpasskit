import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(libs.com.benjaminsproule.license)
    implementation(libs.org.jreleaser)
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        apiVersion = "1.5"
        languageVersion = "1.5"
    }
}

gradlePlugin {
    plugins {
        create("java-conventions") {
            id = "de.brendamour.java-conventions"
            implementationClass = "de.brendamour.JavaConventionsPlugin"
        }
    }
}
