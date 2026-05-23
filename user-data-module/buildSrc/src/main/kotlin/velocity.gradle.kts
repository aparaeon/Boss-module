import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import utils.EnvironmentType
import utils.InternalLibs
import utils.Libs
import utils.Utils

// TODO Find a way to do version management in this.
//      Note: libs.version.toml does not work

plugins {
    id("mmorealms_base")

    id("com.gradleup.shadow")
}

Utils.rootProject = rootProject
val metadata: Utils.ProjectMetadata = Utils.getProjectMetadata("velocity")

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0.3")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0.3")

    when (metadata.type) {
        EnvironmentType.LOADER, EnvironmentType.MODULE -> {
            api(project(":common"))
        }

        EnvironmentType.OTHER -> {
        }
    }

    when (metadata.type) {
        EnvironmentType.LOADER -> {
        }

        EnvironmentType.MODULE -> {
            Utils.findAdditionalDependencies().forEach { dependencyString ->
                val dependency = InternalLibs.findDependency(dependencyString)?.velocity

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
    jar {
        enabled = false
    }

    shadowJar {
        if (findProject(":common") != null) {
            dependsOn(":common:shadowJar")
        }

        archiveFileName = Utils.getJarName(metadata)
        isZip64 = true

        dependencies {
            exclude(dependency("net.luckperms:api:.*"))
        }

        archiveClassifier.set("")
    }

    sourceSets.main {
        java {
            srcDir("${buildDir}/generated/sources/templates")
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    jar {
        archiveFileName = Utils.getJarName(metadata)
    }
}

tasks.register<Sync>("generateTemplates") {

}

configurations {
    named("runtimeElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.named<ShadowJar>("shadowJar"))
    }
    named("apiElements") {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.named<ShadowJar>("shadowJar"))
    }
}
