package gg.mmorealms.module.boss.backend.fabric.command;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.manager.ActiveBoss;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@Command(aliases = {"list", "ls"}, parent = BossAdminCommand.class)
public class BossListCommand extends BackendCommand {

	public BossListCommand(CommonCommandManager commandManager) {
		super(commandManager);
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

		Collection<ActiveBoss> active = mgr.getAllActive();
		if (active.isEmpty()) {
			mod.sendLang(sender, lang.adminListEmpty);
			return;
		}

		mod.sendLang(sender, lang.adminListHeader.parse("count", active.size()));
		for (ActiveBoss boss : active) {
			PokemonEntity entity = mod.findEntity(boss.entityUUID());
			String location;
			if (entity == null || entity.isRemoved()) {
				location = lang.adminListLocationUnloaded.parse();
			} else {
				location = lang.adminListLocation
						.parse("dimension", entity.level().dimension().location().getPath())
						.parse("x", (int) entity.getX())
						.parse("y", (int) entity.getY())
						.parse("z", (int) entity.getZ())
						.parse();
			}
			mod.sendLang(sender, lang.adminListRow
					.parse("short_id", BossManager.shortId(boss.pokemonUUID()))
					.parse("tier", boss.tier().name())
					.parse("species", boss.species())
					.parse("level", boss.level())
					.parse("location", location));
		}
	}
}
