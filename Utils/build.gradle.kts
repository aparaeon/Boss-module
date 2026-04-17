plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("tech.medivh.plugin.publisher") version "1.2.1" apply false
    id("signing")
}

if (project.properties["maven.publish"] == "true") {
    apply(plugin = "tech.medivh.plugin.publisher")
}

group = libs.versions.group.get()
version = libs.versions.version.get()

repositories {
    mavenCentral()
    maven("https://repo.raduvoinea.com/repository/maven-releases/")
}

dependencies {
    // Dependencies
    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.jedis)
    testImplementation(libs.jedis)

    // Annotations
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)

    // Tests
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation("org.junit-pioneer:junit-pioneer:2.3.0")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        jvmArgs(
            "--add-opens", "java.base/java.util=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang=ALL-UNNAMED"
        )
    }
}

tasks.register("javadocJar", Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("javadoc"))
}

tasks.register("sourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

project.afterEvaluate {
    if (project.properties["maven.publish"] == "true") {
        project.tasks["publishMavenPublicationToMedivhSonatypeRepository"].enabled = false
        project.tasks["publishMedivhMavenJavaPublicationToRaduVoineaRepository"].enabled = false
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("Utils Library")
                description.set("A utility library for various purposes.")
                url.set("https://github.com/Radu-Voinea/Utils")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/Radu-Voinea/Utils/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("raduvoinea")
                        name.set("Radu Voinea")
                        email.set("contact@raduvoinea.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Radu-Voinea/Utils.git")
                    developerConnection.set("scm:git:ssh://git@github.com/Radu-Voinea/Utils.git")
                    url.set("https://github.com/Radu-Voinea/Utils")
                }
            }
        }
    }

    repositories {
        if (project.properties["com.raduvoinea.publish"] == "true") {
            maven(url = (project.findProperty("com.raduvoinea.url") ?: "") as String) {
                name = "RaduVoinea"
                credentials(PasswordCredentials::class) {
                    username = (project.findProperty("com.raduvoinea.auth.username") ?: "") as String
                    password = (project.findProperty("com.raduvoinea.auth.password") ?: "") as String
                }
            }
        }

        if (project.properties["generic.publish"] == "true") {
            maven(url = (project.findProperty("generic.url") ?: "") as String) {
                name = "Generic"
                credentials(PasswordCredentials::class) {
                    username = (project.findProperty("generic.auth.username") ?: "") as String
                    password = (project.findProperty("generic.auth.password") ?: "") as String
                }
            }
        }
    }
}

