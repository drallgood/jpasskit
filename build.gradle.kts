plugins {
    `maven-publish`
    id("de.brendamour.java-conventions")
    id("org.jreleaser") version "1.21.0"
}

import org.jreleaser.model.Active

jreleaser {
    gitRootSearch.set(true)
    
    project {
        name.set("jpasskit")
        description.set("Java Library for Apple PassKit Web Service")
        license.set("Apache-2.0")
        authors.set(listOf("Patrice Brend'amour"))
        copyright.set("2012-${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Patrice Brend'amour")
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
        }
    }
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



//nexusPublishing {
//    repositories {
//        sonatype ()
//    }
//}
//
////do not generate extra load on Nexus with new staging repository if signing fails
//val initializeSonatypeStagingRepository by tasks.existing
//subprojects {
//    initializeSonatypeStagingRepository {
//        shouldRunAfter(tasks.withType<Sign>())
//    }
//
//}
