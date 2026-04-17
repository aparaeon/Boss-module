import utils.Libs

plugins {
    id("architectury_fabric")
    kotlin("jvm")
}

dependencies {
    modCompileOnlyApi(Libs.cobblemon)
}