plugins {
    id("architectury_common")
}

loom{
    accessWidenerPath.set(file("src/main/resources/realms.accesswidener"))
}
