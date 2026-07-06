import utils.InternalLibs
import utils.Libs
import utils.Statics
import utils.Utils
import kotlin.text.get

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("architectury-plugin")
    kotlin("jvm")
}

Utils.rootProject = rootProject

version = Utils.getVersion()
group = "gg.mmorealms"

println("================ VERSION ================")
println("Build/Publish version: ${rootProject.version}")
println("================ VERSION ================")

println("================ SETTINGS ================")
Statics.ALL_SETTINGS.forEach {
    println(it + ": " + Utils.getProperty(it))
}
println("================ SETTINGS ================")

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
    val clearLibs by registering {
        group = "build"
        description = "Clears build/libs for the root project and all subprojects before any artifacts are produced."

        doFirst {
            val libsDirs = listOf(rootProject) + subprojects
            libsDirs.forEach { proj ->
                val libsDir = proj.layout.buildDirectory.dir("libs").get().asFile
                if (libsDir.exists()) {
                    println("[Build] Clearing ${proj.path} build/libs")
                    libsDir.deleteRecursively()
                }
            }
        }
    }

    build {
        dependsOn(clearLibs)
        finalizedBy("copySubprojectJars")
    }

    subprojects.forEach { subproject ->
        subproject.tasks.withType<AbstractArchiveTask>().configureEach {
            mustRunAfter(clearLibs)
        }
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