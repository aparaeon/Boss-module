package utils

import org.gradle.api.Project
import utils.InternalLibs.LOCAL_DEPENDENCY_VERSION
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object Utils {

    lateinit var rootProject: Project

    data class ProjectMetadata(
        val type: EnvironmentType, // Fabric
        val name: String, // ex. SomeExampleModule-Fabric
        val id: String, // ex. some-example-module
        val platform: String, // ex. fabric
        val packageName: String = id.replace("-module", "").replace("-", "_") // ex. some_example
    )

    @JvmStatic
    fun toPascalCase(input: String): String {
        return input
            .split(Regex("[\\s_\\-]+")) // Split by spaces, underscores, hyphens
            .filter { it.isNotBlank() }
            .joinToString("") { word ->
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }

    @JvmStatic
    fun getProjectMetadata(platform: String): ProjectMetadata {
        val projectName = rootProject.name

        val type = when {
            projectName.lowercase().contains("loader") -> EnvironmentType.LOADER
            projectName.lowercase().contains("module") -> EnvironmentType.MODULE
            else -> EnvironmentType.OTHER
        }

        val name = "${toPascalCase(projectName)}-${toPascalCase(platform)}"

        val id = when (type) {
            EnvironmentType.LOADER -> "aaaaaaaaaa-loader"
            EnvironmentType.MODULE -> projectName
            EnvironmentType.OTHER -> projectName
        }

        return ProjectMetadata(
            type = type,
            platform = platform.lowercase(),
            name = name,
            id = id,
        )
    }

    @JvmStatic
    fun getJarName(metadata: ProjectMetadata): String {
        return "${rootProject.name}-${metadata.platform}-${rootProject.version}.jar"
    }

    @JvmStatic
    fun findAdditionalDependencies(): List<String> {
        var dependencies = run {
            if (!rootProject.properties.containsKey(Statics.MODULE_DEPENDENICES)) {
                return@run emptyList()
            }


            val moduleDependencies: String = rootProject.properties[Statics.MODULE_DEPENDENICES] as String
            if (moduleDependencies.isEmpty()) {
                return@run emptyList()
            }

            return@run moduleDependencies
                    .split(",")
                    .map { it.trim() }
                    .toList()
        }
        dependencies = ArrayList(dependencies)

        when (getProjectMetadata("").type) {
            EnvironmentType.MODULE -> {
                dependencies.add("loader")
            }

            EnvironmentType.LOADER, EnvironmentType.OTHER -> {

            }
        }

        println("Dependencies: $dependencies")
        return dependencies
    }


    @JvmStatic
    fun findLocalDependencies(): Map<String, String> {
        var localDependenciesData = System.getenv(Statics.LOCAL_DEPENDENCIES)

        if (localDependenciesData.isNullOrEmpty()) {
            val localDependenciesFile = rootProject.file("local.dependencies")

            if (!localDependenciesFile.exists()) {
                if (!Statics.LOGGED_NO_LOCAL_DEPENDENCIES) {
                    println("No local dependencies found")
                    Statics.LOGGED_NO_LOCAL_DEPENDENCIES = true
                }
                return emptyMap()
            }

            localDependenciesData = localDependenciesFile.readText().trim()
        }

        if (localDependenciesData.isEmpty()) {
            return emptyMap()
        }

        val result = mutableMapOf<String, String>()

        for (dependency in localDependenciesData.split(",")) {
            val split = dependency.split("=")
            val id = split[0]
                .replace("-module", "")
                .replace("-", "_")
                .trim()
                .lowercase()
            val version = if (split.size == 2) {
                split[1].trim()
            } else {
                LOCAL_DEPENDENCY_VERSION
            }

            result[id] = version
        }

        return result
    }

    @JvmStatic
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


    @JvmStatic
    fun getVersion(): String {
        return getProperty(Statics.PUBLISH_VERSION_PROPERTY, defaultValue = "0.0.0-local")
    }


    @JvmStatic
    fun bumpVersion(version: String): String {
        val versionParts = version.split(".").map { it.toIntOrNull() ?: 0 }.toMutableList()

        if (versionParts.size < 3) {
            while (versionParts.size < 3) {
                versionParts.add(0)
            }
        }

        versionParts[2] = versionParts[2] + 1

        return versionParts.joinToString(".")
    }

    @JvmStatic
    fun getMaxVersion(version1: String, version2: String): String {
        val parts1 = version1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = version2.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLength = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxLength) {
            val v1 = if (i < parts1.size) parts1[i] else 0
            val v2 = if (i < parts2.size) parts2[i] else 0

            if (v1 > v2) return version1
            if (v2 > v1) return version2
        }

        return version1
    }

    @JvmStatic
    fun getLatestVersion(
        artifactId: String,
        username: String?,
        password: String?,
        baseUrl: String = "https://repo.mmorealms.gg",
        groupId: String = "gg.mmorealms",
        repository: String = "maven-releases"
    ): String {
        val searchUrl =
            "$baseUrl/service/rest/v1/search?repository=$repository&group=$groupId&name=$artifactId&sort=version&direction=desc"
        val url = URL(searchUrl)
        val connection = url.openConnection() as HttpURLConnection

        if (!username.isNullOrEmpty() && password != null) {
            val auth = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
            connection.setRequestProperty("Authorization", "Basic $auth")
        }

        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Failed to fetch artifact versions: HTTP ${connection.responseCode}")
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }

        val itemsIndex = response.indexOf("\"items\"")
        if (itemsIndex == -1) {
            println("No items found in response: $response")
            return "0.0.0"
        }

        val versionRegex = """"version"\s*:\s*"([^"]+)"""".toRegex()
        val matches = versionRegex.findAll(response).toList()

        if (matches.isEmpty()) {
            println("No version matches found in response: $response")
            return "0.0.0"
        }

        return matches[0].groups[1]?.value ?: throw Exception("No version found")
    }

    @JvmStatic
    @Deprecated("Legacy - For removal", level = DeprecationLevel.WARNING)
    fun readVersion(): String {
        val versionFile = rootProject.file("version.txt")
        var version = "1.0.0";

        if (versionFile.exists()) {
            version = versionFile.readText()

            if (version.isBlank() || version.isEmpty()) {
                version = "1.0.0"
            }
        } else {
            version = "1.0.0"
        }

        return version
    }

    @JvmStatic
    @Deprecated("Legacy - For removal", level = DeprecationLevel.WARNING)
    fun writeVersion(version: String) {
        val versionFile = rootProject.file("version.txt")

        if (!versionFile.exists()) {
            versionFile.createNewFile()
        }

        versionFile.writeText(version)
    }

    @JvmStatic
    @Deprecated("Legacy - For removal", level = DeprecationLevel.WARNING)
    fun bumpVersion() {
        val platforms = listOf("backend-common", "backend-fabric", "backend-neoforge", "velocity", "common")
        var remoteVersion = "1.0.0"

        for (platform in platforms) {
            val currentPlatformRemoteVersion = getLatestVersion(
                artifactId = "${rootProject.name}-$platform",
                username = getProperty("gg.mmorealms.username"),
                password = getProperty("gg.mmorealms.password"),
            )

            println("Remote version ($platform): $remoteVersion")
            remoteVersion = getMaxVersion(remoteVersion, currentPlatformRemoteVersion)
        }

        val localVersion = readVersion()

        println("Remote version: $remoteVersion")
        println("Local version: $localVersion")
        var version = getMaxVersion(remoteVersion, localVersion)

        version = bumpVersion(version)
        writeVersion(version)

        println("New version: $version")
    }

}