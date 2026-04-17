import utils.Libs

plugins {
    id("architectury_fabric")
}

dependencies {
    Libs.COMMON_DEPENDENCIES.forEach {
        shadowBundle(it)
    }

    modApi(Libs.sgui.fabric)
    include(Libs.sgui.fabric)

    modApi(Libs.polymer.core)
    include(Libs.polymer.core)

    modApi(Libs.polymer.blocks)
    include(Libs.polymer.blocks)

    modApi(Libs.polymer.resource_pack)
    include(Libs.polymer.resource_pack)

    modApi(Libs.polymer.virtual_entity)
    include(Libs.polymer.virtual_entity)

    modApi(Libs.raduvoinea.commandmanager.backend.fabric)
    include(Libs.raduvoinea.commandmanager.backend.fabric)
}
