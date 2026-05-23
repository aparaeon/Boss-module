package utils

import kotlin.reflect.KProperty1

object InternalLibs {

    val LOCAL_DEPENDENCY_VERSION = "1000.0.0"
    val ALL_LIBS: ArrayList<InternalLib> = ArrayList()

    //@formatter:off
    // NOP
    val nop:              InternalLib = InternalLib(base = "gg.mmorealms:nop-module",               version = "1.0.2")

    // Loader
    val loader:           InternalLib = InternalLib(base = "gg.mmorealms:loader",                   version = "1.0.197")

    // Client
    val client:           InternalLib = InternalLib(base = "gg.mmorealms:client",                   version = "1.0.53", hasCommon = false, hasVelocity = false)
    val testClient:       InternalLib = InternalLib(base = "gg.mmorealms:test-client",              version = "1.0.19", hasCommon = false, hasVelocity = false)

    // Modules
    val core:             InternalLib = InternalLib(base = "gg.mmorealms:core-module",              version = "1.0.152")
    val analytics:        InternalLib = InternalLib(base = "gg.mmorealms:analytics-module",         version = "1.0.52")
    val economy:          InternalLib = InternalLib(base = "gg.mmorealms:economy-module",           version = "1.0.56")
    val pokemon:          InternalLib = InternalLib(base = "gg.mmorealms:pokemon-module",           version = "1.0.72")
    val auctionHouse:     InternalLib = InternalLib(base = "gg.mmorealms:auction-house-module",     version = "1.0.72")
    val breeding:         InternalLib = InternalLib(base = "gg.mmorealms:breeding-module",          version = "1.0.57")
    val userData:         InternalLib = InternalLib(base = "gg.mmorealms:user-data-module",         version = "1.0.62")
    val chat:             InternalLib = InternalLib(base = "gg.mmorealms:chat-module",              version = "1.0.70")
    val crates:           InternalLib = InternalLib(base = "gg.mmorealms:crates-module",            version = "1.0.60")
    val discordChat:      InternalLib = InternalLib(base = "gg.mmorealms:discord-chat-module",      version = "1.0.48")
    val discordLink:      InternalLib = InternalLib(base = "gg.mmorealms:discord-link-module",      version = "1.0.53")
    val essentials:       InternalLib = InternalLib(base = "gg.mmorealms:essentials-module",        version = "1.0.102")
    val realms:           InternalLib = InternalLib(base = "gg.mmorealms:realms-module",            version = "1.0.97")
    val homes:            InternalLib = InternalLib(base = "gg.mmorealms:homes-module",             version = "1.0.56")
    val hunts:            InternalLib = InternalLib(base = "gg.mmorealms:hunts-module",             version = "1.0.64")
    val kits:             InternalLib = InternalLib(base = "gg.mmorealms:kits-module",              version = "1.0.59")
    val legendaries:      InternalLib = InternalLib(base = "gg.mmorealms:legendaries-module",       version = "1.0.56")
    val limbo:            InternalLib = InternalLib(base = "gg.mmorealms:limbo-module",             version = "1.0.33") // Deprecated
    val moderation:       InternalLib = InternalLib(base = "gg.mmorealms:moderation-module",        version = "1.0.49")
    val plushies:         InternalLib = InternalLib(base = "gg.mmorealms:plushies-module",          version = "1.0.88")
    val pokedexRewards:   InternalLib = InternalLib(base = "gg.mmorealms:pokedex-rewards-module",   version = "1.0.54")
    val pokemonRiding:    InternalLib = InternalLib(base = "gg.mmorealms:pokemon-riding-module",    version = "1.0.38")
    val shop:             InternalLib = InternalLib(base = "gg.mmorealms:shop-module",              version = "1.0.73")
    val store:            InternalLib = InternalLib(base = "gg.mmorealms:store-module",             version = "1.0.57")
    val tebexIntegration: InternalLib = InternalLib(base = "gg.mmorealms:tebex-integration-module", version = "1.0.50")
    val tms:              InternalLib = InternalLib(base = "gg.mmorealms:tms-module",               version = "1.0.52")
    val tutorial:         InternalLib = InternalLib(base = "gg.mmorealms:tutorial-module",          version = "1.0.48")
    val warps:            InternalLib = InternalLib(base = "gg.mmorealms:warps-module",             version = "1.0.58")
    val wild:             InternalLib = InternalLib(base = "gg.mmorealms:wild-module",              version = "1.0.52")
    val modpackRewards:   InternalLib = InternalLib(base = "gg.mmorealms:modpack-rewards-module",   version = "1.0.50")
    val voting:           InternalLib = InternalLib(base = "gg.mmorealms:voting-module",            version = "1.0.53")
    val gambling:         InternalLib = InternalLib(base = "gg.mmorealms:gambling-module",          version = "1.0.68")
    val trade:            InternalLib = InternalLib(base = "gg.mmorealms:trade-module",             version = "1.0.80")
    val lobby:            InternalLib = InternalLib(base = "gg.mmorealms:lobby-module",             version = "0.0.0" ) // Deprecated
    val scoreboard:       InternalLib = InternalLib(base = "gg.mmorealms:scoreboard-module",        version = "1.0.11" )
    val metrics:          InternalLib = InternalLib(base = "gg.mmorealms:metrics-module",           version = "1.0.3" )
    val megaEvolution:    InternalLib = InternalLib(base = "gg.mmorealms:mega-evolution-module",    version = "1.0.12")
    //@formatter:on

    init {
        this::class.members
            .filterIsInstance<KProperty1<Any, *>>()
            .filter { it.returnType.classifier == InternalLib::class }
            .forEach { member ->
                val value = member.call(this) as? InternalLib
                if (value != null) {
                    ALL_LIBS.add(value)
                }
            }
    }

    @JvmStatic
    fun findDependency(input: String): InternalLib? {
        val inputWithDashes = input.replace("_", "-")

        for (lib in ALL_LIBS) {
            if (lib.id == input || lib.id == inputWithDashes) {
                return lib
            }
        }

        return null
    }

    class InternalLib {
        val id: String

        val base: String
        private val remoteVersion: String

        val common: String?
        val backend: Backend?
        val velocity: String?

        val hasCommon: Boolean
        val hasVelocity: Boolean
        val hasBackend: Boolean

        constructor(
            base: String,
            version: String,
            hasCommon: Boolean = true,
            hasVelocity: Boolean = true,
            hasBackend: Boolean = true
        ) {
            this.base = base
            this.id = base.split(":")[1].replace("-module", "")
            this.remoteVersion = version

            this.hasCommon = hasCommon
            this.hasVelocity = hasVelocity
            this.hasBackend = hasBackend

            this.common = if (hasCommon) "$base-common:$version" else null
            this.velocity = if (hasVelocity) "$base-velocity:$version" else null
            this.backend = if (hasBackend) Backend(
                common = "$base-backend-common:$version",
                fabric = "$base-backend-fabric:$version",
                neoforge = "$base-backend-neoforge:$version",
            ) else null
        }

        fun version(): String {
            val id = this.id.replace("-", "_").trim().lowercase()
            return Utils.findLocalDependencies().getOrDefault(id, remoteVersion)
        }

        class Backend {
            val common: String
            val fabric: String
            val neoforge: String

            constructor(
                common: String,
                fabric: String,
                neoforge: String
            ) {
                this.common = common
                this.fabric = fabric
                this.neoforge = neoforge
            }
        }
    }

}