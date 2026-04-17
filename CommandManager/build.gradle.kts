plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

var _version = libs.versions.version.get()
var _group = libs.versions.group.get()

version = _version
group = _group

fun getProperty(name: String): String {
    if (project.hasProperty(name)) {
        return project.findProperty(name) as String
    }

    val envName = name.uppercase().replace(".", "_")

    if (System.getenv().containsKey(envName)) {
        return System.getenv(envName) as String
    }

    return ""
}

fun DependencyHandlerScope.applyDependencies() {
    // Annotations
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

fun getProperty(name: String, defaultValue: String = ""): String {
    if (rootProject.hasProperty(name)) {
        return rootProject.findProperty(name) as String
    }

    val envName = name.uppercase().replace(".", "_")

    if (System.getenv().containsKey(envName)) {
        return System.getenv(envName) as String
    }

    return defaultValue
}

fun RepositoryHandler.applyRepositories() {
    mavenLocal()

    val useProxy = getProperty("gg.mmorealms.proxy")
    println("MMORealms proxy: $useProxy")

    if (useProxy == "true") {
        val privateProxyUrl = getProperty("gg.mmorealms.proxy.url.private")
        val publicProxyUrl = getProperty("gg.mmorealms.proxy.url.public")
        val proxyUsername = getProperty("gg.mmorealms.proxy.username")
        val proxyPassword = getProperty("gg.mmorealms.proxy.password")

        println("Using MMORealms Proxy Repositories at $privateProxyUrl and $publicProxyUrl with username $proxyUsername")

        maven(url = getProperty("gg.mmorealms.proxy.url.private")) {
            name = "MMORealms-Repository-Private-Proxy"
            credentials(PasswordCredentials::class) {
                username = proxyUsername
                password = proxyPassword
            }
        }

        maven(url = getProperty("gg.mmorealms.proxy.url.public")) {
            name = "MMORealms-Repository-Public-Proxy"
            credentials(PasswordCredentials::class) {
                username = proxyUsername
                password = proxyPassword
            }
        }
    } else {
        println("Using MMORealms Repositories with username ${getProperty("gg.mmorealms.username")}")

        maven(url = getProperty("gg.mmorealms.url")) {
            name = "MMORealms-Repository-Private"
            credentials(PasswordCredentials::class) {
                username = getProperty("gg.mmorealms.username")
                password = getProperty("gg.mmorealms.password")
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

repositories {
    applyRepositories()
}

dependencies {
    applyDependencies()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = _group
    version = _version

    repositories {
        applyRepositories()
    }

    dependencies {
        applyDependencies()
    }

    tasks {
        java {
            withSourcesJar()
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "${rootProject.name}-${base.archivesName.get()}"
                from(components["java"])
            }
        }

        repositories {
            if (project.properties["com.raduvoinea.publish"] == "true") {
                maven(url = (project.findProperty("com.raduvoinea.url") ?: "") as String) {
                    name = "RaduVoinea"
                    credentials(PasswordCredentials::class) {
                        username = (project.findProperty("com.raduvoinea.auth.username") ?: "") as String
                        password = (project.findProperty("com.raduvoinea.auth.password") ?: "") as String
                    }
                }
            }

            if (project.properties["generic.publish"] == "true") {
                maven(url = (project.findProperty("generic.url") ?: "") as String) {
                    name = "Generic"
                    credentials(PasswordCredentials::class) {
                        username = (project.findProperty("generic.auth.username") ?: "") as String
                        password = (project.findProperty("generic.auth.password") ?: "") as String
                    }
                }
            }
        }
    }
}
