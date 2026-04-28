import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import utils.EnvironmentType
import utils.InternalLibs
import utils.Libs
import utils.Utils

plugins {
    id("mmorealms_base")

    id("com.gradleup.shadow")
}

Utils.rootProject = rootProject
val metadata: Utils.ProjectMetadata = Utils.getProjectMetadata("common")

dependencies {
    when (metadata.type) {
        EnvironmentType.LOADER -> {
        }

        EnvironmentType.MODULE -> {
            Utils.findAdditionalDependencies().forEach { dependencyString ->
                val dependencyCommon = InternalLibs.findDependency(dependencyString)?.common

                if (dependencyCommon == null) {
                    println("Failed to find matching dependency $dependencyString")
                    return@forEach
                }

                compileOnlyApi(dependencyCommon)
            }
        }

        EnvironmentType.OTHER -> {}
    }
}

tasks {
    jar {
        enabled = false
    }

    withType<ShadowJar> {
        archiveFileName = Utils.getJarName(metadata)
        isZip64 = true

        relocate("com.zaxxer.hikari", "gg.mmorealms.shaded.hikari")

        exclude(
            "META-INF/LICENSE", "META-INF/LICENSE.txt", "META-INF/NOTICE", "META-INF/NOTICE.txt",
            "META-INF/NOTICE.md", "META-INF/DEPENDENCIES", "META-INF/io.netty.versions.properties",
            "META-INF/LICENSE.md", "META-INF/FastDoubleParser-LICENSE", "META-INF/FastDoubleParser-NOTICE"
        )

        dependencies {
            exclude(dependency("net.luckperms:api:.*"))
        }
    }

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

