import utils.Libs
import utils.Libs.aws

plugins {
    id("architectury_neoforge")
}

dependencies {
    shadowBundle(Libs.apache.commons.pool2)
    shadowBundle(Libs.jedis)

    shadowBundle(Libs.hibernate.core)
    shadowBundle(Libs.hibernate.validator)
    shadowBundle(Libs.hibernate.hikaricpCore)
    shadowBundle(Libs.hibernate.hikaricpHibernate)
    shadowBundle(Libs.mariadb.driver)

    shadowBundle(aws.sdk.s3) {
        exclude("io.netty")
    }

    shadowBundle(Libs.nbt)

    modApi(Libs.sgui.neoforge)
    include(Libs.sgui.neoforge)

    modApi(Libs.raduvoinea.commandmanager.backend.neoforge)
    include(Libs.raduvoinea.commandmanager.backend.neoforge)
}

tasks {
    shadowJar {
        dependencies {
            exclude {
                var shouldExclude = it.moduleGroup == "org.apache.logging.log4j" ||
                        it.moduleGroup == "org.antlr" ||
                        it.moduleGroup == "org.slf4j" ||
                        it.moduleGroup == "com.google.code.gson" ||
                        it.moduleGroup == "com.google.errorprone"

                shouldExclude
            }
        }
    }
}
