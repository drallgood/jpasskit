plugins {
    `maven-publish`
    id("de.brendamour.java-conventions")
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
