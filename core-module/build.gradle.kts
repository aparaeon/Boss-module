import utils.InternalLibs
import utils.Libs
import utils.Statics
import utils.Utils

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("dev.architectury.loom") apply false
    id("architectury-plugin")
}

Utils.rootProject = rootProject

version = Utils.getVersion()
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
                        "id" to metadata.id.replace("-", "_"),
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

    publishing {
        publications {
            if (Utils.getProperty(Statics.PUBLISH_PROPERTY) == Statics.PUBLISH_PROPERTY_REMOTE) {
                create<MavenPublication>("mavenJava") {
                    artifactId = "${rootProject.name}-${base.archivesName.get()}"
                    val version = Utils.getProperty(Statics.PUBLISH_VERSION_PROPERTY, defaultValue = "")
                    if (version.isNotEmpty()) {
                        this.version = version
                    }
                    from(components["java"])
                    println("Publishing $artifactId with version ${this.version} to remote repository")

                    pom {
                        withXml {
                            val deps = asNode().children()
                                .filterIsInstance<groovy.util.Node>()
                                .find { it.name().toString().endsWith("dependencies") }
                                ?: return@withXml

                            deps.children()
                                .filterIsInstance<groovy.util.Node>()
                                .forEach { dep ->
                                    val group = (dep.get("groupId") as groovy.util.NodeList).text()
                                    val artifact = (dep.get("artifactId") as groovy.util.NodeList).text()
                                    val versionNode = (dep.get("version") as groovy.util.NodeList)
                                        .firstOrNull() as? groovy.util.Node ?: return@forEach

                                    // Map of overrides: "group:artifact" -> "version"
                                    val overrides = mapOf(
                                        "com.example:lib-a" to "1.5.0",
                                        "org.other:lib-b" to "3.2.1"
                                    )

                                    overrides["$group:$artifact"]?.let { versionNode.setValue(it) }
                                }
                        }
                    }
                }
            } else if (Utils.getProperty(Statics.PUBLISH_PROPERTY) == Statics.PUBLISH_PROPERTY_LOCAL) {
                create<MavenPublication>("mavenJavaLocal") {
                    artifactId = "${rootProject.name}-${base.archivesName.get()}"
                    version = Utils.getProperty(
                        Statics.PUBLISH_VERSION_PROPERTY,
                        defaultValue = InternalLibs.LOCAL_DEPENDENCY_VERSION
                    )
                    from(components["java"])
                    println("Publishing $artifactId with version ${this.version} to local repository")
                }
            }
        }

        repositories {
            if (Utils.getProperty(Statics.PUBLISH_PROPERTY) == Statics.PUBLISH_PROPERTY_REMOTE) {
                maven(url = Utils.getProperty(Statics.PUBLISH_URL_PROPERTY)) {
                    name = "MMORealms"
                    credentials(PasswordCredentials::class) {
                        username = Utils.getProperty(Statics.PUBLISH_USERNAME_PROPERTY)
                        password = Utils.getProperty(Statics.PUBLISH_PASSWORD_PROPERTY)
                    }
                }
            } else if (Utils.getProperty(Statics.PUBLISH_PROPERTY) == Statics.PUBLISH_PROPERTY_LOCAL) {
                mavenLocal()
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

}