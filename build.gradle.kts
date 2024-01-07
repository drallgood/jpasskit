plugins {
    alias(libs.plugins.io.github.gradle.nexus.publish.plugin)
}

nexusPublishing {
    repositories {
        sonatype {
            /*stagingProfileId.set()*/
            packageGroup = "de.brendamour"
        }
    }
}

//do not generate extra load on Nexus with new staging repository if signing fails
val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }
}