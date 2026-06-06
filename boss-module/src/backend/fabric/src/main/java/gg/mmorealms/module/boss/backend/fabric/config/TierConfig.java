package gg.mmorealms.module.boss.backend.fabric.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.boss.common.AnnounceLevel;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import net.minecraft.ChatFormatting;
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

	/**
	 * Auto-despawn delay after a boss spawns. {@code Time.minutes(0)} (or any value with
	 * {@code toMilliseconds() <= 0}) means "never auto-despawn" — boss persists until defeated
	 * or admin-removed.
	 */
	public Time despawnAfter = Time.minutes(30);
	/** Hard ceiling on system-generated active bosses for this tier. Admin spawns bypass. */
	public int maxActive = 1;
	/**
	 * Floor on system-generated active bosses for this tier. When a boss is removed and the
	 * tier active count drops below this, an immediate refill spawn fires. {@code 0} disables
	 * refill (default for legendary tiers — let the proxy scheduler decide).
	 */
	public int minActive = 0;

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
	/**
	 * Sound resource id played on TITLE-level announcements. Null/empty = no sound.
	 * Only honored when {@code announceOnSpawn == TITLE}. Example: {@code minecraft:entity.wither.spawn}.
	 */
	public @Nullable String titleSound;

	public transient ChatFormatting glowChatFmt;

	/**
	 * Returns a sane default TierConfig matching the v3 spec table.
	 * Module ships with these baked in so first-install boot passes validation without
	 * requiring admins to write any JSON. rewardRolls defaults to 0 + empty rewards
	 * (passes validation) so admins explicitly opt in to loot via config edit.
	 */
	public static TierConfig defaultsFor(BossTier tier) {
		TierConfig tc = new TierConfig();
		tc.despawnAfter = Time.minutes(30);
			switch (tier) {
				case COMMON -> {
				tc.displayName = "Common";
				tc.glowColor = "gray";
				tc.levelRange = new Range(10, 30);
				tc.scale = 1.2F;
					tc.maxIvs = true;
					tc.maxActive = 4;
					tc.minActive = 2;
					tc.announceOnSpawn = AnnounceLevel.WORLD_CHAT;
					tc.announceOnDefeat = AnnounceLevel.WORLD_CHAT;
					tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(0, 349);
					tc.rewardRolls = 1;
					tc.rewards = List.of(
							reward(35, 2, 4, "give {user} cobblemon:poke_ball {quantity}"),
							reward(20, 1, 2, "give {user} cobblemon:rare_candy {quantity}"),
							reward(12, 1, 2, "give {user} cobblemon:great_ball {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:exp_candy_s {quantity}"),
							reward(8, 1, 2, "give {user} cobblemon:potion {quantity}"),
							reward(8, 2, 4, "give {user} cobblemon:premier_ball {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:link_cable {quantity}"),
							reward(2, 1, 1, "give {user} cobblemon:adamant_mint {quantity}")
					);
				}
				case UNCOMMON -> {
				tc.displayName = "Uncommon";
				tc.glowColor = "green";
				tc.levelRange = new Range(20, 40);
				tc.scale = 1.3F;
					tc.maxIvs = true;
					tc.maxActive = 3;
					tc.minActive = 2;
					tc.announceOnSpawn = AnnounceLevel.WORLD_CHAT;
					tc.announceOnDefeat = AnnounceLevel.WORLD_CHAT;
					tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(350, 449);
					tc.rewardRolls = 1;
					tc.rewards = List.of(
							reward(30, 2, 4, "give {user} cobblemon:great_ball {quantity}"),
							reward(20, 1, 2, "give {user} cobblemon:rare_candy {quantity}"),
							reward(15, 1, 2, "give {user} cobblemon:ultra_ball {quantity}"),
							reward(12, 1, 2, "give {user} cobblemon:exp_candy_s {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:exp_candy_m {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:super_potion {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:protector {quantity}"),
							reward(2, 1, 1, "give {user} cobblemon:ability_capsule {quantity}")
					);
				}
				case RARE -> {
				tc.displayName = "Rare";
				tc.glowColor = "blue";
				tc.levelRange = new Range(35, 55);
				tc.scale = 1.5F;
				tc.maxIvs = true;
				tc.maxActive = 2;
					tc.minActive = 1;
					tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(450, 529);
					tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.ambientEffect = EffectConfig.recurring("minecraft:enchant", 5, 4, 0.4);
					tc.rewardRolls = 2;
					tc.rewards = List.of(
							reward(28, 3, 6, "give {user} cobblemon:ultra_ball {quantity}"),
							reward(22, 2, 4, "give {user} cobblemon:rare_candy {quantity}"),
							reward(15, 1, 2, "give {user} cobblemon:exp_candy_m {quantity}"),
							reward(12, 1, 2, "give {user} cobblemon:exp_candy_l {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:hyper_potion {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:adamant_mint {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:expert_belt {quantity}"),
							reward(2, 1, 1, "give {user} cobblemon:ability_capsule {quantity}")
					);
				}
				case ULTRA_RARE -> {
				tc.displayName = "Ultra Rare";
				tc.glowColor = "light_purple";
				tc.levelRange = new Range(60, 90);
				tc.scale = 1.8F;
				tc.maxIvs = true;
				tc.maxActive = 2;
					tc.minActive = 1;
				tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(530, 9999);
					tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 15, 0.5);
					tc.rewardRolls = 2;
					tc.rewards = List.of(
							reward(25, 2, 4, "give {user} cobblemon:rare_candy {quantity}"),
							reward(22, 1, 3, "give {user} cobblemon:exp_candy_l {quantity}"),
							reward(15, 1, 2, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(12, 1, 1, "give {user} cobblemon:ability_capsule {quantity}"),
							reward(10, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:focus_sash {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:ability_patch {quantity}"),
							reward(3, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case LEGENDARY -> {
				tc.displayName = "Legendary";
				tc.glowColor = "gold";
				tc.levelRange = new Range(90, 120);
				tc.scale = 2.2F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.pokemonClasses = List.of(PokemonClass.LEGENDARY);
				tc.announceOnSpawn = AnnounceLevel.TITLE;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.titleSound = "minecraft:entity.wither.spawn";
					tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 20, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:end_rod", 5, 4, 0.4);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							reward(18, 2, 4, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(15, 3, 6, "give {user} cobblemon:rare_candy {quantity}"),
							reward(12, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(11, 1, 1, "give {user} cobblemon:life_orb {quantity}"),
							reward(10, 200, 500, "balance add {user} {quantity} pokecoins"),
							reward(9, 1, 3, "give {user} cobblemon:dragon_gem {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:sun_stone {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:helix_fossil {quantity}"),
							reward(7, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(3, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case MEGA -> {
				tc.displayName = "Mega";
				tc.glowColor = "aqua";
				tc.levelRange = new Range(100, 130);
				tc.scale = 2.5F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.includeAllMegaCapable = true;
				tc.announceOnSpawn = AnnounceLevel.TITLE;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.titleSound = "minecraft:block.beacon.activate";
					tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 15, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:soul_fire_flame", 4, 5, 0.5);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							reward(17, 2, 4, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(16, 1, 1, "give_group {user} megastone"),
							reward(12, 1, 1, "give {user} cobblemon:life_orb {quantity}"),
							reward(11, 1, 1, "give {user} cobblemon:ability_capsule {quantity}"),
							reward(10, 200, 500, "balance add {user} {quantity} pokecoins"),
							reward(9, 1, 1, "give {user} cobblemon:choice_band {quantity}"),
							reward(8, 1, 3, "give {user} cobblemon:fire_gem {quantity}"),
							reward(8, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:focus_sash {quantity}"),
							reward(2, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case MYTHICAL -> {
				tc.displayName = "Mythical";
				tc.glowColor = "red";
				tc.levelRange = new Range(120, 150);
				tc.scale = 3.0F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.pokemonClasses = List.of(PokemonClass.MYTHICAL, PokemonClass.ULTRA_BEAST);
				tc.announceOnSpawn = AnnounceLevel.TITLE;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.titleSound = "minecraft:entity.wither.spawn";
					tc.spawnEffect = EffectConfig.burst("minecraft:dragon_breath", 25, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:flame", 3, 6, 0.5);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							reward(14, 3, 5, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(13, 4, 8, "give {user} cobblemon:rare_candy {quantity}"),
							reward(13, 1, 2, "give {user} cobblemon:ability_patch {quantity}"),
							reward(11, 1, 1, "give {user} cobblemon:sacred_ash {quantity}"),
							reward(10, 500, 1000, "balance add {user} {quantity} pokecoins"),
							reward(9, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:old_amber_fossil {quantity}"),
							reward(8, 1, 1, "plushie give_class {user} MYTHICAL false"),
							reward(8, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
			}
			return tc;
		}

		private static BossReward reward(double weight, int minQuantity, int maxQuantity, String... commands) {
			return new BossReward(
					weight,
					new Range(minQuantity, maxQuantity),
					new MessageBuilderList(Arrays.asList(commands))
			);
		}
	}
