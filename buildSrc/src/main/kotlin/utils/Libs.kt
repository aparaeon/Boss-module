package utils

object Libs {
    const val minecraftVersion = "1.21.1"
    const val fabricLoaderVersion = "0.16.10"
    const val fabricApiVesion = "0.115.0+1.21.1"
    const val neoforgeVersion = "21.1.180"

    const val lombok = "org.projectlombok:lombok:1.18.36"
    const val jedis = "redis.clients:jedis:5.2.0"
    const val luckperms = "net.luckperms:api:5.4"
    const val jda = "net.dv8tion:JDA:5.3.0"
    const val minecraft = "net.minecraft:minecraft:$minecraftVersion"
    const val parchment = "org.parchmentmc.data:parchment-$minecraftVersion:2024.11.17"
    const val neoforge = "net.neoforged:neoforge:$neoforgeVersion"
    const val cobblemon = "com.cobblemon:cobblemon:1.7.0"
    const val votifierPlus = "com.vexsoftware:votifier-plus:1.4.1"
    const val nbt = "io.github.ensgijs:ens-nbt:0.1"
    const val c3p0 = "com.mchange:c3p0:0.10.2"
    const val osgi = "org.osgi:org.osgi.framework:1.10.0"
    const val pixelmon = "com.pixelmon:pixelmon:9.3.14"
    const val packetevents = "com.github.retrooper:packetevents-velocity:2.8.0"
    const val worldedit = "com.sk89q.worldedit:worldedit-core:7.3.8"

    object spark {
        const val common = "me.lucko:spark-common:1.10.142"
    }

    object sgui {
        private const val version = "1.9.1"

        const val fabric = "eu.pb4:sgui-fabric:$version"
        const val neoforge = "eu.pb4:sgui-neoforge:$version"
    }

    // Fabric only
    object polymer {
        private const val version = "0.9.18+$minecraftVersion"

        const val core = "eu.pb4:polymer-core:$version"
        const val blocks = "eu.pb4:polymer-blocks:$version"
        const val resource_pack = "eu.pb4:polymer-resource-pack:$version"
        const val virtual_entity = "eu.pb4:polymer-virtual-entity:$version"
    }

    object netty {
        const val transport = "io.netty:netty-transport-native-epoll:4.1.112.Final:linux-x86_64"
    }

    object log4j {
        private const val version = "2.24.3"

        const val api = "org.apache.logging.log4j:log4j-api:$version"
        const val core = "org.apache.logging.log4j:log4j-core:$version"
        const val slf4j = "org.apache.logging.log4j:log4j-slf4j-impl:$version"
    }

    object mariadb {
        const val driver = "org.mariadb.jdbc:mariadb-java-client:3.5.2"
    }

    object postgresql {
        const val driver = "org.postgresql:postgresql:42.7.10"
    }

    object jetbrains {
        const val annotations = "org.jetbrains:annotations:24.1.0"
    }

    object raduvoinea {
        const val utils = "com.raduvoinea:utils:1.25.0"

        object commandmanager {
            private const val version = "1.7.1"

            const val common = "com.raduvoinea:command-manager-common:$version"
            const val velocity = "com.raduvoinea:command-manager-velocity:$version"

            object backend {
                const val common = "com.raduvoinea:command-manager-backend-common:$version"
                const val fabric = "com.raduvoinea:command-manager-backend-fabric:$version"
                const val neoforge = "com.raduvoinea:command-manager-backend-neoforge:$version"
            }
        }
    }

    object apache {
        object commons {
            const val compress = "org.apache.commons:commons-compress:1.27.1"
            const val lang3 = "org.apache.commons:commons-lang3:3.17.0"
            const val pool2 = "org.apache.commons:commons-pool2:2.12.0"
            const val io = "commons-io:commons-io:2.18.0"
        }
    }

    object google {
        const val gson = "com.google.code.gson:gson:2.11.0"
        const val guava = "com.google.guava:guava:33.4.0-jre"
        const val protobuf = "com.google.protobuf:protobuf-javalite:4.31.1"
    }

    object kyori {
        const val minimessage = "net.kyori:adventure-text-minimessage:4.20.0"
        const val modCommon = "net.kyori:adventure-platform-mod-shared-fabric-repack:6.0.0"
        const val fabric = "net.kyori:adventure-platform-fabric:5.14.1"
    }

    object aws {
        object sdk {
            const val s3 = "software.amazon.awssdk:s3:2.27.21"
            const val netty = "software.amazon.awssdk:netty-nio-client:2.27.21"
        }
    }

    object fabric {
        const val loader = "net.fabricmc:fabric-loader:0.16.10"
        const val api = "net.fabricmc.fabric-api:fabric-api:0.115.0+$minecraftVersion"
    }

    object architectury {
        const val version = "13.0.8"

        const val common = "dev.architectury:architectury:$version"
        const val fabric = "dev.architectury:architectury-fabric:$version"
        const val neoforge = "dev.architectury:architectury-neoforge:$version"
    }

    object junit {
        private const val version = "5.11.4"

        const val jupiter = "org.junit.jupiter:junit-jupiter:5.$version"
        const val bom = "org.junit:junit-bom:$version"
        const val platform = "org.junit.platform:junit-platform-launcher"
    }

    object jackson {
        private const val version = "2.18.3"

        const val core = "com.fasterxml.jackson.core:jackson-core:$version"
        const val databind = "com.fasterxml.jackson.core:jackson-databind:$version"
        const val annotations = "com.fasterxml.jackson.core:jackson-annotations:$version"
    }

    object hibernate { // TODO maybe update as newer hibernate seems to offer a bit better performance (8% uplift in some cases) - https://quarkus.io/blog/hibernate7-on-quarkus/#:~:text=improved%20the%20performance%20by%208%25
        const val core = "org.hibernate:hibernate-core:6.6.11.Final"
        const val validator = "org.hibernate.validator:hibernate-validator:8.0.2.Final"
        const val hikaricpHibernate = "org.hibernate:hibernate-hikaricp:6.6.11.Final"
        const val hikaricpCore = "com.zaxxer:HikariCP:7.0.2"
    }

    object jakarta {
        const val el = "jakarta.el:jakarta.el-api:6.0.1"
        const val api = "jakarta.inject:jakarta.inject-api:2.0.1"
    }

    object bytebuddy {
        private const val version = "1.14.9"

        const val byteBuddy = "net.bytebuddy:byte-buddy:$version"
        const val agent = "net.bytebuddy:byte-buddy-agent:$version"
    }

    val COMMON_DEPENDENCIES = listOf(
        raduvoinea.commandmanager.common,
        raduvoinea.utils,

        apache.commons.compress,
        apache.commons.lang3,
        apache.commons.pool2,
        apache.commons.io,

        jedis,

        google.gson,
        google.guava,
        google.protobuf,

        jackson.core,
        jackson.databind,
        jackson.annotations,

        hibernate.core,
        hibernate.validator,
        hibernate.hikaricpCore,
        hibernate.hikaricpHibernate,
        jakarta.el,
        jakarta.api,

        c3p0,
        osgi,

        mariadb.driver,
        postgresql.driver,

        log4j.api,
        log4j.core,
        log4j.slf4j,

        aws.sdk.s3,
        aws.sdk.netty,
        netty.transport,

        nbt,

        bytebuddy.byteBuddy,
    )
}