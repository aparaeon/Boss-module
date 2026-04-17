import utils.Libs
import utils.Utils

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("dev.architectury.loom") apply false
    id("architectury-plugin")
}

Utils.rootProject = rootProject

version = Utils.readVersion()
group = "gg.mmorealms"

architectury {
    minecraft = "1.21.1"
}

fun processFile(project: Project, file: String) {
    val metadata: Utils.ProjectMetadata = Utils.getProjectMetadata("base")

    project.tasks {
        processResources {
            filesMatching(file) {
                expand(
                    mapOf(
                        "id" to metadata.id,
                        "underscore_id" to metadata.id,
                        "version" to project.version as String,
                        "minecraft_version" to Libs.minecraftVersion,
                        "fabric_loader_version" to Libs.fabricLoaderVersion,
                        "fabric_api_version" to Libs.fabricApiVesion,
                        "java_version" to "21",
                        "architectury_version" to Libs.architectury.version,
                        "neoforge_version" to Libs.neoforgeVersion
                    )
                )
            }
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = rootProject.group
    version = rootProject.version

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = "${rootProject.name}-${base.archivesName.get()}"
                from(components["java"])
            }
        }

        repositories {
            maven(url = Utils.getProperty("gg.mmorealms.url")) {
                name = "MMORealms"
                credentials(PasswordCredentials::class) {
                    username = Utils.getProperty("gg.mmorealms.username")
                    password = Utils.getProperty("gg.mmorealms.password")
                }
            }
        }
    }

    processFile(project, "fabric.mod.json")
    processFile(project, "META-INF/neoforge.mods.toml")
}


tasks {
    build {
        finalizedBy("copySubprojectJars")
    }

    register<Copy>("copySubprojectJars") {
        group = "build"

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        val baseLibsDirectory = layout.buildDirectory.dir("libs")

        doFirst {
            val libsFile = baseLibsDirectory.get().asFile
            if (libsFile.exists()) {
                libsFile.deleteRecursively()
            }
        }

        dependsOn(subprojects.flatMap { subproject ->
            listOfNotNull(
                subproject.tasks.findByName("remapJar"),
                subproject.tasks.findByName("shadowJar"),
                subproject.tasks.findByName("jar")
            )
        })

        subprojects.forEach { subProject ->
            listOf("remapJar", "shadowJar", "jar").forEach { taskName ->
                subProject.tasks.findByName(taskName)?.let { task ->
                    if (task is AbstractArchiveTask) {

                        from(task.archiveFile) {
                            into(
                                task.archiveFileName.map { name ->
                                    when {
                                        "-dev" in name.lowercase() -> "dev-jars"
                                        "-common" in name.lowercase() -> "common-jars"
                                        else -> "."
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        into(baseLibsDirectory)
    }

    register("bumpVersion") {
        doFirst {
            var platforms = listOf("backend-common", "backend-fabric", "backend-neoforge", "velocity", "common")
            var remoteVersion = "1.0.0"

            for (platform in platforms) {
                var currentPlatformRemoteVersion = Utils.getLatestVersion(
                    artifactId = "${rootProject.name}-$platform",
                    username = Utils.getProperty("gg.mmorealms.username"),
                    password = Utils.getProperty("gg.mmorealms.password"),
                )

                println("Remote version ($platform): $remoteVersion")
                remoteVersion = Utils.getMaxVersion(remoteVersion, currentPlatformRemoteVersion)
            }

            var localVersion = Utils.readVersion()

            println("Remote version: $remoteVersion")
            println("Local version: $localVersion")
            var version = Utils.getMaxVersion(remoteVersion, localVersion)

            version = Utils.bumpVersion(version)
            Utils.writeVersion(version)

            println("New version: $version")
        }

    }

}
