@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.fabric.loom)
}

dependencies {
    // Minecraft
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment)
    })

    modCompileOnly(libs.fabric.loader)
    modCompileOnly(libs.fabric.api)

    // Project
    api(project(":command-manager-common"))

    // Dependencies
    if (project.properties["com.raduvoinea.utils.local"] != null) {
        api(project(project.properties["com.raduvoinea.utils.local"] as String))
    } else {
        api(libs.raduvoinea.utils)
    }
    api(libs.luckperms)
    modApi(libs.kyori.adventure.fabric)

    // Annotations
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)
}

tasks {
    processResources {
        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }

    jar {
        archiveFileName = "FabricCommandManager-$version.jar"
    }

    remapJar {
        archiveFileName = "FabricCommandManager-$version.jar"
    }
}