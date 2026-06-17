package gg.mmorealms.module.boss.backend.fabric.command.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.command.BossCommand;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
@Command(aliases = {"admin"}, parent = BossCommand.class)
public class BossAdminCommand extends BackendCommand {

	public BossAdminCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		BossFabricModule mod = BossFabricModule.instance();
		if (mod == null || mod.getConfig() == null) return;
		BossConfig.Lang lang = mod.getConfig().lang;
		mod.sendLang(sender, lang.adminUsageRoot);
	}
}
