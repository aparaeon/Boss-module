rootProject.name = "command-manager"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.parchmentmc.org")
        maven("https://files.minecraftforge.net/maven")
        mavenCentral()
    }
}

fun createProject(moduleArg: String, path: String) {
    var module: String = moduleArg

    if (!moduleArg.startsWith(":")) {
        module = ":$moduleArg"
    }

    println("[+] Adding module $module with path $path")

    val moduleDir = File(path)

    if (!moduleDir.exists() || !moduleDir.isDirectory) {
        println("[!] Could not create module $module. Path $path does not exist or is not a directory.")
        return
    }

    include(module)
    project(module).projectDir = file(path)
}

createProject("common", "src/common")
createProject("backend-common", "src/backend/common")
createProject("backend-neoforge", "src/backend/neoforge")
createProject("backend-fabric", "src/backend/fabric")
createProject("velocity", "src/velocity")
// TODO: Spigot (Bukkit) Support
