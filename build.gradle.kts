import org.jreleaser.model.Active
import org.jreleaser.model.Signing
import java.util.Calendar

plugins {
    `maven-publish`
    id("de.brendamour.java-conventions")
    id("org.jreleaser") version "1.22.0"
}

allprojects {
    group = "de.brendamour"
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()
    }
}

jreleaser {
    gitRootSearch.set(true)
    
    project {
        name.set("jpasskit")
        description.set("Java Library for Apple PassKit Web Service")
        license.set("Apache-2.0")
        authors.set(listOf("Patrice Brend'amour"))
        copyright.set("2012-${Calendar.getInstance().get(Calendar.YEAR)} Patrice Brend'amour")
    }
    
    release {
        github {
            repoOwner.set("drallgood")
            name.set("jpasskit")
            overwrite.set(true)
            skipTag.set(true)
            changelog {
                preset.set("conventional-commits")
                skipMergeCommits.set(true)
            }
        }
    }
    
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
        command {
            executable.set("gpg")
            args.set(listOf("--batch", "--yes", "--pinentry-mode", "loopback"))
            keyName.set("9474B9FCBDF93BEE7CC4B69B4CE3C3B7A5E5FCC2")
        }
        mode.set(Signing.Mode.COMMAND)
    }
    
    deploy {
        maven {
            mavenCentral {
                create("release-deploy") {
                    active.set(Active.RELEASE)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    stagingRepositories.set(
                        listOf(
                            layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath,
                            project(":jpasskit").layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath,
                            project(":jpasskit.server").layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath
                        )
                    )
                    applyMavenCentralRules.set(true)
                }
            }
            nexus2 {
                create("snapshot-deploy") {
                    active.set(Active.SNAPSHOT)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    snapshotUrl.set("https://central.sonatype.com/repository/maven-snapshots")
                    stagingRepositories.set(
                        listOf(
                            layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath,
                            project(":jpasskit").layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath,
                            project(":jpasskit.server").layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath
                        )
                    )
                    applyMavenCentralRules.set(true)
                    closeRepository.set(true)
                    snapshotSupported.set(true)
                }
            }
        }
    }
}
