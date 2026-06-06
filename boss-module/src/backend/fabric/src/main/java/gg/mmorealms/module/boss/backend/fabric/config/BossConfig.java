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
		/** WORLD_CHAT-level spawn line (sent only to players in the boss's world). */
		public MessageBuilder bossSpawnedAnnouncementWorld = new MessageBuilder(
				"<dark_gray>[<{glow_color}>Boss</dark_gray>] <{glow_color}>A wild Lv.{level} {tier_display} <bold>{species}</bold> has appeared nearby!"
		);
		/** GLOBAL_CHAT-level spawn line (proxy broadcast). */
		public MessageBuilder bossSpawnedAnnouncementGlobal = new MessageBuilder(
				"<dark_gray>» <{glow_color}><bold>BOSS</bold></dark_gray> » <{glow_color}>A {tier_display} <bold>{species}</bold> has appeared in the Wild!"
		);
		/** TITLE-level spawn — chat broadcast (shown above title), then the title + subtitle below. */
		public MessageBuilder bossSpawnedAnnouncementTitleChat = new MessageBuilder(
				"<dark_gray>» <{glow_color}><bold>★ {tier_display} BOSS ★</bold></dark_gray> » <{glow_color}><bold>{species}</bold> <gray>has been spotted in the Wild!"
		);
		public MessageBuilder bossSpawnedAnnouncementTitleMain = new MessageBuilder(
				"<{glow_color}><bold>★ {tier_display} BOSS ★</bold>"
		);
		public MessageBuilder bossSpawnedAnnouncementTitleSub = new MessageBuilder(
				"<{glow_color}>{species} <gray>has appeared!"
		);
		/** WORLD_CHAT-level defeat line. */
		public MessageBuilder bossDefeatedAnnouncementWorld = new MessageBuilder(
				"<dark_gray>[<{glow_color}>Boss</dark_gray>] <{glow_color}>{player} defeated the {tier_display} <bold>{species}</bold>!"
		);
		/** GLOBAL_CHAT-level defeat line. */
		public MessageBuilder bossDefeatedAnnouncementGlobal = new MessageBuilder(
				"<dark_gray>» <{glow_color}><bold>BOSS DEFEATED</bold></dark_gray> » <yellow>{player}</yellow> <{glow_color}>defeated the {tier_display} <bold>{species}</bold>!"
		);
		/** TITLE-level defeat — chat broadcast + title + subtitle. */
		public MessageBuilder bossDefeatedAnnouncementTitleChat = new MessageBuilder(
				"<dark_gray>» <{glow_color}><bold>★ {tier_display} BOSS DEFEATED ★</bold></dark_gray> » <yellow>{player}</yellow> <{glow_color}>vanquished the <bold>{species}</bold>!"
		);
		public MessageBuilder bossDefeatedAnnouncementTitleMain = new MessageBuilder(
				"<{glow_color}><bold>★ BOSS DEFEATED ★</bold>"
		);
		public MessageBuilder bossDefeatedAnnouncementTitleSub = new MessageBuilder(
				"<yellow>{player}</yellow> <{glow_color}>defeated <bold>{species}</bold>"
		);
		public MessageBuilder bossDisplayName = new MessageBuilder("<{glow_color}>★ {tier_display} {species} ★ <gray>Lv.{level}");

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
				"<gold>{short_id} <white>{tier} <green>{species} <gray>lv.{level} <dark_gray>| <gray>{location}"
		);
		public MessageBuilder adminListLocation = new MessageBuilder("<gray>{dimension} <dark_gray>{x} {y} {z}");
		public MessageBuilder adminListLocationUnloaded = new MessageBuilder("<dark_gray>(entity not loaded)");

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
	}
}
