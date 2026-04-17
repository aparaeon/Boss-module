package gg.mmorealms.module.essentials.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import gg.mmorealms.module.essentials.common.EssentialsCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.border.WorldBorder;

@Getter
public class EssentialsBackendModule extends EssentialsCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static EssentialsBackendModule instance;

	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;
	private @Inject BackendMiniMessageManager miniMessageManager;

	private EssentialsConfig config; // exported

	public EssentialsBackendModule() {
		EssentialsBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(EssentialsConfig.class));
	}

	@Override
	public void onEnable() {
		setGameRules();
		setWorldBorder();
	}

	private void setWorldBorder() {
		if (this.getServerType().equals(ServerType.REALMS)) {
			return;
		}
		this.server.getAllLevels().forEach(
				(level) -> {
					WorldBorder worldBorder = level.getWorldBorder();
					worldBorder.setCenter(0, 0);
					worldBorder.setSize(config.borderSize);
					worldBorder.setDamagePerBlock(0.5f);
				}
		);
	}

	private void setGameRules() {
		// TODO Config
		this.server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(true, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_UNIVERSAL_ANGER).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, this.server);
		this.server.getGameRules().getRule(GameRules.RULE_DO_WARDEN_SPAWNING).set(false, this.server);

		if (this.getServerType().equals(ServerType.SPAWN) || this.getServerType().equals(ServerType.GYMS)) {
			this.server.getGameRules().getRule(GameRules.RULE_GLOBAL_SOUND_EVENTS).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH).set(65536, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DO_VINES_SPREAD).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_LAVA_SOURCE_CONVERSION).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_COMMANDBLOCKOUTPUT).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_FORGIVE_DEAD_PLAYERS).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY).set(1, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_MAX_ENTITY_CRAMMING).set(24, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE).set(100, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT).set(1, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_FALL_DAMAGE).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DOENTITYDROPS).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(0, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY).set(80, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_FREEZE_DAMAGE).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DO_WARDEN_SPAWNING).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_FIRE_DAMAGE).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_REDUCEDDEBUGINFO).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_PROJECTILESCANBREAKBLOCKS).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DROWNING_DAMAGE).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_SPAWN_CHUNK_RADIUS).set(2, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DISABLE_RAIDS).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DOINSOMNIA).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT).set(32768, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_LOGADMINCOMMANDS).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(true, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, this.server);
			this.server.getGameRules().getRule(GameRules.RULE_MAX_COMMAND_FORK_COUNT).set(65536, this.server);
		}
	}
}
