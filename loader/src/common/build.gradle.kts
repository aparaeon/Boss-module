import utils.Libs

plugins {
    id("common")

    id("com.gradleup.shadow")
}

dependencies {
    Libs.COMMON_DEPENDENCIES.forEach {
        compileOnlyApi(it)
    }

    api(Libs.raduvoinea.utils)
    compileOnlyApi(Libs.raduvoinea.commandmanager.common)
}
