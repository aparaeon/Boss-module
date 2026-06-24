package gg.mmorealms.module.boss.backend.fabric.command.admin;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.ActiveBoss;
import gg.mmorealms.module.boss.common.BossTier;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Command(aliases = {"despawn"}, arguments = {"target", "?tier"}, parent = BossAdminCommand.class)
public class BossDespawnCommand extends BackendCommand {

	public BossDespawnCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected @NotNull List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		BossFabricModule mod = BossFabricModule.instance();
		BossManager mgr = mod != null ? mod.getBossManager() : null;
		switch (argument) {
			case "target": {
				List<String> out = new ArrayList<>();
				out.add("all");
				out.add("tier");
				if (mgr != null) {
					for (ActiveBoss boss : mgr.getAllActive()) {
						out.add(BossManager.shortId(boss.pokemonUUID()));
					}
				}
				return out;
			}
			case "tier":
				return Arrays.stream(BossTier.values()).map(Enum::name).collect(Collectors.toList());
			default:
				return List.of();
		}
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		BossFabricModule mod = BossFabricModule.instance();
		if (mod == null || mod.getBossManager() == null) {
			if (mod != null && mod.getConfig() != null) {
				mod.sendLang(sender, mod.getConfig().lang.adminModuleNotInitialized);
			}
			return;
		}
		BossConfig.Lang lang = mod.getConfig().lang;
		BossManager mgr = mod.getBossManager();

		@Nullable UUID requester = (sender instanceof ServerPlayer player) ? player.getUUID() : null;

		String targetArg = arguments.isEmpty() ? null : arguments.get(0);
		if (targetArg == null) {
			mod.sendLang(sender, lang.adminUsageDespawn);
			return;
		}
		String target = targetArg.toLowerCase();

		switch (target) {
			case "all": {
				int cleaned = 0;
				int queued = 0;
				List<String> cleanedIds = new ArrayList<>();
				List<String> queuedIds = new ArrayList<>();
				for (ActiveBoss boss : new ArrayList<>(mgr.getAllActive())) {
					String shortId = BossManager.shortId(boss.pokemonUUID());
					if (mgr.dispatchDespawn(boss, requester) == BossManager.DespawnOutcome.CLEANED) {
						cleaned++;
						cleanedIds.add(shortId);
					} else {
						queued++;
						queuedIds.add(shortId);
					}
				}
				mod.sendLang(sender, lang.adminDespawnSuccessAllWithIds
						.parse("count", cleaned)
						.parse("short_ids", String.join(", ", cleanedIds)));
				if (queued > 0) {
					mod.sendLang(sender, lang.adminDespawnQueuedCount
							.parse("count", queued)
							.parse("short_ids", String.join(", ", queuedIds)));
				}
				return;
			}
			case "tier": {
				String tierArg = arguments.size() < 2 ? null : arguments.get(1);
				if (tierArg == null) {
					mod.sendLang(sender, lang.adminUsageDespawnTier);
					return;
				}
				BossTier filter;
				try {
					filter = BossTier.valueOf(tierArg.toUpperCase());
				} catch (IllegalArgumentException e) {
					mod.sendLang(sender, lang.adminTierUnknown.parse("tier", tierArg));
					return;
				}
				int cleaned = 0;
				int queued = 0;
				List<String> cleanedIds = new ArrayList<>();
				List<String> queuedIds = new ArrayList<>();
				for (ActiveBoss boss : new ArrayList<>(mgr.getAllActive())) {
					if (boss.tier() != filter) continue;
					String shortId = BossManager.shortId(boss.pokemonUUID());
					if (mgr.dispatchDespawn(boss, requester) == BossManager.DespawnOutcome.CLEANED) {
						cleaned++;
						cleanedIds.add(shortId);
					} else {
						queued++;
						queuedIds.add(shortId);
					}
				}
				mod.sendLang(sender, lang.adminDespawnSuccessTierWithIds
						.parse("count", cleaned)
						.parse("tier", filter.name())
						.parse("short_ids", String.join(", ", cleanedIds)));
				if (queued > 0) {
					mod.sendLang(sender, lang.adminDespawnQueuedCount
							.parse("count", queued)
							.parse("short_ids", String.join(", ", queuedIds)));
				}
				return;
			}
			default: {
				if (target.length() != 8) {
					mod.sendLang(sender, lang.adminShortIdInvalid);
					return;
				}
				ActiveBoss boss = mgr.findByShortId(target);
				if (boss == null) {
					mod.sendLang(sender, lang.adminBossNotFound.parse("value", target));
					return;
				}
				BossManager.DespawnOutcome outcome = mgr.dispatchDespawn(boss, requester);
				String shortId = BossManager.shortId(boss.pokemonUUID());
				if (outcome == BossManager.DespawnOutcome.QUEUED_BATTLE) {
					mod.sendLang(sender, lang.adminDespawnQueuedBattle.parse("short_id", shortId));
				} else {
					mod.sendLang(sender, lang.adminDespawnSuccess
							.parse("tier", boss.tier().name())
							.parse("species", boss.species())
							.parse("short_id", shortId));
				}
			}
		}
	}
}
