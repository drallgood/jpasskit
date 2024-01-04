plugins {
    alias(libs.plugins.io.github.gradle.nexus.publish.plugin)
}

nexusPublishing {
    repositories {
        sonatype()
    }
}