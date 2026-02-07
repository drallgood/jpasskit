import java.util.*
import org.gradle.api.publish.maven.MavenPublication
import org.jreleaser.model.Active
import org.jreleaser.model.Signing
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.wrapper.Wrapper

// Constants for JReleaser configuration
val JPASSKIT_WEBSITE = "https://github.com/drallgood/jpasskit"
val JPASSKIT_AUTHOR = "Patrice Brend'amour"
val JPASSKIT_SCM_CONNECTION = "scm:https://github.com/drallgood/jpasskit.git"
val JPASSKIT_SCM_DEV_CONNECTION = "scm:git@github.com:drallgood/jpasskit.git"

plugins {
    `java-library`
    `maven-publish`
    `jvm-test-suite`
    signing
    id("org.jreleaser")
    id("com.benjaminsproule.license")
    jacoco
}

jacoco {
    toolVersion = "0.8.13"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://maven.restlet.talend.com")
    }

    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}


publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            url.set(JPASSKIT_WEBSITE)
            name.set(project.name)
            if (project.description != null) {
                description.set(project.description)
            } else {
                description.set("Java Library for Apple PassBook Web Service API")
            }
            inceptionYear.set("2012")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("patrice")
                    name.set(JPASSKIT_AUTHOR)
                    email.set("patrice@brendamour.net")
                    url.set("https://brendamour.net")
                    timezone.set("Europe/Vienna")
                }
            }
            scm {
                connection.set(JPASSKIT_SCM_CONNECTION)
                developerConnection.set(JPASSKIT_SCM_DEV_CONNECTION)
                url.set(JPASSKIT_WEBSITE)
            }
        }
    }
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy").get().asFile)
        }
    }
}

signing {
    val secretKey = System.getenv("JRELEASER_GPG_SECRET_KEY")
    val passphrase = System.getenv("JRELEASER_GPG_PASSPHRASE")
    if (secretKey != null && passphrase != null) {
        useInMemoryPgpKeys(
            secretKey,
            passphrase
        )
    } else {
        useGpgCmd()
    }
    sign(publishing.publications["maven"])
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

license {
    header = project.file("header.txt")
    skipExistingHeaders = true
    ext.set("year", Calendar.getInstance().get(Calendar.YEAR))
    ext.set("owner", "Patrice Brend'amour")
    ext.set("email", "patrice@brendamour.net")
    excludes(
        arrayListOf(
            "*pom.xml",
            "*.checkstyle",
            "**/*.cer",
            "**/*.pem",
            "**/*.p12",
            "**/*.ignored_file",
            "site/*",
            "**/*.json",
            "**/*.png"
        )
    )
}

// JaCoCo configuration
jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

// Re-enable JaCoCo instrumentation with updated version
tasks.test {
    useTestNG()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                minimum = "0.84".toBigDecimal()
            }
        }
        rule {
            enabled = true
            element = "CLASS"
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.60".toBigDecimal()
            }
            excludes = listOf(
                "*.test.*",
                "*.tests.*",
                "*Test*",
                "de.brendamour.jpasskit.signing.PKSigningInformationUtil",
                "de.brendamour.jpasskit.signing.PKAbstractSigningUtil",
                "de.brendamour.jpasskit.util.CertUtils"
            )
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

// Only configure JReleaser for root project
if (project == project.rootProject) {
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
            active.set(org.jreleaser.model.Active.ALWAYS)
            armored.set(true)
            command {
                executable.set("gpg")
                args.set(listOf("--batch", "--yes", "--pinentry-mode", "loopback"))
                keyName.set("9474B9FCBDF93BEE7CC4B69B4CE3C3B7A5E5FCC2")
            }
            mode.set(org.jreleaser.model.Signing.Mode.COMMAND)
        }
        
        deploy {
            maven {
                val stagingPaths = listOf(
                    layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath,
                    project(":jpasskit").layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath,
                    project(":jpasskit.server").layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath
                )
                mavenCentral {
                    create("release-deploy") {
                        active.set(org.jreleaser.model.Active.RELEASE)
                        url.set("https://central.sonatype.com/api/v1/publisher")
                        stagingRepositories.set(stagingPaths)
                        applyMavenCentralRules.set(true)
                    }
                }
                nexus2 {
                    create("snapshot-deploy") {
                        active.set(org.jreleaser.model.Active.SNAPSHOT)
                        url.set("https://central.sonatype.com/api/v1/publisher")
                        snapshotUrl.set("https://central.sonatype.com/repository/maven-snapshots")
                        stagingRepositories.set(stagingPaths)
                        applyMavenCentralRules.set(true)
                        closeRepository.set(true)
                        snapshotSupported.set(true)
                    }
                }
            }
        }
    }
}