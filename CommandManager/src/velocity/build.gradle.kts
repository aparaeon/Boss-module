@file:Suppress("VulnerableLibrariesLocal")

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    api(project(":common"))
    api(libs.raduvoinea.utils)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)
}


