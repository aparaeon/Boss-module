package gg.mmorealms.module.boss.backend.fabric;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raduvoinea.commandmanager.backend.fabric.FabricMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.boss.backend.common.BossBackendModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager;
import gg.mmorealms.module.boss.backend.fabric.manager.BossSpawner;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.ActiveBoss;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import net.minecraft.nbt.CompoundTag;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Getter
public class BossFabricModule extends BossBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static BossFabricModule instance;
	private @Inject FabricMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;
	private @Inject MinecraftServer server;

	private BossConfig config;
	private BossManager bossManager;
	private BossSpawner bossSpawner;

	public BossFabricModule() {
		BossFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}

	@Override
	public void onInit() throws ModuleException {
		if (getServerType() != ServerType.WILD) {
			Logger.info("Boss module skipping init on non-WILD server (type=" + getServerType() + ").");
			return;
		}
		migrateLegacyConfig();
		this.config = fileManager.load(BossConfig.class);
		applyCurrentConfigDefaults(config);
		validateConfig(config);

		this.bossManager = new BossManager(config, fileManager);
		this.bossSpawner = new BossSpawner(config, bossManager);

		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (!(entity instanceof PokemonEntity pe)) return;
			runOnMain(() -> handleEntityLoad(pe));
		});
		net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents.START_TRACKING
				.register((entity, player) -> {
					if (!(entity instanceof PokemonEntity pe)) return;
					CompoundTag tag = pe.getPokemon().getPersistentData();
					if (!tag.getBoolean(NbtKeys.BOSS)) return;
					BossTier tier;
					try {
						tier = BossTier.valueOf(tag.getString(NbtKeys.TIER));
					} catch (IllegalArgumentException e) {
						return;
					}
					TierConfig tc = config.tiers.get(tier);
					MinecraftServer s = this.server;
					if (tc == null || s == null) return;
					runOnMain(() -> {
						if (!pe.isRemoved()) {
							bossManager.applyBossTeam(s, pe, tc);
						}
					});
				});

		net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register(
				(handler, sender, s) -> bossManager.bootstrapFillAllTiers()
		);
		com.cobblemon.mod.common.api.events.CobblemonEvents.BATTLE_FLED.subscribe(
				com.cobblemon.mod.common.api.Priority.NORMAL,
				event -> {
			runOnMain(() -> {
				if (bossManager != null) {
					bossManager.retryPendingDespawns();
				}
			});
					return kotlin.Unit.INSTANCE;
				}
		);
		com.cobblemon.mod.common.api.events.CobblemonEvents.BATTLE_STARTED_PRE.subscribe(
				com.cobblemon.mod.common.api.Priority.NORMAL,
				event -> {
					runOnMain(() -> {
						if (bossManager != null) {
							bossManager.handleBattleStarted(event.getBattle());
						}
					});
					return kotlin.Unit.INSTANCE;
				}
		);
		bossManager.bootstrapFillAllTiers();
		com.raduvoinea.utils.lambda.ScheduleUtils.runTaskTimer(
				() -> bossManager.refillSweep(),
				com.raduvoinea.utils.generic.Time.minutes(5)
		);
		com.raduvoinea.utils.lambda.ScheduleUtils.runTaskTimer(
				() -> bossManager.glowSweep(),
				com.raduvoinea.utils.generic.Time.seconds(15)
		);
	}
	private void applyCurrentConfigDefaults(@NotNull BossConfig cfg) {
		cfg.lang = new BossConfig.Lang();
		if (cfg.tiers == null) {
			cfg.tiers = new java.util.EnumMap<>(BossTier.class);
		}
		for (BossTier tier : BossTier.values()) {
			TierConfig tc = cfg.tiers.get(tier);
			if (tc == null) {
				tc = TierConfig.defaultsFor(tier);
				cfg.tiers.put(tier, tc);
			}
			TierConfig defaults = TierConfig.defaultsFor(tier);
			tc.displayName = defaults.displayName;
			tc.glowColor = defaults.glowColor;
			tc.announceOnSpawn = defaults.announceOnSpawn;
			tc.announceOnDefeat = defaults.announceOnDefeat;
			if (tc.defeatDialogue == null || tc.defeatDialogue.isEmpty()) {
				tc.defeatDialogue = defaults.defeatDialogue;
			}
			if (tc.battleCryTaunts == null || tc.battleCryTaunts.isEmpty()) {
				tc.battleCryTaunts = defaults.battleCryTaunts;
			}
			if (tc.bossAbility == null) {
				tc.bossAbility = defaults.bossAbility;
			}
			if (tc.minActive <= 0 && defaults.minActive > 0) {
				Logger.warn("Boss tier " + tier + " had minActive=" + tc.minActive
						+ " in boss_config.json; raising to " + defaults.minActive + ".");
				tc.minActive = defaults.minActive;
			}
			if (tc.maxActive < tc.minActive) {
				Logger.warn("Boss tier " + tier + " had maxActive=" + tc.maxActive
						+ " below minActive=" + tc.minActive + "; raising maxActive to " + tc.minActive + ".");
				tc.maxActive = tc.minActive;
			}
		}
		fileManager.save(cfg);
	}

	private void migrateLegacyConfig() {
		String raw = fileManager.readFile("", "boss_config.json");
		if (raw.isEmpty()) {
			return;
		}
		boolean legacy;
		try {
			JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
			JsonObject tiers = root.getAsJsonObject("tiers");
			if (tiers == null) {
				return;
			}
			legacy = tiers.entrySet().stream()
					.map(Map.Entry::getValue)
					.map(JsonElement::getAsJsonObject)
					.map(obj -> obj.get("announceOnSpawn"))
					.anyMatch(el -> el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean());
		} catch (RuntimeException t) {
			Logger.warn("Could not inspect boss_config.json for legacy schema: " + t.getMessage());
			return;
		}
		if (!legacy) {
			return;
		}
		Logger.warn("Legacy boss_config.json detected (boolean announceOnSpawn) — regenerating defaults.");
		fileManager.writeFile("", "boss_config.json", "");
	}

	private void validateConfig(BossConfig cfg) throws ModuleException {
		if (cfg.spawnRangeFromPlayer == null || cfg.spawnRangeFromPlayer.getMin() > cfg.spawnRangeFromPlayer.getMax()) {
			throw new ModuleException(this, "BossConfig.spawnRangeFromPlayer min must be <= max");
		}
		if (cfg.randomSpawnRangeY == null || cfg.randomSpawnRangeY.getMin() > cfg.randomSpawnRangeY.getMax()) {
			throw new ModuleException(this, "BossConfig.randomSpawnRangeY min must be <= max");
		}
		if (cfg.tiers == null || cfg.tiers.isEmpty()) {
			throw new ModuleException(this,"BossConfig.tiers is empty");
		}
		for (BossTier tier : BossTier.values()) {
			TierConfig tc = cfg.tiers.get(tier);
			if (tc == null) {
				throw new ModuleException(this,"BossConfig.tiers missing entry for " + tier);
			}
			if (tc.levelRange == null || tc.levelRange.getMin() > tc.levelRange.getMax()) {
				throw new ModuleException(this,"Tier " + tier + " has invalid levelRange");
			}
			if (tc.scale <= 0) {
				throw new ModuleException(this,"Tier " + tier + " scale must be > 0");
			}
			if (tc.maxActive < 0) {
				throw new ModuleException(this,"Tier " + tier + " maxActive must be >= 0");
			}
			if (tc.minActive < 0) {
				throw new ModuleException(this,"Tier " + tier + " minActive must be >= 0");
			}
			if (tc.minActive > tc.maxActive) {
				throw new ModuleException(this,"Tier " + tier + " minActive (" + tc.minActive
						+ ") cannot exceed maxActive (" + tc.maxActive + ")");
			}
			if (tc.bstRange != null && tc.bstRange.getMin() > tc.bstRange.getMax()) {
				throw new ModuleException(this,"Tier " + tier + " bstRange min > max");
			}
			boolean hasSpecies = !tc.pokemonClasses.isEmpty()
					|| !tc.extraSpecies.isEmpty()
					|| (tier == BossTier.MEGA && tc.includeAllMegaCapable);
			if (!hasSpecies) {
				throw new ModuleException(this,"Tier " + tier + " has no species source");
			}
			if (tc.rewardRolls > 0 && tc.rewards.isEmpty()) {
				throw new ModuleException(this,"Tier " + tier + " has rewardRolls > 0 but rewards is empty");
			}
			for (int i = 0; i < tc.rewards.size(); i++) {
				TierConfig.BossReward r = tc.rewards.get(i);
				if (r == null) {
					throw new ModuleException(this, "Tier " + tier + " reward[" + i + "] is null");
				}
				if (r.getWeight() <= 0) {
					throw new ModuleException(this, "Tier " + tier + " reward[" + i + "] weight must be > 0 (got " + r.getWeight() + ")");
				}
				if (r.getQuantity() == null
						|| r.getQuantity().getMin() < 0
						|| r.getQuantity().getMin() > r.getQuantity().getMax()) {
					throw new ModuleException(this, "Tier " + tier + " reward[" + i + "] has invalid quantity range");
				}
				if (r.getRewardCommands() == null) {
					throw new ModuleException(this, "Tier " + tier + " reward[" + i + "] rewardCommands is null");
				}
			}
			tc.extraSpecies = normalizeSpeciesList(tc.extraSpecies);
			tc.excludedSpecies = normalizeSpeciesList(tc.excludedSpecies);
			for (String s : tc.extraSpecies) {
				if (PokemonSpecies.INSTANCE.getByName(s) == null) {
					throw new ModuleException(this, "Tier " + tier + " extraSpecies contains unknown species: " + s);
				}
			}
			for (String s : tc.excludedSpecies) {
				if (PokemonSpecies.INSTANCE.getByName(s) == null) {
					throw new ModuleException(this, "Tier " + tier + " excludedSpecies contains unknown species: " + s);
				}
			}
			if (tier == BossTier.MEGA) {
				for (String s : tc.extraSpecies) {
					boolean megaCapable = MegaEvolution.stream().anyMatch(me ->
							me.getSpeciesName() != null
									&& me.getSpeciesName().equalsIgnoreCase(s)
									&& me.getMegaAspect() != null && !me.getMegaAspect().isEmpty());
					if (!megaCapable) {
						throw new ModuleException(this, "Tier MEGA extraSpecies contains non-mega-capable species: " + s);
					}
				}
			}
			tc.glowChatFmt = parseGlowColor(tc.glowColor, tier);
			if (tc.heldItem != null && !tc.heldItem.isBlank()
					&& BuiltInRegistries.ITEM.get(ResourceLocation.parse(tc.heldItem)) == net.minecraft.world.item.Items.AIR) {
				throw new ModuleException(this, "Tier " + tier + " heldItem is not a valid item: " + tc.heldItem);
			}
			resolveEffectParticles(tc.spawnEffect, "spawnEffect", tier);
			resolveEffectParticles(tc.ambientEffect, "ambientEffect", tier);
			if (tc.ambientEffect != null && tc.ambientEffect.enabled
					&& (tc.ambientEffect.intervalSeconds == null || tc.ambientEffect.intervalSeconds <= 0)) {
				throw new ModuleException(this,"Tier " + tier + " ambientEffect requires intervalSeconds > 0");
			}
		}
	}

	private static List<String> normalizeSpeciesList(List<String> speciesList) {
		return speciesList == null ? List.of() : speciesList.stream().map(name -> name.trim().toLowerCase(Locale.ROOT)).toList();
	}

	private ChatFormatting parseGlowColor(String value, BossTier tier) throws ModuleException {
		if (value == null) return ChatFormatting.WHITE;
		String norm = value.trim().toUpperCase().replace(' ', '_').replace('-', '_');
		try {
			return ChatFormatting.valueOf(norm);
		} catch (IllegalArgumentException e) {
			throw new ModuleException(this,"Tier " + tier + " invalid glowColor: " + value
					+ " (expected MC names: GRAY, GREEN, BLUE, LIGHT_PURPLE, GOLD, AQUA, RED, ...)");
		}
	}

	private void resolveEffectParticles(TierConfig.EffectConfig effect, String label, BossTier tier) throws ModuleException {
		if (effect == null || !effect.enabled) return;
		if (effect.particles == null || effect.particles.isEmpty()) {
			throw new ModuleException(this,"Tier " + tier + " " + label + " enabled but particles list is empty");
		}
		List<SimpleParticleType> resolved = new ArrayList<>();
		for (String id : effect.particles) {
			ParticleType<?> t = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(id));
			if (t == null) {
				throw new ModuleException(this,"Tier " + tier + " " + label + " unknown particle: " + id);
			}
			if (!(t instanceof SimpleParticleType simple)) {
				throw new ModuleException(this,"Tier " + tier + " " + label + " particle " + id
						+ " requires data params (e.g. dust color); not supported in MVP. "
						+ "Use simple particles like minecraft:enchant, minecraft:flame, minecraft:end_rod, "
						+ "minecraft:explosion, minecraft:soul_fire_flame, minecraft:dragon_breath.");
			}
			resolved.add(simple);
		}
		effect.particleOptions = resolved;
	}

	public void handleEntityLoad(PokemonEntity entity) {
		CompoundTag tag = entity.getPokemon().getPersistentData();
		if (!tag.getBoolean(NbtKeys.BOSS)) {
			return;
		}
		if (bossManager.consumePendingDiscard(entity.getUUID())) {
			tag.putBoolean(NbtKeys.BOSS, false);
			entity.discard();
			Logger.info("Discarded boss entity " + entity.getUUID() + " on chunk reload (pending hard-despawn).");
			return;
		}

		int schemaVersion = tag.contains(NbtKeys.SCHEMA)
				? tag.getInt(NbtKeys.SCHEMA)
				: 1;

		if (schemaVersion > NbtKeys.SCHEMA_VERSION) {
			Logger.error("Boss NBT schema v" + schemaVersion + " is newer than supported v"
					+ NbtKeys.SCHEMA_VERSION + "; discarding entity " + entity.getUUID());
			entity.discard();
			return;
		}

		String tierName = tag.getString(NbtKeys.TIER);
		BossTier tier;
		try {
			tier = BossTier.valueOf(tierName);
		} catch (IllegalArgumentException e) {
			Logger.error("Boss NBT has invalid tier: " + tierName + "; discarding entity " + entity.getUUID());
			entity.discard();
			return;
		}

		TierConfig tc = config.tiers.get(tier);
		if (tc == null) {
			Logger.error("Boss NBT tier " + tier + " has no config entry; discarding entity " + entity.getUUID());
			entity.discard();
			return;
		}
		boolean systemSpawned = !tag.contains(NbtKeys.SYSTEM_SPAWNED)
				|| tag.getBoolean(NbtKeys.SYSTEM_SPAWNED);
		net.minecraft.core.BlockPos spawnPos = tag.contains(NbtKeys.SPAWN_X)
				? new net.minecraft.core.BlockPos(
						tag.getInt(NbtKeys.SPAWN_X),
						tag.getInt(NbtKeys.SPAWN_Y),
						tag.getInt(NbtKeys.SPAWN_Z))
				: entity.blockPosition();
		net.minecraft.resources.ResourceLocation spawnDim = tag.contains(NbtKeys.SPAWN_DIMENSION)
				? net.minecraft.resources.ResourceLocation.parse(tag.getString(NbtKeys.SPAWN_DIMENSION))
				: entity.level().dimension().location();

		ActiveBoss boss = new ActiveBoss(
				entity.getPokemon().getUuid(),
				entity.getUUID(),
				tier,
				tag.getString(NbtKeys.SPECIES),
				tag.getInt(NbtKeys.LEVEL),
				tag.getLong(NbtKeys.SPAWNED_AT),
				systemSpawned,
				spawnPos,
				spawnDim
		);
		boolean newRegistration = bossManager.registerActive(boss);
		if (newRegistration && systemSpawned) {
			bossManager.forceReserveTier(tier);
		}
		if (newRegistration) {
			Logger.info("Boss reloaded: " + tier + " " + tag.getString(NbtKeys.SPECIES)
					+ " lv." + tag.getInt(NbtKeys.LEVEL)
					+ " (" + entity.getUUID().toString().substring(0, 8) + ")"
					+ (systemSpawned ? " [system]" : " [admin]") + " on chunk load");
		}
		entity.setPersistenceRequired();
		entity.setInvulnerable(true);
		if (bossSpawner != null) {
			bossSpawner.applyBossName(entity, tier, tag.getString(NbtKeys.SPECIES), tag.getInt(NbtKeys.LEVEL));
		}
		MinecraftServer s = this.server;
		if (s != null) {
			com.raduvoinea.utils.lambda.ScheduleUtils.runTaskLater(
					() -> runOnMain(() -> {
						if (!entity.isRemoved()) {
							bossManager.applyBossTeam(s, entity, tc);
						}
					}),
					com.raduvoinea.utils.generic.Time.milliseconds(500)
			);
		}
		bossManager.scheduleDespawn(boss);
		bossManager.scheduleAmbientParticles(boss);
	}
	public @Nullable PokemonEntity findEntity(UUID entityUUID) {
		if (server == null) return null;
		for (var level : server.getAllLevels()) {
			var e = level.getEntity(entityUUID);
			if (e instanceof PokemonEntity pe) return pe;
		}
		return null;
	}
	public boolean runOnMain(Runnable r) {
		MinecraftServer s = this.server;
		if (s == null) return false;
		s.execute(r);
		return true;
	}
	public void sendLang(@org.jetbrains.annotations.NotNull net.minecraft.commands.CommandSource sender,
	                     @org.jetbrains.annotations.NotNull com.raduvoinea.utils.message_builder.MessageBuilder mb) {
		sender.sendSystemMessage(miniMessageManager.parse(mb.parse()));
	}
}
