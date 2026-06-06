package gg.mmorealms.module.boss.backend.fabric.command;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.manager.ActiveBoss;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager;
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

		if (arguments.isEmpty()) {
			mod.sendLang(sender, lang.adminUsageDespawn);
			return;
		}

		String target = arguments.get(0).toLowerCase();

		switch (target) {
			case "all": {
				int count = mgr.handleDespawnRequest(null, null, true, true);
				mod.sendLang(sender, lang.adminDespawnSuccessAll.parse("count", count));
				return;
			}
			case "tier": {
				if (arguments.size() < 2) {
					mod.sendLang(sender, lang.adminUsageDespawnTier);
					return;
				}
				BossTier filter;
				try {
					filter = BossTier.valueOf(arguments.get(1).toUpperCase());
				} catch (IllegalArgumentException e) {
					mod.sendLang(sender, lang.adminTierUnknown.parse("tier", arguments.get(1)));
					return;
				}
				int count = mgr.handleDespawnRequest(null, filter, false, true);
				mod.sendLang(sender, lang.adminDespawnSuccessTier
						.parse("count", count)
						.parse("tier", filter.name()));
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
				mgr.handleDespawnRequest(boss.pokemonUUID(), null, false, true);
				mod.sendLang(sender, lang.adminDespawnSuccess
						.parse("tier", boss.tier().name())
						.parse("species", boss.species())
						.parse("short_id", BossManager.shortId(boss.pokemonUUID())));
			}
		}
	}
}
