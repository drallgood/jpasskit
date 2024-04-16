import java.util.*
import net.researchgate.release.ReleaseExtension
/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    `java-library`
    `maven-publish`
    `jvm-test-suite`
    signing
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
            description = "Java Library for Apple PassBook Web Service API (PARENT POM)"
            url.set("https://github.com/drallgood/jpasskit")
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
}

signing {
    if (project.hasProperty("signingKey")) {
        useInMemoryPgpKeys(properties["signingKey"].toString(), properties["signingPassword"].toString())
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
apply(plugin = "net.researchgate.release")

configure<ReleaseExtension> {
    with(git) {
        requireBranch.set("master")
        // to disable branch verification: requireBranch.set(null as String?)
    }
}