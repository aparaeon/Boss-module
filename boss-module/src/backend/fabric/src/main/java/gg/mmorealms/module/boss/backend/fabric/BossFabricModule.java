package gg.mmorealms.module.boss.backend.fabric;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.raduvoinea.commandmanager.backend.fabric.FabricMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.boss.backend.common.BossBackendModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.BossReward;
import gg.mmorealms.module.boss.backend.fabric.config.EffectConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import gg.mmorealms.module.boss.backend.fabric.manager.ActiveBoss;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager;
import gg.mmorealms.module.boss.backend.fabric.manager.BossNbtKeys;
import gg.mmorealms.module.boss.backend.fabric.manager.BossSpawner;
import gg.mmorealms.module.boss.common.BossTier;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import net.minecraft.nbt.CompoundTag;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class BossFabricModule extends BossBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static BossFabricModule instance;

	private @Inject FabricMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;

	private BossConfig config;
	private BossManager bossManager;
	private BossSpawner bossSpawner;
	private @Nullable MinecraftServer server;

	public BossFabricModule() {
		BossFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}

	@Override
	public void onInit() throws ModuleException {
		// WILD server gate: skip backend init entirely on non-WILD shards.
		// BossListener has @OnlyOn(WILD) for defence-in-depth, but we also avoid loading
		// config + spinning up managers + subscribing Cobblemon events on Spawn/Realms/Gym backends.
		if (getServerType() != ServerType.WILD) {
			Logger.info("Boss module skipping init on non-WILD server (type=" + getServerType() + ").");
			return;
		}

		this.config = fileManager.load(BossConfig.class);
		validateConfig(config);

		this.bossManager = new BossManager(config);
		this.bossSpawner = new BossSpawner(config, bossManager);

		// Capture server reference for off-thread handlers.
		ServerLifecycleEvents.SERVER_STARTED.register(s -> this.server = s);
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> this.server = null);

		// Defer 250ms so BattleWonEvent wins the race for battle-faints; env-faints land on the deferred check.
		CobblemonEvents.POKEMON_FAINTED.subscribe(
				com.cobblemon.mod.common.api.Priority.NORMAL,
				ev -> {
					UUID uuid = ev.getPokemon().getUuid();
					com.raduvoinea.utils.lambda.ScheduleUtils.runTaskLater(
							() -> runOnMain(() -> {
								ActiveBoss boss = bossManager.get(uuid);
								if (boss == null) return; // BattleWon already claimed it
								PokemonEntity pe = findEntity(boss.entityUUID());
								if (pe != null && pe.isBusy()) {
									return; // still in battle
								}
								bossManager.handleEnvironmentalFaint(uuid);
							}),
							com.raduvoinea.utils.generic.Time.milliseconds(250)
					);
					return kotlin.Unit.INSTANCE;
				}
		);

		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (!(entity instanceof PokemonEntity pe)) return;
			runOnMain(() -> handleEntityLoad(pe));
		});

		// BossListener auto-registers via reflection scan after this method returns.
	}

	/* ---------- Validation ---------- */

	private void validateConfig(BossConfig cfg) throws ModuleException {
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
			// Validate each reward entry — fail-loud at boot rather than at first dispatch.
			for (int i = 0; i < tc.rewards.size(); i++) {
				BossReward r = tc.rewards.get(i);
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
			// Species in extraSpecies / excludedSpecies must resolve at boot, not at first spawn.
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
			tc.glowChatFmt = parseGlowColor(tc.glowColor, tier);
			resolveEffectParticles(tc.spawnEffect, "spawnEffect", tier);
			resolveEffectParticles(tc.ambientEffect, "ambientEffect", tier);
			if (tc.ambientEffect != null && tc.ambientEffect.enabled
					&& (tc.ambientEffect.intervalSeconds == null || tc.ambientEffect.intervalSeconds <= 0)) {
				throw new ModuleException(this,"Tier " + tier + " ambientEffect requires intervalSeconds > 0");
			}
		}
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

	private void resolveEffectParticles(EffectConfig effect, String label, BossTier tier) throws ModuleException {
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

	/* ---------- Entity load (called via onInit-registered ENTITY_LOAD) ---------- */

	public void handleEntityLoad(PokemonEntity entity) {
		CompoundTag tag = entity.getPokemon().getPersistentData();
		if (!tag.getBoolean(BossNbtKeys.BOSS)) {
			return; // not a boss
		}

		int schemaVersion = tag.contains(BossNbtKeys.SCHEMA)
				? tag.getInt(BossNbtKeys.SCHEMA)
				: 1; // missing = treat as v1 (backward compat)

		if (schemaVersion > BossNbtKeys.SCHEMA_VERSION) {
			Logger.error("Boss NBT schema v" + schemaVersion + " is newer than supported v"
					+ BossNbtKeys.SCHEMA_VERSION + "; discarding entity " + entity.getUUID());
			entity.discard();
			return;
		}

		String tierName = tag.getString(BossNbtKeys.TIER);
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

		ActiveBoss boss = new ActiveBoss(
				entity.getPokemon().getUuid(),
				entity.getUUID(),
				tier,
				tag.getString(BossNbtKeys.SPECIES),
				tag.getInt(BossNbtKeys.LEVEL),
				tag.getLong(BossNbtKeys.SPAWNED_AT)
		);

		// putIfAbsent → counter increment only on new insert (idempotent across chunk reload).
		boolean newRegistration = bossManager.registerActive(boss);
		if (newRegistration) {
			bossManager.forceReserveTier(tier);
		}

		// Team owned by BossManager. Scheduled tasks are idempotent — they cancel-and-replace.
		MinecraftServer s = this.server;
		if (s != null) {
			bossManager.applyBossTeam(s, entity, tc);
		}
		bossManager.scheduleDespawn(boss);
		bossManager.scheduleAmbientParticles(boss);
	}

	/* ---------- Lookup helpers ---------- */

	public @Nullable PokemonEntity findEntity(UUID entityUUID) {
		if (server == null) return null;
		for (var level : server.getAllLevels()) {
			var e = level.getEntity(entityUUID);
			if (e instanceof PokemonEntity pe) return pe;
		}
		return null;
	}

	/**
	 * Fire-and-forget hop to main thread. Returns false if server is unavailable
	 * (shutdown / not started yet) — caller can short-circuit if needed.
	 * Used everywhere off-thread code touches entity / world / scoreboard state.
	 */
	public boolean runOnMain(Runnable r) {
		MinecraftServer s = this.server;
		if (s == null) return false;
		s.execute(r);
		return true;
	}

	/** Render a lang MessageBuilder via MiniMessage and dispatch to the command sender. */
	public void sendLang(@org.jetbrains.annotations.NotNull net.minecraft.commands.CommandSource sender,
	                     @org.jetbrains.annotations.NotNull com.raduvoinea.utils.message_builder.MessageBuilder mb) {
		sender.sendSystemMessage(miniMessageManager.parse(mb.parse()));
	}
}
