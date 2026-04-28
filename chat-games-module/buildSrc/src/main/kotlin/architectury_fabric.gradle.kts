@file:Suppress("UnstableApiUsage")

import utils.EnvironmentType
import utils.InternalLibs
import utils.Libs
import utils.Utils
import utils.Utils.ProjectMetadata

plugins {
    id("mmorealms_base")
    id("com.gradleup.shadow")
    id("dev.architectury.loom")
    id("architectury-plugin")
}

Utils.rootProject = rootProject
val metadata: ProjectMetadata = Utils.getProjectMetadata("fabric")

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName = when (metadata.type) {
            EnvironmentType.LOADER -> "loader.refmap.json"
            EnvironmentType.MODULE -> "${metadata.id}.refmap.json"
            EnvironmentType.OTHER -> "${metadata.id}.refmap.json"
        }
    }
}

val common = configurations.create("common") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val shadowBundle = configurations.create("shadowBundle") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations {
    compileClasspath {
        extendsFrom(common)
    }
    runtimeClasspath {
        extendsFrom(common)
    }
    api {
        extendsFrom(shadowBundle)
    }
}

dependencies {
    minecraft(Libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(Libs.parchment)
    })

    modImplementation(Libs.fabric.loader)
    modImplementation(Libs.fabric.api)
    modImplementation(Libs.architectury.fabric)

    when (metadata.type) {
        EnvironmentType.LOADER, EnvironmentType.MODULE -> {
            shadowBundle(project(":common"))

            common(project(":backend-common", configuration = "namedElements"))
            shadowBundle(project(":backend-common", configuration = "transformProductionFabric"))
        }

        EnvironmentType.OTHER -> {
        }
    }

    when (metadata.type) {
        EnvironmentType.LOADER -> {
        }

        EnvironmentType.MODULE -> {
            Utils.findAdditionalDependencies().forEach { dependencyString ->
                val dependency = InternalLibs.findDependency(dependencyString)?.backend?.fabric

                if (dependency == null) {
                    println("Failed to find matching dependency $dependencyString")
                    return@forEach
                }

                modCompileOnlyApi(dependency)
            }
        }

        EnvironmentType.OTHER -> {
        }
    }
}

tasks {
    remapJar {
        archiveFileName = Utils.getJarName(metadata)

        injectAccessWidener.set(true)
        inputFile.set(tasks.shadowJar.get().archiveFile)
        dependsOn(tasks.shadowJar)
    }

    shadowJar {
        exclude("architectury.common.json")
        isZip64 = true

        configurations = listOf(
            shadowBundle
        )
        archiveClassifier.set("dev-shadow")
    }

    jar {
        archiveClassifier.set("dev")
    }
}

afterEvaluate {
    components.getByName("java") {
        this as AdhocComponentWithVariants
        this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
            skip()
        }
    }
}