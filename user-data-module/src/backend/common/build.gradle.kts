plugins {
    id("architectury_common")
}

loom{
    accessWidenerPath.set(file("src/main/resources/user_data_module.accesswidener"))
}