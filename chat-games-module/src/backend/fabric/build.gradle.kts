import utils.Libs

plugins {
    id("architectury_fabric")
}

dependencies {
    modCompileOnlyApi(Libs.cobblemon)
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
}