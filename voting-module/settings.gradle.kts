pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.parchmentmc.org") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://files.minecraftforge.net/maven/") }
    }
}

rootProject.name = "voting-module"

fun createProject(moduleArg: String, path: String) {
    var module: String = moduleArg

    if (!moduleArg.startsWith(":")) {
        module = ":$moduleArg"
    }

    println("[+] Adding module $module with path $path")

    val moduleDir = File(rootDir, path)

    if (!moduleDir.exists() || !moduleDir.isDirectory) {
        println("[!] Could not create module $module. Path $path does not exist or is not a directory.")
        return
    }

    include(module)
    project(module).projectDir = file(path)
}

createProject("common", "src/common")
createProject("backend-common", "src/backend/common")
createProject("backend-fabric", "src/backend/fabric")
createProject("backend-neoforge", "src/backend/neoforge")
createProject("velocity", "src/velocity")
