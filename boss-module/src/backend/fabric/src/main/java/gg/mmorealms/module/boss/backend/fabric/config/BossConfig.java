package gg.mmorealms.module.boss.backend.fabric.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.boss.common.BossTierTheme;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BossConfig {

	public List<String> allowedDimensions = List.of("minecraft:overworld");
	public Range spawnRangeFromPlayer = new Range(50, 200);
	public int maxCandidateAttempts = 50;
	public boolean ignoreLeaves = true;
	public Range randomSpawnRangeY = new Range(60, 120);
	public int canPokemonFitCheckRadius = 1;

	public float bossDamageTakenMultiplier = 0.2f;
	public float bossDamageDealtMultiplier = 2.5f;

	public Time bossFightCooldown = Time.minutes(2);

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
		public MessageBuilder bossSpawnedAnnouncementWorld = new MessageBuilder(
				"{announcement_title}\n{announcement_line}"
		);
		public MessageBuilder bossSpawnedAnnouncementGlobal = new MessageBuilder(
				"{announcement_title}\n{announcement_line}"
		);
		public MessageBuilder bossDefeatedAnnouncementWorld = new MessageBuilder(
				"{announcement_title}\n{announcement_line}"
		);
		public MessageBuilder bossDefeatedAnnouncementGlobal = new MessageBuilder(
				"{announcement_title}\n{announcement_line}"
		);
		public MessageBuilder bossDisplayName = new MessageBuilder(
				BossTierTheme.bossDisplayNameTemplate()
		);

		public MessageBuilder bossPersonalDefeat = new MessageBuilder(
				BossTierTheme.personalDefeatTemplate()
		);

		public MessageBuilder bossOnCooldown = new MessageBuilder(
				"<red>You must wait {time} before challenging this boss again."
		);

		public MessageBuilder bossVictoryLegendaryTitle = new MessageBuilder(
				BossTierTheme.victoryLegendaryTitleTemplate()
		);
		public List<String> bossVictoryLegendarySubtitles = List.of(
				"You conquered the Legendary boss {species}.",
				"The legendary {species} has met its match.",
				"Few have stood where you stand now."
		);
		public MessageBuilder bossVictoryMegaTitle = new MessageBuilder(
				BossTierTheme.victoryMegaTitleTemplate()
		);
		public List<String> bossVictoryMegaSubtitles = List.of(
				"You obliterated the Mega boss {species}.",
				"The mighty {species} was no match for you.",
				"You have done the impossible."
		);
		public MessageBuilder bossVictoryMythicalTitle = new MessageBuilder(
				BossTierTheme.victoryMythicalTitleTemplate()
		);
		public List<String> bossVictoryMythicalSubtitles = List.of(
				"You defeated the Mythical boss {species}.",
				"The myths were wrong. You proved it.",
				"What was eternal is no more.",
				"The myth of {species} ends here."
		);

		public MessageBuilder bossRewardWinnerHeader = new MessageBuilder(
				BossTierTheme.rewardHeaderTemplate()
		);
		public MessageBuilder bossRewardWinnerSummary = new MessageBuilder(
				BossTierTheme.rewardSummaryTemplate()
		);

		public MessageBuilder adminUsageRoot = new MessageBuilder(
				BossTierTheme.adminUsageRootTemplate()
		);
		public MessageBuilder adminUsageSpawn = new MessageBuilder(BossTierTheme.adminUsageSpawnTemplate());
		public MessageBuilder adminUsageDespawn = new MessageBuilder(BossTierTheme.adminUsageDespawnTemplate());
		public MessageBuilder adminUsageDespawnTier = new MessageBuilder(BossTierTheme.adminUsageDespawnTierTemplate());

		public MessageBuilder adminSpawnSuccess = new MessageBuilder(
				BossTierTheme.adminSpawnSuccessTemplate()
		);
		public MessageBuilder adminSpawnShinySuffix = new MessageBuilder(BossTierTheme.adminSpawnShinySuffixTemplate());
		public MessageBuilder adminDespawnSuccess = new MessageBuilder(
				BossTierTheme.adminDespawnSuccessTemplate()
		);
		public MessageBuilder adminListEmpty = new MessageBuilder(BossTierTheme.adminListEmptyTemplate());
		public MessageBuilder adminListHeader = new MessageBuilder(BossTierTheme.adminListHeaderTemplate());
		public MessageBuilder adminListRow = new MessageBuilder(
				BossTierTheme.adminListRowTemplate()
		);
		public MessageBuilder adminListLocation = new MessageBuilder(BossTierTheme.adminListLocationTemplate());

		public MessageBuilder adminModuleNotInitialized = new MessageBuilder(BossTierTheme.adminModuleNotInitializedTemplate());
		public MessageBuilder adminTierUnknown = new MessageBuilder(BossTierTheme.adminTierUnknownTemplate());
		public MessageBuilder adminLevelInvalid = new MessageBuilder(BossTierTheme.adminLevelInvalidTemplate());
		public MessageBuilder adminLevelOutOfRange = new MessageBuilder(BossTierTheme.adminLevelOutOfRangeTemplate());
		public MessageBuilder adminSpeciesUnknown = new MessageBuilder(BossTierTheme.adminSpeciesUnknownTemplate());
		public MessageBuilder adminSpeciesNotInPool = new MessageBuilder(BossTierTheme.adminSpeciesNotInPoolTemplate());
		public MessageBuilder adminCoordsIncomplete = new MessageBuilder(BossTierTheme.adminCoordsIncompleteTemplate());
		public MessageBuilder adminCoordsInvalid = new MessageBuilder(BossTierTheme.adminCoordsInvalidTemplate());
		public MessageBuilder adminCoordsRequirePlayer = new MessageBuilder(BossTierTheme.adminCoordsRequirePlayerTemplate());
		public MessageBuilder adminNoAnchor = new MessageBuilder(BossTierTheme.adminNoAnchorTemplate());
		public MessageBuilder adminPositionNotFound = new MessageBuilder(BossTierTheme.adminPositionNotFoundTemplate());
		public MessageBuilder adminSpawnFailed = new MessageBuilder(BossTierTheme.adminSpawnFailedTemplate());
		public MessageBuilder adminShortIdInvalid = new MessageBuilder(BossTierTheme.adminShortIdInvalidTemplate());
		public MessageBuilder adminBossNotFound = new MessageBuilder(BossTierTheme.adminBossNotFoundTemplate());
		public MessageBuilder adminDespawnQueuedBattle = new MessageBuilder(
				BossTierTheme.adminDespawnQueuedBattleTemplate()
		);
		public MessageBuilder adminDespawnQueuedCount = new MessageBuilder(
				BossTierTheme.adminDespawnQueuedCountTemplate()
		);
		public MessageBuilder adminDespawnSuccessAllWithIds = new MessageBuilder(
				BossTierTheme.adminDespawnSuccessAllWithIdsTemplate()
		);
		public MessageBuilder adminDespawnSuccessTierWithIds = new MessageBuilder(
				BossTierTheme.adminDespawnSuccessTierWithIdsTemplate()
		);
	}
}
