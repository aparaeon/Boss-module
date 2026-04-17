import utils.InternalLibs

plugins {
    id("architectury_common")
}

dependencies{
    modCompileOnly(InternalLibs.client.backend!!.common)
}
