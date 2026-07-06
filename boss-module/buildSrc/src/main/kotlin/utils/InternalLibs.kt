package utils

import kotlin.reflect.KProperty1

object InternalLibs {

    val LOCAL_DEPENDENCY_VERSION: String get() = Utils.getLatestLocalVersion()
    val ALL_LIBS: ArrayList<InternalLib> = ArrayList()

    //@formatter:off
    // NOP
    val nop:              InternalLib = InternalLib(base = "gg.mmorealms:nop-module",               version = "1.0.2")

    // Loader
    val loader:           InternalLib = InternalLib(base = "gg.mmorealms:loader",                   version = "0.0.0-master-252")

    // Client
    val client:           InternalLib = InternalLib(base = "gg.mmorealms:client",                   version = "0.0.0-master-66", hasCommon = false, hasVelocity = false)
    val testClient:       InternalLib = InternalLib(base = "gg.mmorealms:test-client",              version = "0.0.0-master-29", hasCommon = false, hasVelocity = false)

    // Modules
    val core:             InternalLib = InternalLib(base = "gg.mmorealms:core-module",              version = "0.0.0-master-166")
    val analytics:        InternalLib = InternalLib(base = "gg.mmorealms:analytics-module",         version = "0.0.0-master-63" )
    val economy:          InternalLib = InternalLib(base = "gg.mmorealms:economy-module",           version = "0.0.0-master-75" )
    val pokemon:          InternalLib = InternalLib(base = "gg.mmorealms:pokemon-module",           version = "0.0.0-master-104" )
    val auctionHouse:     InternalLib = InternalLib(base = "gg.mmorealms:auction-house-module",     version = "0.0.0-master-93" )
    val breeding:         InternalLib = InternalLib(base = "gg.mmorealms:breeding-module",          version = "0.0.0-master-78" )
    val userData:         InternalLib = InternalLib(base = "gg.mmorealms:user-data-module",         version = "0.0.0-master-72" )
    val chat:             InternalLib = InternalLib(base = "gg.mmorealms:chat-module",              version = "0.0.0-master-118" )
    val crates:           InternalLib = InternalLib(base = "gg.mmorealms:crates-module",            version = "0.0.0-master-82" )
    val discordChat:      InternalLib = InternalLib(base = "gg.mmorealms:discord-chat-module",      version = "0.0.0-master-59" )
    val discordLink:      InternalLib = InternalLib(base = "gg.mmorealms:discord-link-module",      version = "0.0.0-master-65" )
    val essentials:       InternalLib = InternalLib(base = "gg.mmorealms:essentials-module",        version = "0.0.0-master-136")
    val realms:           InternalLib = InternalLib(base = "gg.mmorealms:realms-module",            version = "0.0.0-master-127")
    val homes:            InternalLib = InternalLib(base = "gg.mmorealms:homes-module",             version = "0.0.0-master-66" )
    val hunts:            InternalLib = InternalLib(base = "gg.mmorealms:hunts-module",             version = "0.0.0-master-80" )
    val kits:             InternalLib = InternalLib(base = "gg.mmorealms:kits-module",              version = "0.0.0-master-72" )
    val legendaries:      InternalLib = InternalLib(base = "gg.mmorealms:legendaries-module",       version = "0.0.0-master-74" )
    val limbo:            InternalLib = InternalLib(base = "gg.mmorealms:limbo-module",             version = "0.0.0-master-59" ) // Deprecated
    val moderation:       InternalLib = InternalLib(base = "gg.mmorealms:moderation-module",        version = "0.0.0-master-57" )
    val plushies:         InternalLib = InternalLib(base = "gg.mmorealms:plushies-module",          version = "0.0.0-master-104" )
    val pokedexRewards:   InternalLib = InternalLib(base = "gg.mmorealms:pokedex-rewards-module",   version = "0.0.0-master-70" )
    val pokemonRiding:    InternalLib = InternalLib(base = "gg.mmorealms:pokemon-riding-module",    version = "1.0.38"          ) // Deprecated
    val shop:             InternalLib = InternalLib(base = "gg.mmorealms:shop-module",              version = "0.0.0-master-89" )
    val store:            InternalLib = InternalLib(base = "gg.mmorealms:store-module",             version = "0.0.0-master-71" )
    val tebexIntegration: InternalLib = InternalLib(base = "gg.mmorealms:tebex-integration-module", version = "0.0.0-master-65" )
    val tms:              InternalLib = InternalLib(base = "gg.mmorealms:tms-module",               version = "0.0.0-master-63" )
    val tutorial:         InternalLib = InternalLib(base = "gg.mmorealms:tutorial-module",          version = "0.0.0-master-59" )
    val warps:            InternalLib = InternalLib(base = "gg.mmorealms:warps-module",             version = "0.0.0-master-72" )
    val wild:             InternalLib = InternalLib(base = "gg.mmorealms:wild-module",              version = "0.0.0-master-80" )
    val modpackRewards:   InternalLib = InternalLib(base = "gg.mmorealms:modpack-rewards-module",   version = "0.0.0-master-60" )
    val voting:           InternalLib = InternalLib(base = "gg.mmorealms:voting-module",            version = "0.0.0-master-65" )
    val gambling:         InternalLib = InternalLib(base = "gg.mmorealms:gambling-module",          version = "0.0.0-master-79" )
    val trade:            InternalLib = InternalLib(base = "gg.mmorealms:trade-module",             version = "0.0.0-master-97" )
    val lobby:            InternalLib = InternalLib(base = "gg.mmorealms:lobby-module",             version = "0.0.0-master-53" )
    val scoreboard:       InternalLib = InternalLib(base = "gg.mmorealms:scoreboard-module",        version = "0.0.0-master-21" )
    val metrics:          InternalLib = InternalLib(base = "gg.mmorealms:metrics-module",           version = "0.0.0-master-18"  )
    val megaEvolution:    InternalLib = InternalLib(base = "gg.mmorealms:mega-evolution-module",    version = "0.0.0-master-36" )
    val catchCombo:       InternalLib = InternalLib(base = "gg.mmorealms:catch-combo-module",       version = "0.0.0-master-29" )
    val chatGames:        InternalLib = InternalLib(base = "gg.mmorealms:chat-games-module",        version = "0.0.0-master-23" )
    val coreProtect:      InternalLib = InternalLib(base = "gg.mmorealms:core-protect-module",      version = "0.0.0-master-17" )
    val gyms:             InternalLib = InternalLib(base = "gg.mmorealms:gyms-module",              version = "0.0.0-master-50" )
    val loginRewards:     InternalLib = InternalLib(base = "gg.mmorealms:login-rewards-module",     version = "0.0.0-master-15"  )
    val playtime:         InternalLib = InternalLib(base = "gg.mmorealms:playtime-module",          version = "0.0.0-master-11"  )
    val resourcePack:     InternalLib = InternalLib(base = "gg.mmorealms:resource-pack-module",     version = "0.0.0-master-44" )
    val example:          InternalLib = InternalLib(base = "gg.mmorealms:example-module",           version = "0.0.0-master-1")
    val pokeloot:         InternalLib = InternalLib(base = "gg.mmorealms:pokeloot-module",          version = "0.0.0-master-5")
    val wondertrade:      InternalLib = InternalLib(base = "gg.mmorealms:wondertrade-module",       version = "0.0.0-master-8")
    val tags:             InternalLib = InternalLib(base = "gg.mmorealms:tags-module",              version = "0.0.0-master-1")
    val antiDupe:         InternalLib = InternalLib(base = "gg.mmorealms:anti-dupe-module",         version = "0.0.0-master-1")
    val wildTrainers:     InternalLib = InternalLib(base = "gg.mmorealms:wild-trainers-module",     version = "0.0.0-master-1")
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

        val hasCommon: Boolean
        val hasVelocity: Boolean
        val hasBackend: Boolean

        val common: String? get() = if (hasCommon) "$base-common:${version()}" else null
        val velocity: String? get() = if (hasVelocity) "$base-velocity:${version()}" else null
        val backend: Backend? get() = if (hasBackend) Backend(
            common = "$base-backend-common:${version()}",
            fabric = "$base-backend-fabric:${version()}",
            neoforge = "$base-backend-neoforge:${version()}",
        ) else null

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
        }

        fun version(): String {
            val id = this.id.replace("-", "_").trim().lowercase()

            Utils.findLocalDependencies()[id]?.let { return it }
            Utils.readInternalLibsVersions()[id]?.let { return it }

            return remoteVersion
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