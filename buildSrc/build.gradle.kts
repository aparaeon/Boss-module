plugins {
    id("java")
    id("java-library")
    `kotlin-dsl`
}

repositories {
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

    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.parchmentmc.org") }
    maven { url = uri("https://maven.minecraftforge.net/") }
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://files.minecraftforge.net/maven/") }
}

dependencies {
    implementation("org.jetbrains.gradle.plugin.idea-ext:org.jetbrains.gradle.plugin.idea-ext.gradle.plugin:1.1.10")
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.4.1")
    implementation("dev.architectury.loom:dev.architectury.loom.gradle.plugin:1.10-SNAPSHOT")
    implementation("architectury-plugin:architectury-plugin.gradle.plugin:3.4-SNAPSHOT")
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.2.0")
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

