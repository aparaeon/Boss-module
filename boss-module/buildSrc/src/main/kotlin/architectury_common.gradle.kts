@file:Suppress("UnstableApiUsage")

import utils.EnvironmentType
import utils.InternalLibs
import utils.Libs
import utils.Utils

plugins {
    id("mmorealms_base")
    id("dev.architectury.loom")
    id("architectury-plugin")
}

Utils.rootProject = rootProject
val metadata: Utils.ProjectMetadata = Utils.getProjectMetadata("backend-common")

architectury {
    common("fabric,neoforge".split(","))
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

dependencies {
    modCompileOnlyApi(Libs.sgui.fabric)

    minecraft(Libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(Libs.parchment)
    })

    modImplementation(Libs.fabric.loader)
    modImplementation(Libs.architectury.common)

    when (metadata.type) {
        EnvironmentType.LOADER, EnvironmentType.MODULE -> {
            api(project(":common"))
            modCompileOnly(Libs.raduvoinea.commandmanager.backend.common)
        }

        EnvironmentType.OTHER -> {
        }
    }

    when (metadata.type) {
        EnvironmentType.LOADER -> {
        }

        EnvironmentType.MODULE -> {
            Utils.findAdditionalDependencies().forEach { dependencyString ->
                val dependency = InternalLibs.findDependency(dependencyString)?.backend?.common

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
    getByName("transformProductionFabric") {
        when (metadata.type) {
            EnvironmentType.LOADER, EnvironmentType.MODULE -> {
                findProject(":common")?.tasks?.build?.let { dependsOn(it) }
            }
            EnvironmentType.OTHER -> {}
        }
    }

    getByName("transformProductionNeoForge") {
        when (metadata.type) {
            EnvironmentType.LOADER, EnvironmentType.MODULE -> {
                findProject(":common")?.tasks?.build?.let { dependsOn(it) }
            }
            EnvironmentType.OTHER -> {}
        }
    }
}