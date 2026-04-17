import utils.Libs

plugins {
    id("architectury_common")
}

loom {
    accessWidenerPath.set(file("src/main/resources/loader.accesswidener"))
}

dependencies {
    compileOnlyApi(Libs.spark.common)
}
