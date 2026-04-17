class Utils {

    companion object {
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
        fun getProjectMetadata(projectName: String): Triplet<EnvironmentType, String, String> {
            val type: EnvironmentType = when (projectName.split(".")[0]) {
                "loader" -> EnvironmentType.LOADER
                "module" -> EnvironmentType.MODULE
                else -> EnvironmentType.OTHER
            }
            val module = when (type) {
                EnvironmentType.LOADER -> "loader"
                EnvironmentType.MODULE -> projectName.split(".")[1]
                EnvironmentType.OTHER -> projectName.split(".")[0]
            }
            val id = when (type) {
                EnvironmentType.LOADER -> "aaaaaaaaaa_loader"
                EnvironmentType.MODULE -> "${module}_module"
                EnvironmentType.OTHER -> module
            }

            return Triplet(type, module, id)
        }

        @JvmStatic
        fun getJarName(module: String, platform: String, version: String): String {
            if (module == "loader") {
                return "${toPascalCase(module)}-${toPascalCase(platform)}-${version}.jar"
            }
            return "${toPascalCase(module)}Module-${toPascalCase(platform)}-${version}.jar"
        }
    }

    data class Triplet<A, B, C>(val first: A, val second: B, val third: C)
}