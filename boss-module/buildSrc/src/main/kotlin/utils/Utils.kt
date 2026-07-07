package utils

import org.gradle.api.Project
import utils.InternalLibs.LOCAL_DEPENDENCY_VERSION
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

object Utils {

    private var _rootProject: Project? = null
    var rootProject: Project
        get() = _rootProject!!
        set(value) {
            if (_rootProject !== value) {
                _rootProject = value
                localDependenciesCache = null
                localVersionCache = null
                latestLocalVersionCache = null
                internalLibsVersionsCache = null
            }
        }

    private var localDependenciesCache: Map<String, String>? = null
    private var localVersionCache: String? = null
    private var latestLocalVersionCache: String? = null
    private var internalLibsVersionsCache: Map<String, String>? = null

    private fun readFileTracked(relativePath: String): String {
        val file = rootProject.file(relativePath)
        if (!file.exists()) return ""

        val fileProvider = rootProject.layout.projectDirectory.file(relativePath)
        return rootProject.providers.fileContents(fileProvider).asText.get().trim()
    }

    data class ProjectMetadata(
        val type: EnvironmentType,
        val name: String,
        val id: String,
        val platform: String,
        val packageName: String = id.replace("-module", "").replace("-", "_")
    )

    @JvmStatic
    fun toPascalCase(input: String): String {
        return input
            .split(Regex("[\\s_\\-]+"))
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
        localDependenciesCache?.let { return it }

        val result = mutableMapOf<String, String>()

        val localDependenciesData = readLocalDependenciesData()

        if (!localDependenciesData.isNullOrEmpty()) {
            for (dependency in localDependenciesData.split(",")) {
                if (dependency.isBlank()) continue

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
        }

        val branch = getCurrentBranch()
        if (branch != null) {
            val branchVersionPrefix = "0.0.0-" + branch.replace("/", "-") + "-"

            for (dep in findAdditionalDependencies()) {
                val id = dep
                    .replace("-module", "")
                    .replace("-", "_")
                    .trim()
                    .lowercase()

                if (result.containsKey(id)) {
                    continue
                }

                val lib = InternalLibs.findDependency(dep) ?: continue
                if (!lib.hasCommon) {
                    continue
                }

                val artifactId = lib.base.split(":").getOrNull(1)?.plus("-common") ?: continue
                val username = getProperty(Statics.PUBLISH_USERNAME_PROPERTY)
                val password = getProperty(Statics.PUBLISH_PASSWORD_PROPERTY)

                val version = try {
                    getLatestVersion(
                        artifactId = artifactId,
                        username = username,
                        password = password,
                        versionPrefix = branchVersionPrefix,
                    )
                } catch (e: Exception) {
                    println("Failed to resolve branch version for $artifactId on branch '$branch': ${e.message}")
                    null
                }

                if (version != null) {
                    result[id] = version
                }
            }
        }

        localDependenciesCache = result
        return result
    }

    private fun readLocalDependenciesData(): String? {
        var localDependenciesData = System.getenv(Statics.LOCAL_DEPENDENCIES)

        if (localDependenciesData.isNullOrEmpty()) {
            localDependenciesData = readFileTracked("local.dependencies")

            if (localDependenciesData.isEmpty()) {
                if (!Statics.LOGGED_NO_LOCAL_DEPENDENCIES) {
                    println("No local dependencies found")
                    Statics.LOGGED_NO_LOCAL_DEPENDENCIES = true
                }
                return null
            }
        }

        return if (localDependenciesData.isEmpty()) null else localDependenciesData
    }

    @JvmStatic
    fun readInternalLibsVersions(): Map<String, String> {
        internalLibsVersionsCache?.let { return it }

        val result = mutableMapOf<String, String>()
        val content = readFileTracked("buildSrc/internal-libs.versions")

        if (content.isEmpty()) {
            internalLibsVersionsCache = result
            return result
        }

        for (line in content.lines()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

            val eq = trimmed.indexOf('=')
            if (eq <= 0) continue

            val rawId = trimmed.substring(0, eq).trim()
            val version = trimmed.substring(eq + 1).trim()
            if (rawId.isEmpty() || version.isEmpty()) continue

            val id = rawId.replace("-module", "").replace("-", "_").trim().lowercase()
            result[id] = version
        }

        internalLibsVersionsCache = result
        return result
    }

    private fun getCurrentBranch(): String? {
        return try {
            val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
                .directory(rootProject.projectDir)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText().trim() }
            val exitCode = process.waitFor()

            if (exitCode == 0 && output.isNotEmpty() && output != "HEAD") output else null
        } catch (e: Exception) {
            println("Failed to determine current git branch: ${e.message}")
            null
        }
    }

    @JvmStatic
    fun getProperty(name: String, defaultValue: String = ""): String {
        val valueFromFile = getPropertyFromFile(name)
        val valueFromEnv = getPropertyFromEnv(name)

        if (valueFromEnv.isNotEmpty()) {
            return valueFromEnv
        }

        if (valueFromFile.isNotEmpty()) {
            return valueFromFile
        }

        return defaultValue
    }

    fun getPropertyFromFile(name: String): String {
        if (rootProject.hasProperty(name)) {
            return rootProject.findProperty(name) as String
        }
        return ""
    }

    fun getPropertyFromEnv(name: String): String {
        val envName = name.uppercase().replace(".", "_")

        if (System.getenv().containsKey(envName)) {
            return System.getenv(envName) as String
        }
        return ""
    }


    @JvmStatic
    fun getVersion(): String {
        return getProperty(Statics.PUBLISH_VERSION_PROPERTY, defaultValue = getLocalVersion())
    }

    @JvmStatic
    fun getLocalVersion(): String {
        localVersionCache?.let { return it }

        val version = "0.0.0-local-${getMaxLocalBuild() + 1}"
        localVersionCache = version
        return version
    }

    @JvmStatic
    fun getLatestLocalVersion(): String {
        latestLocalVersionCache?.let { return it }

        val maxBuild = getMaxLocalBuild()
        val version = if (maxBuild > 0) "0.0.0-local-$maxBuild" else "0.0.0-local-1"
        latestLocalVersionCache = version
        return version
    }

    private fun getMaxLocalBuild(): Int {
        val m2Dir = File(System.getProperty("user.home"), ".m2/repository/gg/mmorealms")
        val versionRegex = Regex("""0\.0\.0-local-(\d+)""")

        var maxBuild = 0

        if (m2Dir.isDirectory) {
            m2Dir.walkTopDown().forEach { file ->
                if (!file.isDirectory) return@forEach

                val match = versionRegex.matchEntire(file.name) ?: return@forEach
                val build = match.groupValues[1].toIntOrNull() ?: return@forEach
                if (build > maxBuild) {
                    maxBuild = build
                }
            }
        }

        return maxBuild
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
        repository: String = "maven-releases",
        versionPrefix: String? = null
    ): String? {
        var continuationToken: String? = null
        var latestMatch: String? = null
        var maxBuild = -1

        val versionParam = versionPrefix?.let { "&version=" + URLEncoder.encode("$it*", "UTF-8") } ?: ""

        do {
            val tokenParam = continuationToken?.let { "&continuationToken=" + URLEncoder.encode(it, "UTF-8") } ?: ""
            val searchUrl =
                "$baseUrl/service/rest/v1/search?repository=$repository&group=$groupId&name=$artifactId&sort=version&direction=desc$versionParam$tokenParam"

            val response = fetchRepoResponse(searchUrl, username, password)

            val versionRegex = """"version"\s*:\s*"([^"]+)"""".toRegex()
            val versions = versionRegex.findAll(response).mapNotNull { it.groups[1]?.value }.toList()

            if (versionPrefix == null) {
                if (latestMatch == null && versions.isNotEmpty()) {
                    latestMatch = versions[0]
                }
            } else {
                for (version in versions) {
                    val build = version.substring(versionPrefix.length).toIntOrNull() ?: continue
                    if (build > maxBuild) {
                        maxBuild = build
                        latestMatch = "$versionPrefix$build"
                    }
                }
            }

            continuationToken = parseContinuationToken(response)
        } while (continuationToken != null)

        if (latestMatch == null) {
            println("No versions found for $groupId:$artifactId" + (versionPrefix?.let { " matching prefix '$it'" } ?: ""))
            return if (versionPrefix == null) "0.0.0" else null
        }

        return latestMatch
    }

    private fun fetchRepoResponse(searchUrl: String, username: String?, password: String?): String {
        val url = URL(searchUrl)
        val connection = url.openConnection() as HttpURLConnection

        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        if (!username.isNullOrEmpty() && password != null) {
            val auth = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
            connection.setRequestProperty("Authorization", "Basic $auth")
        }

        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Failed to fetch artifact versions: HTTP ${connection.responseCode}")
        }

        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun parseContinuationToken(response: String): String? {
        val tokenRegex = """"continuationToken"\s*:\s*(?:"([^"]+)"|null)""".toRegex()
        val match = tokenRegex.find(response) ?: return null
        return match.groups[1]?.value
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
            remoteVersion = getMaxVersion(remoteVersion, currentPlatformRemoteVersion ?: "0.0.0")
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
