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

	public Range levelRange;
	public float scale = 1.0F;
	public boolean maxIvs = true;
	public boolean maxEvs = true;
	public @Nullable String heldItem;

	public Time despawnAfter = Time.minutes(30);
	public int maxActive = 1;
	public int minActive = 1;

	public List<PokemonClass> pokemonClasses = List.of();
	public List<String> extraSpecies = List.of();
	public List<String> excludedSpecies = List.of();
	public boolean includeAllMegaCapable = false;
	public @Nullable Range bstRange;

	public List<String> allowedDimensions;
	public List<String> biomes = List.of();

	public EffectConfig spawnEffect;
	public EffectConfig ambientEffect;

	public List<BossReward> rewards = List.of();
	public int rewardRolls = 0;
	public List<List<String>> dialogueBoxes = List.of();
	public List<List<String>> defeatDialogue = List.of();
	public List<String> battleCryTaunts = List.of();

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

	private static List<String> dialogue(String... lines) {
		return List.of(lines);
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
				tc.battleCryTaunts = List.of(
						"{species} stands its ground.",
						"{species} refuses to back down.",
						"{species} guards what is its.");
				tc.heldItem = "cobblemon:oran_berry";
				tc.levelRange = new Range(10, 30);
				tc.scale = 2.0F;
				tc.maxIvs = true;
				tc.maxActive = 2;
				tc.minActive = 1;
				tc.despawnAfter = Time.minutes(5);
				tc.announceOnSpawn = AnnounceLevel.OFF;
				tc.announceOnDefeat = AnnounceLevel.OFF;
				tc.pokemonClasses = List.of(PokemonClass.NORMAL);
				tc.bstRange = new Range(0, 349);
				tc.ambientEffect = EffectConfig.recurring("minecraft:enchant", 8, 2, 0.3);
				tc.dialogueBoxes = List.of(
						dialogue("A stubborn presence holds this stretch of the wild.", "The land refuses to look empty while it stands here."),
						dialogue("A low-born threat keeps watch over its territory.", "Nothing here is given freely."),
						dialogue("The air carries the weight of a creature shaped by the wild.", "It has no interest in yielding."),
						dialogue("A modest power still demands respect.", "Even the smallest bosses leave a mark."),
						dialogue("A simple boss remains rooted in unfamiliar ground.", "The wild has made it hard to move.")
				);
				tc.defeatDialogue = List.of(
						dialogue("You came this far. That was your first mistake.", "The wild does not hand out mercy."),
						dialogue("You challenged something smaller than you expected.", "It still knows how to bite."),
						dialogue("A weak hand reached for a stronger fate.", "It broke before it could close."),
						dialogue("This was never a safe encounter.", "It simply looked that way."),
						dialogue("You lost to a common threat.", "That should worry you more than it does.")
				);
				tc.rewardRolls = 1;
				tc.rewards = List.of(
						reward(28, 2, 4, "give {user} cobblemon:poke_ball {quantity}"),
						reward(20, 1, 2, "give {user} cobblemon:rare_candy {quantity}"),
						reward(14, 2, 3, "give {user} cobblemon:great_ball {quantity}"),
						reward(12, 1, 2, "give {user} cobblemon:exp_candy_s {quantity}"),
						reward(8, 1, 2, "give {user} cobblemon:potion {quantity}"),
						reward(6, 1, 1, "give {user} cobblemon:fire_stone {quantity}"),
						reward(5, 1, 1, "give {user} cobblemon:water_stone {quantity}"),
						reward(4, 1, 1, "give {user} cobblemon:thunder_stone {quantity}"),
						reward(3, 1, 1, "give {user} cobblemon:link_cable {quantity}")
				);
			}
			case UNCOMMON -> {
				tc.displayName = "<b><gradient:#11998E:#38EF7D>Uncommon</gradient></b>";
				tc.glowColor = "green";
				tc.battleCryTaunts = List.of(
						"{species} fights to survive.",
						"{species} won't be cornered.",
						"{species} knows every trick.");
				tc.heldItem = "cobblemon:sitrus_berry";
				tc.levelRange = new Range(20, 40);
				tc.scale = 2.2F;
					tc.maxIvs = true;
					tc.maxActive = 2;
					tc.minActive = 1;
					tc.despawnAfter = Time.minutes(5);
					tc.announceOnSpawn = AnnounceLevel.OFF;
					tc.announceOnDefeat = AnnounceLevel.OFF;
					tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(350, 449);
					tc.ambientEffect = EffectConfig.recurring("minecraft:happy_villager", 7, 3, 0.3);
					tc.dialogueBoxes = List.of(
							dialogue("A rogue presence lingers where it was never meant to stay.", "It learned to survive by moving first."),
							dialogue("A wandering boss has sharpened itself on hard roads.", "Comfort was never part of its story."),
							dialogue("The wild has turned this one into a restless threat.", "Stillness does not suit it."),
							dialogue("A lone power keeps to the margins.", "It has no need for a crowd."),
							dialogue("A clever boss survives by being difficult to catch.", "That lesson was learned well.")
					);
					tc.defeatDialogue = List.of(
							dialogue("You hesitated, and I punished it.", "That is how survival works."),
							dialogue("You thought this would be simple.", "The wild remembers arrogance."),
							dialogue("Your stride broke before mine did.", "That is enough for a defeat."),
							dialogue("A restless foe does not forgive mistakes.", "You gave me several."),
							dialogue("You were close.", "Close is where disappointment lives.")
					);
					tc.rewardRolls = 1;
					tc.rewards = List.of(
							reward(24, 2, 4, "give {user} cobblemon:great_ball {quantity}"),
							reward(18, 1, 2, "give {user} cobblemon:rare_candy {quantity}"),
							reward(14, 2, 3, "give {user} cobblemon:ultra_ball {quantity}"),
							reward(12, 1, 2, "give {user} cobblemon:exp_candy_m {quantity}"),
							namedReward(9, 100, 250, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(8, 1, 2, "give {user} cobblemon:protein {quantity}"),
							reward(7, 1, 2, "give {user} cobblemon:calcium {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:moon_stone {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:leaf_stone {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:ability_capsule {quantity}")
					);
				}
				case RARE -> {
					tc.displayName = "<b><gradient:#36D1DC:#5B86E5>Rare</gradient></b>";
					tc.glowColor = "blue";
					tc.battleCryTaunts = List.of(
							"{species} has bested many before you.",
							"{species} shows no fear.",
							"{species} fights like a veteran.");
					tc.heldItem = "cobblemon:leftovers";
				tc.levelRange = new Range(35, 55);
				tc.scale = 2.5F;
				tc.maxIvs = true;
				tc.maxActive = 2;
					tc.minActive = 1;
					tc.despawnAfter = Time.minutes(5);
					tc.pokemonClasses = List.of(PokemonClass.NORMAL);
					tc.bstRange = new Range(450, 529);
						tc.announceOnSpawn = AnnounceLevel.WORLD_CHAT;
						tc.announceOnDefeat = AnnounceLevel.WORLD_CHAT;
						tc.ambientEffect = EffectConfig.recurring("minecraft:enchant", 5, 4, 0.4);
						tc.dialogueBoxes = List.of(
								dialogue("A rare power settles into the battlefield.", "Its presence feels tempered by many fights."),
								dialogue("This boss carries the silence of a veteran.", "Nothing about it feels accidental."),
								dialogue("A refined threat stands in the open.", "Its strength has been tested and kept."),
								dialogue("The encounter tightens around a serious opponent.", "This is no casual presence."),
								dialogue("A battle-hardened force has made itself known.", "Few wins come easily against it.")
						);
						tc.defeatDialogue = List.of(
								dialogue("You had power.", "Not enough to matter."),
								dialogue("This battle was already leaning my way.", "You just arrived late to the truth."),
								dialogue("You fought like someone expecting a prize.", "I fought like something guarding a grave."),
								dialogue("You lost your opening.", "After that, the end was only a formality."),
								dialogue("Rare does not mean merciful.", "It means you should have been careful.")
						);
						tc.rewardRolls = 2;
					tc.rewards = List.of(
							reward(20, 3, 5, "give {user} cobblemon:ultra_ball {quantity}"),
							reward(16, 2, 4, "give {user} cobblemon:rare_candy {quantity}"),
							reward(13, 1, 2, "give {user} cobblemon:exp_candy_l {quantity}"),
							namedReward(10, 250, 500, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(8, 1, 1, "give {user} cobblemon:ability_capsule {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:adamant_mint {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:jolly_mint {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:focus_sash {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:expert_belt {quantity}"),
							reward(5, 1, 2, "give {user} cobblemon:calcium {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:moon_stone {quantity}")
					);
				}
					case ULTRA_RARE -> {
					tc.displayName = "<b><gradient:#DA22FF:#FFF59D:#9733EE>Ultra Rare</gradient></b>";
					tc.glowColor = "light_purple";
					tc.battleCryTaunts = List.of(
							"{species} radiates raw pressure.",
							"{species} will not be tamed.",
							"{species} towers over its rivals.");
					tc.heldItem = "cobblemon:assault_vest";
				tc.levelRange = new Range(60, 90);
				tc.scale = 2.8F;
				tc.maxIvs = true;
				tc.maxActive = 2;
					tc.minActive = 1;
					tc.despawnAfter = Time.minutes(5);
						tc.pokemonClasses = List.of(PokemonClass.NORMAL);
						tc.bstRange = new Range(530, 9999);
						tc.announceOnSpawn = AnnounceLevel.WORLD_CHAT;
						tc.announceOnDefeat = AnnounceLevel.WORLD_CHAT;
						tc.spawnEffect = EffectConfig.burst("minecraft:end_rod", 18, 0.5);
						tc.dialogueBoxes = List.of(
								dialogue("An unusually powerful presence gathers in the wild.", "The air around it feels heavier than it should."),
								dialogue("A near-elite boss stands where ordinary encounters fade.", "Its rarity only sharpens the danger."),
								dialogue("The battlefield bends around a force like this.", "It has the posture of something exceptional."),
								dialogue("A boss of rare stature leaves a deep pressure behind.", "Even its stillness feels intentional."),
								dialogue("A presence like this does not belong to the ordinary.", "It turns the encounter into a warning.")
						);
						tc.defeatDialogue = List.of(
								dialogue("You stood before something exceptional.", "And still failed to endure."),
								dialogue("This was never a fair contest.", "It was a measure of your limits."),
								dialogue("You reached for glory too early.", "The fall was already waiting."),
								dialogue("A stronger will claimed the field.", "Yours was not strong enough."),
								dialogue("You met a higher power.", "It did not spare you.")
						);
						tc.rewardRolls = 2;
					tc.rewards = List.of(
							reward(18, 2, 4, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(15, 2, 4, "give {user} cobblemon:rare_candy {quantity}"),
							namedReward(11, 500, 1000, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(10, 1, 1, "give {user} cobblemon:ability_capsule {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:choice_scarf {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:assault_vest {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:focus_sash {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:ability_patch {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:shiny_stone {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:pp_max {quantity}"),
							reward(3, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
					case LEGENDARY -> {
					tc.displayName = "<b><gradient:#FFB300:#FFF59D:#FFB300>Legendary</gradient></b>";
					tc.glowColor = "gold";
					tc.battleCryTaunts = List.of(
							"{species} refuses to yield its legend!",
							"{species} guards a legend untouched.",
							"{species} answers your challenge.");
					tc.heldItem = "cobblemon:life_orb";
				tc.levelRange = new Range(90, 120);
				tc.scale = 3.5F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.minActive = 0;
				tc.despawnAfter = Time.minutes(10);
						tc.pokemonClasses = List.of(PokemonClass.LEGENDARY);
						tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
						tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
						tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 20, 0.5);
						tc.ambientEffect = EffectConfig.recurring("minecraft:end_rod", 5, 4, 0.4);
						tc.dialogueBoxes = List.of(
								dialogue("A legendary presence has taken shape.", "The old stories feel closer when it stands here."),
								dialogue("A name-worthy boss lingers like history refusing to disappear.", "Nothing about it feels temporary."),
								dialogue("The battlefield carries the weight of something enduring.", "Legends do not arrive by accident."),
								dialogue("A force that outlasted many challengers has returned.", "Its silence speaks like authority."),
								dialogue("The air itself seems to remember this boss.", "That kind of memory is hard to erase.")
						);
						tc.defeatDialogue = List.of(
								dialogue("You dared to challenge legend.", "Legend answered."),
								dialogue("Your resolve was visible.", "So was its collapse."),
								dialogue("Few reach this far.", "Fewer leave with pride intact."),
								dialogue("You faced a ruler of this wild.", "And were judged wanting."),
								dialogue("This was not defeat.", "It was an example.")
						);
						tc.rewardRolls = 3;
					tc.rewards = List.of(
							reward(14, 2, 4, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(12, 3, 6, "give {user} cobblemon:rare_candy {quantity}"),
							namedReward(11, 1000, 2000, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(10, 1, 1, "give {user} cobblemon:life_orb {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:ability_patch {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:choice_band {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:choice_specs {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:weakness_policy {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:heavy_duty_boots {quantity}"),
							reward(6, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(5, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case MEGA -> {
				// 2-stop only — 3-stop on a 4-letter word stripes per-letter.
				tc.displayName = "<b><gradient:#FF0080:#7928CA>Mega</gradient></b>";
				// Cobblemon renders its own "Lv. X" for mega-form Pokémon; omit ours to avoid duplication.
				tc.glowColor = "aqua";
				tc.battleCryTaunts = List.of(
						"{species} erupts with unstable power!",
						"{species} burns with raw fury!",
						"{species} threatens to break loose!");
				tc.heldItem = "cobblemon:life_orb";
				tc.levelRange = new Range(100, 130);
				tc.scale = 4.0F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.minActive = 0;
					tc.despawnAfter = Time.minutes(10);
					tc.includeAllMegaCapable = true;
					tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.spawnEffect = EffectConfig.burst("minecraft:explosion", 15, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:soul_fire_flame", 4, 5, 0.5);
					tc.dialogueBoxes = List.of(
							dialogue("A transformed power hangs unstable in the air.", "Its strength feels less contained than before."),
							dialogue("A violent evolution has turned this boss into a disaster.", "The form itself looks ready to erupt."),
							dialogue("The battlefield strains under raw, unsteady force.", "This is power without comfort."),
							dialogue("A sharpened storm has taken shape here.", "It feels built to break momentum."),
							dialogue("Something immense and volatile is standing in place.", "The encounter already feels dangerous.")
					);
					tc.defeatDialogue = List.of(
							dialogue("You awakened something brutal.", "It crushed the moment you touched it."),
							dialogue("Power like this does not negotiate.", "It erases."),
							dialogue("You mistook transformation for weakness.", "That was your last mistake."),
							dialogue("The battlefield bent around me.", "You broke against it."),
							dialogue("You challenged excess.", "Excess won.")
					);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							namedReward(14, 1, 1, "Mega Stone", "give_group {user} megastone"),
							reward(12, 2, 4, "give {user} cobblemon:exp_candy_xl {quantity}"),
							namedReward(11, 1000, 2000, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(10, 1, 1, "give {user} cobblemon:life_orb {quantity}"),
							reward(9, 1, 1, "give {user} cobblemon:ability_patch {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:choice_band {quantity}"),
							reward(8, 1, 1, "give {user} cobblemon:choice_specs {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:assault_vest {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:rocky_helmet {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:focus_sash {quantity}"),
							reward(6, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
							reward(4, 1, 1, "give {user} cobblemon:master_ball {quantity}")
					);
				}
				case MYTHICAL -> {
				tc.displayName = "<b><gradient:#FF1744:#FFD700:#FF1744>Mythical</gradient></b>";
				tc.glowColor = "red";
				tc.battleCryTaunts = List.of(
						"{species} stirs from ancient slumber.",
						"{species} awakens with old wrath.",
						"{species} remembers forgotten power.");
				tc.heldItem = "cobblemon:leftovers";
				tc.levelRange = new Range(120, 150);
				tc.scale = 4.0F;
				tc.maxIvs = true;
				tc.maxActive = 1;
				tc.minActive = 0;
				tc.despawnAfter = Time.minutes(10);
					// Ultra Beasts are bundled into Mythical — treated as mythical-equivalent power level.
					tc.pokemonClasses = List.of(PokemonClass.MYTHICAL, PokemonClass.ULTRA_BEAST);
					tc.announceOnSpawn = AnnounceLevel.GLOBAL_CHAT;
					tc.announceOnDefeat = AnnounceLevel.GLOBAL_CHAT;
					tc.spawnEffect = EffectConfig.burst("minecraft:dragon_breath", 25, 0.5);
					tc.ambientEffect = EffectConfig.recurring("minecraft:flame", 3, 6, 0.5);
					tc.dialogueBoxes = List.of(
							dialogue("An ancient presence has surfaced where it should not be.", "The world seems smaller around it."),
							dialogue("A forgotten power has returned with old weight still intact.", "Its age feels older than the story around it."),
							dialogue("The encounter has crossed into something nearly myth-bound.", "There is history in the air."),
							dialogue("A relic of a harsher age stands in the wild.", "It carries the silence of long memory."),
							dialogue("Something beyond ordinary rarity has awakened here.", "The battlefield feels like a borrowed place.")
					);
					tc.defeatDialogue = List.of(
							dialogue("You stood before an ancient truth.", "It did not recognize you."),
							dialogue("Myths do not forgive intruders.", "They consume them."),
							dialogue("You reached for the impossible.", "The impossible reached back."),
							dialogue("What was sealed has not forgotten how to rule.", "You were never its equal."),
							dialogue("Even now, you do not understand.", "That is why you lost.")
					);
					tc.rewardRolls = 3;
					tc.rewards = List.of(
							namedReward(12, 2000, 4000, "Pokecoins", "balance add {user} {quantity} pokecoins"),
							reward(11, 3, 5, "give {user} cobblemon:exp_candy_xl {quantity}"),
							reward(11, 4, 8, "give {user} cobblemon:rare_candy {quantity}"),
							reward(10, 1, 2, "give {user} cobblemon:ability_patch {quantity}"),
							namedReward(8, 1, 1, "Mythical Plushie", "plushie give_class {user} MYTHICAL false"),
							reward(8, 1, 1, "give {user} cobblemon:lucky_egg {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:life_orb {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:leftovers {quantity}"),
							reward(7, 1, 1, "give {user} cobblemon:assault_vest {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:choice_specs {quantity}"),
							reward(6, 1, 1, "give {user} cobblemon:heavy_duty_boots {quantity}"),
							reward(6, 1, 1, "give {user} minecraft:enchanted_golden_apple {quantity}"),
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
