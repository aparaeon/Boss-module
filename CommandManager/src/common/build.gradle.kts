dependencies {
    // Dependencies
    compileOnlyApi(libs.raduvoinea.utils)
    compileOnlyApi(libs.luckperms)
    compileOnlyApi(libs.kyori.minimessage)

    // Annotations
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)
}
