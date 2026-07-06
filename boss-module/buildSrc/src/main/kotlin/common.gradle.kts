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
            // Do NOT bundle the Kotlin stdlib. It is provided once at runtime
            // (KotlinForForge on NeoForge, Fabric Language Kotlin on Fabric).
            // Bundling it into every jar makes each an automatic JPMS module that
            // exports kotlin.* — two such modules exporting the same package to a
            // third is a fatal ResolutionException on NeoForge. This is the only
            // place it can be dropped: every platform jar bundles :common via
            // shadowBundle, by which point the stdlib is loose class files with no
            // dependency coordinate left to exclude downstream.
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib.*"))
        }

        // Belt-and-suspenders: drop the stdlib by path too, in case the Kotlin
        // plugin pulls it onto the classpath without a matchable coordinate.
        exclude("kotlin/**")
        exclude("META-INF/*.kotlin_module")

        // Same JPMS split-package problem as Kotlin: org.jetbrains:annotations
        // (compile-only, advisory) leaks transitively onto the runtime classpath
        // and gets bundled into every jar, exporting org.jetbrains.annotations /
        // org.intellij.lang.annotations. Drop it — nothing reads it at runtime.
        exclude("org/jetbrains/annotations/**")
        exclude("org/intellij/lang/annotations/**")
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

