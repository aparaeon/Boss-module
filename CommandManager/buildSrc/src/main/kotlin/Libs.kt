object Libs {
    const val lombok = "org.projectlombok:lombok:1.18.36"
    const val jedis = "redis.clients:jedis:5.2.0"
    const val luckperms = "net.luckperms:api:5.4"
    const val jda = "net.dv8tion:JDA:5.3.0"
    const val minecraft = "net.minecraft:minecraft:1.21.1"
    const val parchment = "org.parchmentmc.data:parchment-1.21.1:2024.11.17"
    const val neoforge = "net.neoforged:neoforge:21.1.169"

    const val sgui = "eu.pb4:sgui:1.6.1+1.21.1"

    object jetbrains {
        const val annotations = "org.jetbrains:annotations:24.1.0"
    }

    object raduvoinea {
        const val utils = "com.raduvoinea:utils:1.5.8"
        object commandmanager {
            private const val version = "1.1.23"

            const val common = "com.raduvoinea:command-manager-common:$version"
            const val fabric = "com.raduvoinea:command-manager-fabric-21:$version"
            const val velocity = "com.raduvoinea:command-manager-velocity:$version"
        }
    }

    object apache{
        object commons{
            const val compress = "org.apache.commons:commons-compress:1.27.1"
            const val lang3 = "org.apache.commons:commons-lang3:3.17.0"
            const val pool2 = "org.apache.commons:commons-pool2:2.12.0"
            const val io = "commons-io:commons-io:2.18.0"
        }
    }

    object google{
        const val gson = "com.google.code.gson:gson:2.11.0"
        const val guava = "com.google.guava:guava:33.4.0-jre"
    }

    object kyori{
        const val minimessage = "net.kyori:adventure-text-minimessage:4.20.0"
    }

    object aws{
        object sdk{
            const val s3 = "software.amazon.awssdk:s3:2.27.21"
            const val netty = "software.amazon.awssdk:netty-nio-client:2.27.21"
        }
    }

    object fabric{
        const val loader = "net.fabricmc:fabric-loader:0.16.10"
        const val api = "net.fabricmc.fabric-api:fabric-api:0.115.0+1.21.1"
    }

    object architectury{
        const val common = "dev.architectury:architectury:13.0.8"
        const val fabric = "dev.architectury:architectury-fabric:13.0.8"
        const val neoforge = "dev.architectury:architectury-neoforge:13.0.8"
    }
}
