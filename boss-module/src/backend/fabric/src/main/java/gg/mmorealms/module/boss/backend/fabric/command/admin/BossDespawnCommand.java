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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
				// Snapshot to avoid mutating the active map while iterating.
				for (ActiveBoss boss : new ArrayList<>(mgr.getAllActive())) {
					if (mgr.dispatchDespawn(boss) == BossManager.DespawnOutcome.CLEANED) cleaned++;
					else queued++;
				}
				mod.sendLang(sender, lang.adminDespawnSuccessAll.parse("count", cleaned));
				if (queued > 0) {
					mod.sendLang(sender, lang.adminDespawnQueuedBattle.parse("short_id", queued + " in-battle"));
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
				for (ActiveBoss boss : new ArrayList<>(mgr.getAllActive())) {
					if (boss.tier() != filter) continue;
					if (mgr.dispatchDespawn(boss) == BossManager.DespawnOutcome.CLEANED) cleaned++;
					else queued++;
				}
				mod.sendLang(sender, lang.adminDespawnSuccessTier
						.parse("count", cleaned)
						.parse("tier", filter.name()));
				if (queued > 0) {
					mod.sendLang(sender, lang.adminDespawnQueuedBattle.parse("short_id", queued + " in-battle"));
				}
				return;
			}
			default: {
				// Short-ID lookup — must be exactly 8 hex chars (BossManager rejects shorter/longer).
				if (target.length() != 8) {
					mod.sendLang(sender, lang.adminShortIdInvalid);
					return;
				}
				ActiveBoss boss = mgr.findByShortId(target);
				if (boss == null) {
					mod.sendLang(sender, lang.adminBossNotFound.parse("value", target));
					return;
				}
				BossManager.DespawnOutcome outcome = mgr.dispatchDespawn(boss);
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
