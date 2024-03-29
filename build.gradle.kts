plugins {
    alias(libs.plugins.io.github.gradle.nexus.publish.plugin)
}

version =  "0.4.0-SNAPSHOT"

nexusPublishing {
    repositories {
        sonatype ()
    }
}

//do not generate extra load on Nexus with new staging repository if signing fails
val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }
}