package gg.mmorealms.module.boss.common;

import org.jetbrains.annotations.NotNull;

public final class BossTierTheme {
	private BossTierTheme() {}

	public enum BannerKind {
		SPAWN,
		DEFEAT
	}

	public static String title(@NotNull BossTier tier, @NotNull BannerKind kind) {
		String frame = switch (kind) {
			case SPAWN -> switch (tier) {
				case COMMON, UNCOMMON -> "<<< ⚔ BOSS DEX ⚔ >>>";
				case RARE, ULTRA_RARE, LEGENDARY, MEGA, MYTHICAL -> "⟪⟪⟪ ⚔ BOSS DEX ⚔ ⟫⟫⟫";
			};
			case DEFEAT -> switch (tier) {
				case COMMON, UNCOMMON -> "<<< ⚔ BOSS VANQUISHED ⚔ >>>";
				case RARE, ULTRA_RARE, LEGENDARY, MEGA, MYTHICAL -> "⟪⟪⟪ ⚔ BOSS VANQUISHED ⚔ ⟫⟫⟫";
			};
		};
		String styleStart = isHighTier(tier) ? "<bold>" : "";
		String styleEnd = isHighTier(tier) ? "</bold>" : "";
		return wrapTierGradient(tier, styleStart + frame + styleEnd);
	}

	public static String spawnLine(@NotNull BossTier tier, @NotNull String speciesDisplay, @NotNull String biome) {
		String species = highlightSpecies(tier, capitalizeFirst(speciesDisplay));
		String biomeStyled = highlightBiome(tier, biome);
		String body = switch (tier) {
			case COMMON -> "A Common Boss " + species + " has been seen in the " + biomeStyled + ".";
			case UNCOMMON -> "A Rogue Boss " + species + " has been spotted in the " + biomeStyled + ".";
			case RARE -> "A Rare Boss " + species + " has been stirring in the " + biomeStyled + ".";
			case ULTRA_RARE -> "An Ultra Rare Boss " + species + " has been seen in the " + biomeStyled + ".";
			case LEGENDARY -> "A Legendary Boss " + species + " now commands " + biomeStyled + ".";
			case MEGA -> "A Mega Boss " + species + " has awakened with brutal force in the " + biomeStyled + ".";
			case MYTHICAL -> "An Ancient Mythical Boss " + species + " has returned to the " + biomeStyled + ".";
		};
		return wrapTierGradient(tier, body);
	}

	public static String defeatLine(@NotNull BossTier tier, @NotNull String speciesDisplay, @NotNull String player) {
		String species = capitalizeFirst(speciesDisplay);
		String body = switch (tier) {
			case COMMON -> "The Common Boss " + species + " has been bested by " + player + ".";
			case UNCOMMON -> "The Uncommon Boss " + species + " has been defeated by " + player + ".";
			case RARE -> "The Rare Boss " + species + " has been overpowered by " + player + ".";
			case ULTRA_RARE -> "The Ultra Rare Boss " + species + " has been vanquished by " + player + ".";
			case LEGENDARY -> "The Legendary Boss " + species + " has been conquered by " + player + ".";
			case MEGA -> "The Mega Boss " + species + " has been obliterated by " + player + ".";
			case MYTHICAL -> "The Mythical Boss " + species + " has been slain by " + player + ".";
		};
		return wrapTierGradient(tier, body);
	}

	public static String rewardHeaderTemplate() {
		return "<gradient:#FDE7A1:#D8A24A><bold>⟪ ✦ BOSS REWARDS ✦ ⟫</bold></gradient>";
	}

	public static String rewardSummaryTemplate() {
		return "<gradient:#FF7BBF:#FF4FD8>Claimed: <green>{rewards}</green></gradient>";
	}

	public static String bossDisplayNameTemplate() {
		return "<gradient:#{tier_start}:#{tier_end}><bold>✦ {species} ✦</bold></gradient>";
	}

	public static String bossReactionTitle(@NotNull BossTier tier) {
		String body = isHighTier(tier)
				? "<bold>✠ BOSS OMEN ✠</bold>"
				: "✠ BOSS RESPONSE ✠";
		return wrapTierGradient(tier, body);
	}

	/** Battle-start title, e.g. "Mega Boss" in the tier gradient. */
	public static String battleCryTitle(@NotNull BossTier tier) {
		return wrapTierGradient(tier, "<bold>" + tierPlainName(tier) + " Boss</bold>");
	}

	public static String tierPlainName(@NotNull BossTier tier) {
		return switch (tier) {
			case COMMON -> "Common";
			case UNCOMMON -> "Uncommon";
			case RARE -> "Rare";
			case ULTRA_RARE -> "Ultra Rare";
			case LEGENDARY -> "Legendary";
			case MEGA -> "Mega";
			case MYTHICAL -> "Mythical";
		};
	}

	public static String personalDefeatTemplate() {
		return "<gradient:#FDE7A1:#D8A24A><bold>⚔ VICTORY!</bold></gradient> <white>You defeated the {tier_display} <white>boss <yellow><bold>{species}</bold></yellow><white>!";
	}

	public static String victoryLegendaryTitleTemplate() {
		return "<gradient:#D79A00:#FFE98A><bold>LEGENDARY VICTORY</bold></gradient>";
	}

	public static String victoryMegaTitleTemplate() {
		return "<gradient:#00D4FF:#FF4FB1><bold>MEGA CONQUEST</bold></gradient>";
	}

	public static String victoryMythicalTitleTemplate() {
		return "<gradient:#E23B2E:#FFB15A><bold>MYTHICAL ASCENDANT</bold></gradient>";
	}

	public static String adminDespawnSuccessTemplate() {
		return "<gradient:#FDE7A1:#D8A24A><bold>✓ DESPAWNED</bold></gradient> <white>{tier} <green>{species}</green> <dark_gray>({short_id})";
	}

	public static String adminDespawnSuccessAllTemplate() {
		return "<gradient:#FDE7A1:#D8A24A><bold>✓ DESPAWNED</bold></gradient> <white>{count} boss(es)";
	}

	public static String adminDespawnSuccessTierTemplate() {
		return "<gradient:#FDE7A1:#D8A24A><bold>✓ DESPAWNED</bold></gradient> <white>{count} <green>{tier}</green> boss(es)";
	}

	public static String adminDespawnQueuedBattleTemplate() {
		return "<gradient:#FF7BBF:#FF4FD8><bold>⏳ QUEUED</bold></gradient> <yellow>Boss <white>{short_id}</white><yellow> will despawn after the battle ends.";
	}

	public static String adminDespawnQueuedCountTemplate() {
		return "<gradient:#FF7BBF:#FF4FD8><bold>⏳ QUEUED</bold></gradient> <yellow>{count} boss(es) are still in battle and will despawn after they finish. Use <white>/boss admin list</white> for IDs.";
	}

	public static String adminUsageRootTemplate() {
		return "<yellow>/boss admin <gray>subcommands:\n"
				+ "<gray>  spawn <tier> [species] [level] [shiny] [x y z]\n"
				+ "<gray>  despawn <all | tier <tier> | <short-id>>\n"
				+ "<gray>  list <dark_gray>(show active bosses with their short-IDs)";
	}

	public static String adminUsageSpawnTemplate() {
		return "<red>/boss admin spawn <tier> [species] [level] [shiny] [x y z]";
	}

	public static String adminUsageDespawnTemplate() {
		return "<red>/boss admin despawn <all | tier <tier> | <short-id>>";
	}

	public static String adminUsageDespawnTierTemplate() {
		return "<red>/boss admin despawn tier <tier>";
	}

	public static String adminSpawnSuccessTemplate() {
		return "<green>Spawned <white>{tier} <green>{species} <gray>lv.{level}{shiny_suffix} <gray>at <aqua>({x}, {y}, {z}) <dark_gray>| <gold>{short_id}";
	}

	public static String adminSpawnShinySuffixTemplate() {
		return " <yellow>✨";
	}

	public static String adminListEmptyTemplate() {
		return "<gray>No active bosses on this server.";
	}

	public static String adminListHeaderTemplate() {
		return "<gradient:#FF4D4D:#4DA3FF><bold>ACTIVE BOSSES ({count}):</bold></gradient>";
	}

	public static String adminListRowTemplate() {
		return "<gradient:#FF4D4D:#4DA3FF>{short_id}</gradient> <white>{tier} <gradient:#FF4D4D:#4DA3FF>{species}</gradient> <gray>lv.{level}{origin} <dark_gray>| <gradient:#4DA3FF:#FF4D4D>{location}</gradient>";
	}

	public static String adminListLocationTemplate() {
		return "<gradient:#FF4D4D:#4DA3FF>{dimension}</gradient> <dark_gray>{x} {y} {z}";
	}

	public static String adminModuleNotInitializedTemplate() {
		return "<red>Boss module is not initialized on this server.";
	}

	public static String adminTierUnknownTemplate() {
		return "<red>Unknown tier: <white>{tier}";
	}

	public static String adminLevelInvalidTemplate() {
		return "<red>Invalid level: <white>{value}";
	}

	public static String adminLevelOutOfRangeTemplate() {
		return "<red>Level <white>{level}<red> is outside tier <white>{tier}<red> range (<white>{min}<red>-<white>{max}<red>).";
	}

	public static String adminSpeciesUnknownTemplate() {
		return "<red>Unknown Pokémon species: <white>{species}";
	}

	public static String adminSpeciesNotInPoolTemplate() {
		return "<red>Species <white>{species}<red> is not in tier <white>{tier}<red> spawn pool. Edit the tier config to add it.";
	}

	public static String adminCoordsIncompleteTemplate() {
		return "<red>Coordinates require all three values: <white>x y z<red>.";
	}

	public static String adminCoordsInvalidTemplate() {
		return "<red>Invalid coordinate: <white>{value}";
	}

	public static String adminCoordsRequirePlayerTemplate() {
		return "<red>Forced coordinates require a player sender — run in-game or omit <white>x y z<red>.";
	}

	public static String adminNoAnchorTemplate() {
		return "<red>No eligible anchor player on this server.";
	}

	public static String adminPositionNotFoundTemplate() {
		return "<red>Could not find a valid spawn position near anchor.";
	}

	public static String adminSpawnFailedTemplate() {
		return "<red>Boss spawn failed — check server log.";
	}

	public static String adminShortIdInvalidTemplate() {
		return "<red>Short-ID must be exactly 8 hex characters. Use /boss admin list to see active short-IDs.";
	}

	public static String adminBossNotFoundTemplate() {
		return "<red>No active boss matches <white>{value}<red>. Use tab-complete or /boss admin list.";
	}

	public static String adminDespawnSuccessAllWithIdsTemplate() {
		return "<gradient:#FDE7A1:#D8A24A><bold>✓ DESPAWNED</bold></gradient> <white>{count} boss(es): <gold>{short_ids}</gold>";
	}

	public static String adminDespawnSuccessTierWithIdsTemplate() {
		return "<gradient:#FDE7A1:#D8A24A><bold>✓ DESPAWNED</bold></gradient> <white>{count} <green>{tier}</green> boss(es): <gold>{short_ids}</gold>";
	}

	public static String wrapTierGradient(@NotNull BossTier tier, @NotNull String body) {
		// MiniMessage gradient colours must be prefixed with '#'; without it the tag is invalid
		// and renders as literal text (no colour/gradient).
		return "<gradient:#" + tierLineStart(tier) + ":#" + tierLineEnd(tier) + ">" + body + "</gradient>";
	}

	public static boolean isHighTier(@NotNull BossTier tier) {
		return tier == BossTier.LEGENDARY || tier == BossTier.MEGA || tier == BossTier.MYTHICAL;
	}

	public static String tierLineStart(@NotNull BossTier tier) {
		return switch (tier) {
			case COMMON -> "B9B1A3";
			case UNCOMMON -> "4CD137";
			case RARE -> "2F80ED";
			case ULTRA_RARE -> "C56CFF";
			case LEGENDARY -> "D79A00";
			case MEGA -> "00D4FF";
			case MYTHICAL -> "E23B2E";
		};
	}

	public static String tierLineEnd(@NotNull BossTier tier) {
		return switch (tier) {
			case COMMON -> "F4ECE0";
			case UNCOMMON -> "D8FF9C";
			case RARE -> "7FDBFF";
			case ULTRA_RARE -> "FFB8FF";
			case LEGENDARY -> "FFE98A";
			case MEGA -> "FF4FB1";
			case MYTHICAL -> "FFB15A";
		};
	}

	private static String capitalizeFirst(@NotNull String text) {
		if (text.isEmpty()) {
			return text;
		}
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	private static String highlightSpecies(@NotNull BossTier tier, @NotNull String species) {
		String start = switch (tier) {
			case COMMON -> "D9D9D9";
			case UNCOMMON -> "A6FF9E";
			case RARE -> "8FD3FF";
			case ULTRA_RARE -> "FFB8FF";
			case LEGENDARY -> "FFE98A";
			case MEGA -> "FF92D0";
			case MYTHICAL -> "FFD4A0";
		};
		String end = switch (tier) {
			case COMMON -> "FFFFFF";
			case UNCOMMON -> "E6FFD8";
			case RARE -> "D7F3FF";
			case ULTRA_RARE -> "FFD1FF";
			case LEGENDARY -> "FFF4BF";
			case MEGA -> "FFD1EA";
			case MYTHICAL -> "FFE6C8";
		};
		return "<gradient:#" + start + ":#" + end + "><bold>" + species + "</bold></gradient>";
	}

	private static String highlightBiome(@NotNull BossTier tier, @NotNull String biome) {
		String start = switch (tier) {
			case COMMON -> "A7B7C8";
			case UNCOMMON -> "7CDA8C";
			case RARE -> "7DBBFF";
			case ULTRA_RARE -> "D8B8FF";
			case LEGENDARY -> "F5D56E";
			case MEGA -> "FF9CDA";
			case MYTHICAL -> "FFB88A";
		};
		String end = switch (tier) {
			case COMMON -> "EEF3F8";
			case UNCOMMON -> "D9FFE0";
			case RARE -> "CDEBFF";
			case ULTRA_RARE -> "F1DAFF";
			case LEGENDARY -> "FFF1AA";
			case MEGA -> "FFD0EC";
			case MYTHICAL -> "FFDDBA";
		};
		return "<gradient:#" + start + ":#" + end + ">" + biome + "</gradient>";
	}
}
