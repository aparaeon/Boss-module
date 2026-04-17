import utils.Libs

plugins {
    id("architectury_fabric")
}

dependencies {
    modCompileOnlyApi(Libs.cobblemon)

    modCompileOnlyApi(Libs.polymer.core)
    modCompileOnlyApi(Libs.polymer.blocks)
    modCompileOnlyApi(Libs.polymer.resource_pack)
    modCompileOnlyApi(Libs.polymer.virtual_entity)
}