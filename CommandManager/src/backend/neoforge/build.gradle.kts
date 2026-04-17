@file:Suppress("UnstableApiUsage")

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("com.gradleup.shadow")
}

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

    neoForge(Libs.neoforge)
    modImplementation(Libs.architectury.neoforge)

    shadowBundle(project(":common"))

    common(project(":backend-common", configuration = "namedElements"))
    shadowBundle(project(":backend-common", configuration = "transformProductionNeoForge"))

    modCompileOnlyApi("net.kyori:adventure-platform-neoforge:6.0.0")
}

tasks {
    remapJar {
        archiveFileName = "CommandManager-NeoForge-${version}.jar"

        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
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

components.getByName("java") {
    this as AdhocComponentWithVariants
    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}


