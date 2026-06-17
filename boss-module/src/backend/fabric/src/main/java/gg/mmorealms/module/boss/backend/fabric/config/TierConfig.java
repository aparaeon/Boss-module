package gg.mmorealms.module.boss.backend.fabric.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.IWeighted;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class TierConfig {
	public String displayName;
	public String glowColor;
	public String nameplateFormat;

	public Range levelRange;
	public float scale = 1.0F;
	public boolean maxIvs = true;
	public boolean maxEvs = true;

	/** Auto-despawn delay. {@code 0m} = never (boss persists until defeated or admin-removed). */
	public Time despawnAfter = Time.minutes(30);
	/** Hard cap on system-spawned alive bosses (system-only — admin spawns stack on top). */
	public int maxActive = 1;
	/** Refill floor for system spawns. {@code 0} = no auto-refill (gone until admin/restart). System-only. */
	public int minActive = 1;

	public List<PokemonClass> pokemonClasses = List.of();
	public List<String> extraSpecies = List.of();
	public List<String> excludedSpecies = List.of();
	public boolean includeAllMegaCapable = false;
	/** Filter pool by Base Stat Total. Null = no filter. Used to slice the broad NORMAL class into tier-appropriate strength bands. */
	public @Nullable Range bstRange;

	public List<String> allowedDimensions;
	public List<String> biomes = List.of();

	public EffectConfig spawnEffect;
	public EffectConfig ambientEffect;

	public List<BossReward> rewards = List.of();
	public int rewardRolls = 0;

	public AnnounceLevel announceOnSpawn = AnnounceLevel.OFF;
	public AnnounceLevel announceOnDefeat = AnnounceLevel.OFF;

	public transient ChatFormatting glowChatFmt;

	public enum AnnounceLevel {
		OFF,
		WORLD_CHAT,
		GLOBAL_CHAT
	}

	public static class EffectConfig {
		public boolean enabled = false;
		public List<String> particles = List.of();
		public Integer intervalSeconds;
		public int count = 5;
		public double offset = 0.5;

		public transient List<SimpleParticleType> particleOptions;

		public static EffectConfig burst(String particleId, int count, double offset) {
			EffectConfig e = new EffectConfig();
			e.enabled = true;
			e.particles = List.of(particleId);
			e.count = count;
			e.offset = offset;
			return e;
		}

		public static EffectConfig recurring(String particleId, int intervalSeconds, int count, double offset) {
			EffectConfig e = new EffectConfig();
			e.enabled = true;
			e.particles = List.of(particleId);
			e.intervalSeconds = intervalSeconds;
			e.count = count;
			e.offset = offset;
			return e;
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class BossReward implements IWeighted {
		private double weight;
		private Range quantity;
		private MessageBuilderList rewardCommands;
		private @Nullable String displayName;

		@Override
		public double getWeight() {
			return weight;
		}
	}

	public static TierConfig defaultsFor(BossTier tier) {
		TierConfig tc = new TierConfig();
		switch (tier) {
			case COMMON -> {
				tc.displayName = "<b><gradient:#C9D6DF:#F8F9FA>Common</gradient></b>";
				tc.glowColor = "white";
				tc.levelRange = new Range(10, 30);
				tc.scale = 1.2F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.minActive = 1;
				tc.despawnAfter = Time.minutes(2);
				tc.announceOnSpawn = AnnounceLevel.OFF;
				tc.announceOnDefeat = AnnounceLevel.OFF;
				tc.pokemonClasses = List.of(PokemonClass.NORMAL);
				tc.bstRange = new Range(0, 349);
				tc.ambientEffect = EffectConfig.recurring("minecraft:enchant", 8, 2, 0.3);
				tc.rewardRolls = 1;
				tc.rewards = List.of(
						reward(30, 2, 4, "give {user} cobblemon:poke_ball {quantity}"),
						reward(18, 1, 2, "give {user} cobblemon:rare_candy {quantity}"),
						reward(12, 1, 2, "give {user} cobblemon:great_ball {quantity}"),
						reward(10, 1, 1, "give {user} cobblemon:exp_candy_s {quantity}"),
						reward(8, 1, 2, "give {user} cobblemon:potion {quantity}"),
						reward(7, 2, 4, "give {user} cobblemon:premier_ball {quantity}"),
						reward(5, 1, 1, "give {user} cobblemon:link_cable {quantity}"),
						reward(4, 1, 2, "give {user} cobblemon:oran_berry {quantity}"),
						reward(3, 1, 1, "give {user} cobblemon:fire_stone {quantity}"),
						reward(2, 1, 1, "give {user} cobblemon:water_stone {quantity}"),
						reward(1, 1, 1, "give {user} cobblemon:adamant_mint {quantity}")
				);
			}
			case UNCOMMON -> {
				tc.displayName = "<b><gradient:#11998E:#38EF7D>Uncommon</gradient></b>";
				tc.glowColor = "green";
				tc.levelRange = new Range(20, 40);
				tc.scale = 1.3F;
					tc.maxIvs = true;
					tc.maxActive = 1;
					tc.minActive = 1;
					tc.despawnAfter = Time.minutes(2);
					tc.announceOnSpawn = AnnounceLevel.OFF;
					tc.announceOnDefeat = AnnounceLevel.OFF;
					tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(350, 449);
					tc.ambientEffect = EffectConfig.recurring("minecraft:happy_villager", 7, 3, 0.3);
					tc.rewardRolls = 1;
					tc.rewards = List.of(
							reward(25, 2, 4, "give {user} cobblemon:great_ball {quantity}"),
							reward(17, 1, 2, "give {user} cobblemon:rare_candy {quantity}"),
							reward(13, 1, 2, "give {user} cobblemon:ultra_ball {quantity}"),
							reward(11, 1, 2, "give {user} cobblemon:exp_candy_s {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:exp_candy_m {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:super_potion {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:thunder_stone {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:leaf_stone {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:protector {quantity}"),
							reward(2, 1, 2, "give {user} cobblemon:sitrus_berry {quantity}"),
							reward(2, 1, 1, "give {user} cobblemon:ability_capsule {quantity}")
					);
				}
				case RARE -> {
				tc.displayName = "<b><gradient:#36D1DC:#5B86E5>Rare</gradient></b>";
				tc.glowColor = "blue";
				tc.levelRange = new Range(35, 55);
				tc.scale = 1.5F;
				tc.maxIvs = true;
				tc.maxActive = 1;
					tc.minActive = 1;
					tc.despawnAfter = Time.minutes(2);
					tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(450, 529);
					tc.announceOnSpawn = AnnounceLevel.OFF;
					tc.announceOnDefeat = AnnounceLevel.OFF;
					tc.ambientEffect = EffectConfig.recurring("minecraft:enchant", 5, 4, 0.4);
					tc.rewardRolls = 2;
					tc.rewards = List.of(
							reward(22, 3, 6, "give {user} cobblemon:ultra_ball {quantity}"),
							reward(18, 2, 4, "give {user} cobblemon:rare_candy {quantity}"),
							reward(13, 1, 2, "give {user} cobblemon:exp_candy_m {quantity}"),
							reward(11, 1, 2, "give {user} cobblemon:exp_candy_l {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:hyper_potion {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:adamant_mint {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:jolly_mint {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:moon_stone {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:expert_belt {quantity}"),
							reward(3, 1, 2, "give {user} cobblemon:lum_berry {quantity}"),
							reward(2, 1, 1, "give {user} cobblemon:ability_capsule {quantity}")
					);
				}
				case ULTRA_RARE -> {
				tc.displayName = "<b><gradient:#DA22FF:#9733EE>Ultra Rare</gradient></b>";
				tc.glowColor = "light_purple";
				tc.levelRange = new Range(60, 90);
				tc.scale = 1.8F;
				tc.maxIvs = true;
				tc.maxActive = 1;
					tc.minActive = 1;
					tc.despawnAfter = Time.minutes(3);
				tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(530, 9999);
					tc.announceOnSpawn = AnnounceLevel.WORLD_CHAT;
					tc.announceOnDefeat = AnnounceLevel.OFF;
					tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 15, 0.5);
					tc.rewardRolls = 2;
					tc.rewards = List.of(
							reward(20, 2, 4, "give {user} cobblemon:rare_candy {quantity}"),
							reward(18, 1, 3, "give {user} cobblemon:exp_candy_l {quantity}"),
							reward(13, 1, 2, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:ability_capsule {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:focus_sash {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:choice_scarf {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:assault_vest {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:ability_patch {quantity}"),
							reward(3, 1, 1, "give {user} cobblemon:shiny_stone {quantity}"),
							reward(3, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case LEGENDARY -> {
				tc.displayName = "<b><gradient:#FFB300:#FFF59D:#FFB300>Legendary</gradient></b>";
				tc.glowColor = "gold";
				tc.levelRange = new Range(90, 120);
				tc.scale = 2.2F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.minActive = 1;
				tc.despawnAfter = Time.minutes(3);
				tc.pokemonClasses = List.of(PokemonClass.LEGENDARY);
				tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 20, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:end_rod", 5, 4, 0.4);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							reward(15, 2, 4, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(13, 3, 6, "give {user} cobblemon:rare_candy {quantity}"),
							reward(11, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:life_orb {quantity}"),
							namedReward(9, 200, 500, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(8, 1, 3, "give {user} cobblemon:dragon_gem {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:sun_stone {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:dusk_stone {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:helix_fossil {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:dome_fossil {quantity}"),
							reward(6, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:weakness_policy {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:max_potion {quantity}"),
							reward(3, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case MEGA -> {
				// 2-stop only — 3-stop on a 4-letter word stripes per-letter.
				tc.displayName = "<b><gradient:#FF0080:#7928CA>Mega</gradient></b>";
				tc.glowColor = "aqua";
				tc.levelRange = new Range(100, 130);
				tc.scale = 2.5F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.minActive = 1;
				tc.despawnAfter = Time.minutes(3);
				tc.includeAllMegaCapable = true;
				tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 15, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:soul_fire_flame", 4, 5, 0.5);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							reward(15, 2, 4, "give {user} cobblemon:exp_candy_xl {quantity}"),
							namedReward(14, 1, 1, "Mega Stone", "give_group {user} megastone"),
							reward(11, 1, 1, "give {user} cobblemon:life_orb {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:ability_capsule {quantity}"),
							namedReward(9, 200, 500, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(8, 1, 1, "give {user} cobblemon:choice_band {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:choice_specs {quantity}"),
							reward(7, 1, 3, "give {user} cobblemon:fire_gem {quantity}"),
							reward(7, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:focus_sash {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:assault_vest {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:rocky_helmet {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:ability_patch {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:dragon_scale {quantity}"),
							reward(2, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case MYTHICAL -> {
				tc.displayName = "<b><gradient:#FF1744:#FFD700:#FF1744>Mythical</gradient></b>";
				tc.glowColor = "red";
				tc.levelRange = new Range(120, 150);
				tc.scale = 3.0F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.minActive = 1;
				tc.despawnAfter = Time.minutes(3);
				// Ultra Beasts are bundled into Mythical — treated as mythical-equivalent power level.
				tc.pokemonClasses = List.of(PokemonClass.MYTHICAL, PokemonClass.ULTRA_BEAST);
				tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.spawnEffect = EffectConfig.burst("minecraft:dragon_breath", 25, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:flame", 3, 6, 0.5);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							reward(12, 3, 5, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(11, 4, 8, "give {user} cobblemon:rare_candy {quantity}"),
							reward(11, 1, 2, "give {user} cobblemon:ability_patch {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:max_revive {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:full_restore {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:max_elixir {quantity}"),
							namedReward(9, 500, 1000, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(8, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:old_amber_fossil {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:claw_fossil {quantity}"),
							namedReward(7, 1, 1, "Mythical Plushie", "plushie give_class {user} MYTHICAL false"),
							reward(7, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:soothe_bell {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:lucky_egg {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
			}
			return tc;
		}
		private static BossReward reward(double weight, int minQuantity, int maxQuantity, String... commands) {
			return new BossReward(
					weight,
					new Range(minQuantity, maxQuantity),
					new MessageBuilderList(Arrays.asList(commands)),
					null
			);
		}
		private static BossReward namedReward(double weight, int minQuantity, int maxQuantity, String displayName, String... commands) {
			return new BossReward(
					weight,
					new Range(minQuantity, maxQuantity),
					new MessageBuilderList(Arrays.asList(commands)),
					displayName
			);
		}
	}
