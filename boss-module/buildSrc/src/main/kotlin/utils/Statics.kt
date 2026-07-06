package utils

object Statics {

    var LOGGED_FORCED_VERSIONS: Boolean = false
    var LOGGED_NO_LOCAL_DEPENDENCIES: Boolean = false

    val MODULE_DEPENDENICES: String = "module_dependencies"

    val PUBLISH_PROPERTY: String = "gg.mmorealms.publish"
    val PUBLISH_PROPERTY_REMOTE: String = "remote" // value for PUBLISH_PROPERTY
    val PUBLISH_PROPERTY_LOCAL: String = "local" // value for PUBLISH_PROPERTY

    val PUBLISH_VERSION_PROPERTY: String = "gg.mmorealms.publish.version"
    val PUBLISH_URL_PROPERTY: String = "gg.mmorealms.url"
    val PUBLISH_USERNAME_PROPERTY: String = "gg.mmorealms.username"
    val PUBLISH_PASSWORD_PROPERTY: String = "gg.mmorealms.password"
    val WARNINGS_AS_ERRORS_PROPERTY: String = "gg.mmorealms.warnings_as_errors"
    val WARNINGS_AS_ERRORS_PROPERTY_OVERRIDE: String = "gg.mmorealms.warnings_as_errors_override"
    val LOCAL_DEPENDENCIES: String = "LOCAL_DEPENDENCIES"

    val ALL_SETTINGS: List<String> = listOf(
        PUBLISH_PROPERTY,
        PUBLISH_VERSION_PROPERTY,
        PUBLISH_URL_PROPERTY,
        PUBLISH_USERNAME_PROPERTY,
//        PUBLISH_PASSWORD_PROPERTY, // Disabled as it's senstivie
        WARNINGS_AS_ERRORS_PROPERTY,
        WARNINGS_AS_ERRORS_PROPERTY_OVERRIDE,
        LOCAL_DEPENDENCIES,
    )

}