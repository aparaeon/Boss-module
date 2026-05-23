@file:Suppress("UnstableApiUsage")

import utils.EnvironmentType
import utils.InternalLibs
import utils.Libs
import utils.Utils

project.ext["loom.platform"] = "neoforge"

plugins {
    id("mmorealms_base")
    id("com.gradleup.shadow")
    id("dev.architectury.loom")
    id("architectury-plugin")
}

Utils.rootProject = rootProject
val metadata: Utils.ProjectMetadata = Utils.getProjectMetadata("neoforge")

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    silentMojangMappingsLicense()

    neoForge {
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

    "neoForge"(Libs.neoforge)
    modImplementation(Libs.architectury.neoforge)

    when (metadata.type) {
        EnvironmentType.LOADER, EnvironmentType.MODULE -> {
            shadowBundle(project(":common"))

            common(project(":backend-common", configuration = "namedElements"))
            shadowBundle(project(":backend-common", configuration = "transformProductionNeoForge"))
        }

        EnvironmentType.OTHER -> {
        }
    }

    when (metadata.type) {
        EnvironmentType.LOADER -> {
        }

        EnvironmentType.MODULE -> {
            Utils.findAdditionalDependencies().forEach { dependencyString ->
                val dependency = InternalLibs.findDependency(dependencyString)?.backend?.neoforge

                if (dependency == null) {
                    println("Failed to find matching dependency $dependencyString")
                    return@forEach
                }

                compileOnlyApi(dependency)
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