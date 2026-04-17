import utils.Libs

plugins {
    id("velocity")
}

dependencies {
    Libs.COMMON_DEPENDENCIES.forEach {
        api(it)
    }

    api(Libs.raduvoinea.commandmanager.velocity)
    compileOnlyApi(Libs.packetevents)
}
