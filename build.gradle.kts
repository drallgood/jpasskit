import org.gradle.tooling.GradleConnector

plugins {
    alias(libs.plugins.io.github.gradle.nexus.publish.plugin)
    alias(libs.plugins.net.researchgate.release)
}

allprojects {
    group = "de.brendamour"
    version = "0.4.0-SNAPSHOT"
}

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

// Workaround for https://github.com/researchgate/gradle-release/issues/184
configure(listOf(tasks.release, tasks.runBuildTasks)) {
    configure {
        actions.clear()
        doLast {
            GradleConnector
                    .newConnector()
                    .forProjectDirectory(layout.projectDirectory.asFile)
                    .connect()
                    .use { projectConnection ->
                        val buildLauncher = projectConnection
                                .newBuild()
                                .forTasks(*tasks.toTypedArray())
                                .setStandardInput(System.`in`)
                                .setStandardOutput(System.out)
                                .setStandardError(System.err)
                        gradle.startParameter.excludedTaskNames.forEach {
                            buildLauncher.addArguments("-x", it)
                        }
                        buildLauncher.run()
                    }
        }
    }
}