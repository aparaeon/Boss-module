import utils.*
import java.util.zip.ZipFile

plugins {
    id("java")
    id("java-library")
}

version = rootProject.version
group = rootProject.group

Utils.rootProject = rootProject
val metadata: Utils.ProjectMetadata = Utils.getProjectMetadata("base")


repositories {
    mavenLocal()

    val useProxy = Utils.getProperty("gg.mmorealms.proxy")
    println("MMORealms proxy: $useProxy")

    if (useProxy == "true") {
        val privateProxyUrl = Utils.getProperty("gg.mmorealms.proxy.url.private")
        val publicProxyUrl = Utils.getProperty("gg.mmorealms.proxy.url.public")
        val proxyUsername = Utils.getProperty("gg.mmorealms.proxy.username")
        val proxyPassword = Utils.getProperty("gg.mmorealms.proxy.password")

        println("Using MMORealms Proxy Repositories at $privateProxyUrl and $publicProxyUrl with username $proxyUsername")

        maven(url = Utils.getProperty("gg.mmorealms.proxy.url.private")) {
            name = "MMORealms-Repository-Private-Proxy"
            credentials(PasswordCredentials::class) {
                username = proxyUsername
                password = proxyPassword
            }
        }

        maven(url = Utils.getProperty("gg.mmorealms.proxy.url.public")) {
            name = "MMORealms-Repository-Public-Proxy"
            credentials(PasswordCredentials::class) {
                username = proxyUsername
                password = proxyPassword
            }
        }
    } else {
        println("Using MMORealms Repositories with username ${Utils.getProperty("gg.mmorealms.username")}")

        maven(url = Utils.getProperty("gg.mmorealms.url")) {
            name = "MMORealms-Repository-Private"
            credentials(PasswordCredentials::class) {
                username = Utils.getProperty("gg.mmorealms.username")
                password = Utils.getProperty("gg.mmorealms.password")
            }
        }

        mavenCentral()

        maven("https://repo.raduvoinea.com/repository/maven-releases/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://maven.parchmentmc.org/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://jitpack.io/")
        maven("https://maven.nucleoid.xyz")
        maven("https://maven.enginehub.org/repo/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://maven.impactdev.net/repository/development/")
    }
}

dependencies {
    compileOnlyApi(Libs.lombok)
    annotationProcessor(Libs.lombok)
    testCompileOnly(Libs.lombok)
    testAnnotationProcessor(Libs.lombok)

    compileOnlyApi(Libs.jetbrains.annotations)
    annotationProcessor(Libs.jetbrains.annotations)
    testCompileOnly(Libs.jetbrains.annotations)
    testAnnotationProcessor(Libs.jetbrains.annotations)

    testImplementation(platform(Libs.junit.bom))
    testImplementation(Libs.junit.jupiter)
    testRuntimeOnly(Libs.junit.platform)
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    val generateBuildConstants by registering {
        val outputDir = layout.buildDirectory.dir("generated/sources/buildConstants")
        outputs.dir(outputDir)

        doLast {
            val targetPackage = when (metadata.type) {
                EnvironmentType.LOADER -> "loader"
                EnvironmentType.MODULE -> "module.${metadata.packageName}"
                EnvironmentType.OTHER -> metadata.packageName
            }.replace("-", "_")
            val targetClassPrefix = Utils.toPascalCase(
                when (metadata.type) {
                    EnvironmentType.LOADER -> "loader"
                    EnvironmentType.MODULE -> metadata.id
                    EnvironmentType.OTHER -> metadata.id
                }
            )
            val targetPath = targetPackage.replace('.', '/')

            var dependenciesArray = ArrayList<String>()

            if (metadata.type == EnvironmentType.MODULE) {
                Utils.findAdditionalDependencies().forEach { dependency ->
                    var workingDependency = dependency.replace("_", "-")
                    if (workingDependency.endsWith("-module")) {
                        dependenciesArray.add(workingDependency)
                    } else {
                        dependenciesArray.add("$workingDependency-module")
                    }
                }
            }

            dependenciesArray.remove("loader-module")
            dependenciesArray.remove("test-client-module")
            val dependenciesArrayString = dependenciesArray.joinToString(separator = ",")

            val constantsFile =
                File(outputDir.get().asFile, "gg/mmorealms/$targetPath/${targetClassPrefix}BuildConstants.java")
            constantsFile.parentFile.mkdirs()

            constantsFile.writeText(
                """
                |package gg.mmorealms.$targetPackage;
                |
                |public final class ${targetClassPrefix}BuildConstants {
                |    // Auto-generated file, do not modify!
                |    
                |    public static final String ID = "${metadata.id.replace("-", "_")}";
                |    public static final String VERSION = "$version";
                |    public static final String DEPENDENCIES = "$dependenciesArrayString";
                |}
            """.trimMargin()
            )
        }
    }

    sourceSets {
        main {
            java {
                srcDir(generateBuildConstants.map { it.outputs.files })
            }
        }
    }

    compileJava {
        dependsOn(generateBuildConstants)
    }

    register("printDependencyBytecode") {
        doLast {
            val cfg = configurations.named("runtimeClasspath").get()
            cfg.resolve().filter { it.extension == "jar" }.forEach { jar ->
                var maxMajor = 0
                ZipFile(jar).use { zip ->
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        val e = entries.nextElement()
                        if (!e.isDirectory && e.name.endsWith(".class")) {
                            zip.getInputStream(e).use { ins ->
                                val h = ByteArray(8)
                                if (ins.read(h) == 8) maxMajor = maxOf(maxMajor, classMajor(h))
                            }
                        }
                    }
                }
                if (maxMajor > 65) println("${jar.name} -> major $maxMajor")
            }
        }
    }

    compileJava {
        val warningsAsErrorsPropertyOverride = Utils.getProperty(Statics.WARNINGS_AS_ERRORS_PROPERTY_OVERRIDE)
        val warningsAsErrorsProperty = Utils.getProperty(Statics.WARNINGS_AS_ERRORS_PROPERTY)

        if (warningsAsErrorsPropertyOverride == "") {
            if (warningsAsErrorsProperty == "true") {
                options.compilerArgs.add("-Werror")
            }
        } else {
            if (warningsAsErrorsPropertyOverride == "true") {
                options.compilerArgs.add("-Werror")
            }
        }


    }

    test {
        jvmArgs("-XX:+EnableDynamicAgentLoading", "-Djdk.attach.allowAttachSelf=true")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations.all {
    resolutionStrategy {
        InternalLibs.ALL_LIBS.forEach { lib ->
            val version = lib.version()

            if (!Statics.LOGGED_FORCED_VERSIONS) {
                println("[MMORealms] Forcing ${lib.base} to version $version")
            }

            if (lib.hasCommon) {
                force("${lib.base}-common:$version")
            }
            if (lib.hasVelocity) {
                force("${lib.base}-velocity:$version")
            }
            if (lib.hasBackend) {
                force("${lib.base}-backend-common:$version")
                force("${lib.base}-backend-fabric:$version")
                force("${lib.base}-backend-neoforge:$version")
            }
        }

        force(Libs.raduvoinea.utils)

        Statics.LOGGED_FORCED_VERSIONS = true
    }
}

fun classMajor(array: ByteArray): Int = ((array[6].toInt() and 0xFF) shl 8) or (array[7].toInt() and 0xFF)
