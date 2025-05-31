import java.util.*
import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
    `java-library`
    `maven-publish`
    `jvm-test-suite`
    signing
    id("org.jreleaser")
    id("com.benjaminsproule.license")
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
            url.set("https://github.com/drallgood/jpasskit")
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
                    name.set("Patrice Brend'amour")
                    email.set("patrice@brendamour.net")
                    url.set("https://brendamour.net")
                    timezone.set("Europe/Vienna")
                }
            }
            scm {
                connection.set("scm:https://github.com/drallgood/jpasskit.git")
                developerConnection.set("scm:git@github.com:drallgood/jpasskit.git")
                url.set("https://github.com/drallgood/jpasskit")
            }
        }
    }
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy").get().asFile)
        }
    }
}

jreleaser {
    gitRootSearch.set(true)
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
                    stagingRepositories.set(listOf(layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath))
                    applyMavenCentralRules.set(true)
                }
            }
            nexus2 {
                create("snapshot-deploy") { // Use create() for named configurations
                    active.set(Active.SNAPSHOT)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    snapshotUrl.set("https://central.sonatype.com/repository/maven-snapshots")
                    stagingRepositories.set(listOf(layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath))
                    applyMavenCentralRules.set(true)
                    closeRepository.set(true)
                    snapshotSupported.set(true)
                    releaseRepository.set(true)
                }
            }
        }
    }
}

signing {
    if (project.hasProperty("signingKeyId")) {
        useInMemoryPgpKeys(properties["signingKeyId"].toString(),properties["signingKey"].toString(), properties["signingPassword"].toString())
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

tasks.register<Wrapper>("wrapper") {
    gradleVersion = "8.7"
}
tasks.register("prepareKotlinBuildScriptModel"){}

license {
    header  = project.file("header.txt")
    skipExistingHeaders = true
    ext.set("year", Calendar.getInstance().get(Calendar.YEAR))
    ext.set("owner", "Patrice Brend'amour")
    ext.set("email", "patrice@brendamour.net")
    excludes(arrayListOf("*pom.xml","*.checkstyle","**/*.cer","**/*.pem","**/*.p12","**/*.ignored_file","site/*","**/*.json","**/*.png"))
}