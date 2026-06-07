package gg.mmorealms.module.boss.backend.fabric.config;

import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.boss.common.BossTier;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BossConfig {

	public List<String> allowedDimensions = List.of("minecraft:overworld");
	public Range spawnRangeFromPlayer = new Range(50, 200);
	public int maxCandidateAttempts = 50;
	public int biomeSearchStartPositionY = 70;
	public boolean ignoreLeaves = true;
	public Range randomSpawnRangeY = new Range(60, 120);
	public int canPokemonFitCheckRadius = 1;

	public Map<BossTier, TierConfig> tiers = defaultTiers();

	public Lang lang = new Lang();

	private static Map<BossTier, TierConfig> defaultTiers() {
		Map<BossTier, TierConfig> map = new EnumMap<>(BossTier.class);
		for (BossTier tier : BossTier.values()) {
			map.put(tier, TierConfig.defaultsFor(tier));
		}
		return map;
	}

	public static class Lang {
		/* ---------- Announcements ---------- */
		/** WORLD_CHAT spawn — light tone, sent only to wild-shard players in the boss's dimension. */
		public MessageBuilder bossSpawnedAnnouncementWorld = new MessageBuilder(
				"<light_purple>[Boss] <{glow_color}>A <bold>{tier_display}</bold> <{glow_color}>{species} <gray>appeared in <{glow_color}>{biome}<gray>!"
		);
		/** GLOBAL_CHAT spawn — used for LEG/MEGA/MYTH only. */
		public MessageBuilder bossSpawnedAnnouncementGlobal = new MessageBuilder(
				"<{glow_color}>«« <bold>WILD BOSS</bold> »» <{glow_color}>A <bold>{tier_display}</bold> <bold>{species}</bold> <{glow_color}>has appeared in the Wild!"
		);
		/** WORLD_CHAT defeat — opt-in only; default behaviour for low tiers is OFF (winner-only personal message). */
		public MessageBuilder bossDefeatedAnnouncementWorld = new MessageBuilder(
				"<light_purple>[Boss] <{glow_color}>{player} defeated the <bold>{tier_display}</bold> <bold>{species}</bold>!"
		);
		/** GLOBAL_CHAT defeat — used for LEG/MEGA/MYTH. */
		public MessageBuilder bossDefeatedAnnouncementGlobal = new MessageBuilder(
				"<{glow_color}>«« <bold>BOSS DEFEATED</bold> »» <yellow><bold>{player}</bold></yellow> <{glow_color}>has vanquished the <bold>{tier_display}</bold> <bold>{species}</bold>!"
		);
		/** Nameplate above the boss entity. Species/level live in the battle UI; keep this terse. */
		public MessageBuilder bossDisplayName = new MessageBuilder("<{glow_color}><bold>★ {tier_display} Boss ★</bold>");

		/* ---------- Personal defeat — always to winner, regardless of announceOnDefeat ---------- */
		public MessageBuilder bossPersonalDefeat = new MessageBuilder(
				"<green>You defeated the <{glow_color}><bold>{tier_display} {species}</bold><green>!"
		);

		/* ---------- Reward summary — only to winner, only when rewards rolled ---------- */
		public MessageBuilder bossRewardWinnerHeader = new MessageBuilder(
				"<dark_gray>«« <{glow_color}><bold>BOSS REWARDS</bold></dark_gray> »» <{glow_color}>You defeated the <bold>{tier_display} {species}</bold>:"
		);
		/** Single aggregated line — {rewards} is a comma-joined list of '<quantity>x <item>' entries. */
		public MessageBuilder bossRewardWinnerSummary = new MessageBuilder(
				"<{glow_color}>You received <yellow>{rewards}<{glow_color}>!"
		);

		/* ---------- Admin command — root usage ---------- */
		public MessageBuilder adminUsageRoot = new MessageBuilder(
				"<yellow>/boss admin <gray>subcommands:\n"
						+ "<gray>  spawn <tier> [species] [level] [shiny] [x y z]\n"
						+ "<gray>  despawn <all | tier <tier> | <short-id>>\n"
						+ "<gray>  list <dark_gray>(show active bosses with their short-IDs)"
		);
		public MessageBuilder adminUsageSpawn = new MessageBuilder("<red>/boss admin spawn <tier> [species] [level] [shiny] [x y z]");
		public MessageBuilder adminUsageDespawn = new MessageBuilder("<red>/boss admin despawn <all | tier <tier> | <short-id>>");
		public MessageBuilder adminUsageDespawnTier = new MessageBuilder("<red>/boss admin despawn tier <tier>");

		/* ---------- Admin command — success ---------- */
		public MessageBuilder adminSpawnSuccess = new MessageBuilder(
				"<green>Spawned <white>{tier} <green>{species} <gray>lv.{level}{shiny_suffix} <gray>at <aqua>({x}, {y}, {z}) <dark_gray>| <gold>{short_id}"
		);
		public MessageBuilder adminSpawnShinySuffix = new MessageBuilder(" <yellow>✨");
		public MessageBuilder adminDespawnSuccess = new MessageBuilder("<green>Despawn requested: <white>{tier} <green>{species} <dark_gray>({short_id})");
		public MessageBuilder adminDespawnSuccessAll = new MessageBuilder("<green>Requested despawn of {count} boss(es).");
		public MessageBuilder adminDespawnSuccessTier = new MessageBuilder("<green>Requested despawn of {count} {tier} boss(es).");
		/* ---------- Admin command — list ---------- */
		public MessageBuilder adminListEmpty = new MessageBuilder("<gray>No active bosses on this server.");
		public MessageBuilder adminListHeader = new MessageBuilder("<yellow>Active bosses ({count}):");
		public MessageBuilder adminListRow = new MessageBuilder(
				"<gold>{short_id} <white>{tier} <green>{species} <gray>lv.{level}{origin} <dark_gray>| <gray>{location}"
		);
		public MessageBuilder adminListLocation = new MessageBuilder("<gray>{dimension} <dark_gray>{x} {y} {z}");

		/* ---------- Admin command — errors ---------- */
		public MessageBuilder adminModuleNotInitialized = new MessageBuilder("<red>Boss module is not initialized on this server.");
		public MessageBuilder adminTierUnknown = new MessageBuilder("<red>Unknown tier: <white>{tier}");
		public MessageBuilder adminLevelInvalid = new MessageBuilder("<red>Invalid level: <white>{value}");
		public MessageBuilder adminLevelOutOfRange = new MessageBuilder("<red>Level <white>{level}<red> is outside tier <white>{tier}<red> range (<white>{min}<red>-<white>{max}<red>).");
		public MessageBuilder adminSpeciesUnknown = new MessageBuilder("<red>Unknown Pokémon species: <white>{species}");
		public MessageBuilder adminSpeciesNotInPool = new MessageBuilder("<red>Species <white>{species}<red> is not in tier <white>{tier}<red> spawn pool. Edit the tier config to add it.");
		public MessageBuilder adminCoordsIncomplete = new MessageBuilder("<red>Coordinates require all three values: <white>x y z<red>.");
		public MessageBuilder adminCoordsInvalid = new MessageBuilder("<red>Invalid coordinate: <white>{value}");
		public MessageBuilder adminNoAnchor = new MessageBuilder("<red>No eligible anchor player on this server.");
		public MessageBuilder adminPositionNotFound = new MessageBuilder("<red>Could not find a valid spawn position near anchor.");
		public MessageBuilder adminSpawnFailed = new MessageBuilder("<red>Boss spawn failed — check server log.");
		public MessageBuilder adminShortIdInvalid = new MessageBuilder("<red>Short-ID must be exactly 8 hex characters. Use /boss admin list to see active short-IDs.");
		public MessageBuilder adminBossNotFound = new MessageBuilder("<red>No active boss matches <white>{value}<red>. Use tab-complete or /boss admin list.");
		public MessageBuilder adminDespawnQueuedBattle = new MessageBuilder("<yellow>Boss <white>{short_id}<yellow> is currently in battle — queued to despawn once the battle ends.");
	}
}
