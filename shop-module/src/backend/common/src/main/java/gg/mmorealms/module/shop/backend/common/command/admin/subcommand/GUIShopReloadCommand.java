package gg.mmorealms.module.shop.backend.common.command.admin.subcommand;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.command.admin.GUIShopAdminBaseCommand;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"reload"}, parent = GUIShopAdminBaseCommand.class)
public class GUIShopReloadCommand extends BackendCommand {
	public GUIShopReloadCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		ShopBackendModule.instance().config(ShopBackendModule.instance().getFileManager().load(ShopConfig.class));
		ShopBackendModule.instance().manager().reloadShopsNames();

		sendMessage(sender, ShopBackendModule.instance().config().lang.messageReloadSuccess);
	}
}
