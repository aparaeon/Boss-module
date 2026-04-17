package gg.mmorealms.module.shop.backend.common.command.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.command.GuiShopBaseCommand;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"admin"}, parent = GuiShopBaseCommand.class)
public class GUIShopAdminBaseCommand extends BackendCommand {
	private final static String HELP_MESSAGE = """
			<yellow>
			/guishop admin addhelditem <shopName> <buyPrice> <sellPrice>
			/guishop admin additem <shopName> <itemId> <buyPrice> <sellPrice>
			/guishop admin removeitem <shopName> <itemName>
			/guishop admin create <shopName>
			/guishop admin delete <shopName>
			/guishop forcesave
			/guishop reload""";

	public GUIShopAdminBaseCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	public static void saveToFile() {
		ShopBackendModule.instance().getFileManager().save(ShopBackendModule.instance().config());
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		sendMessage(sender, HELP_MESSAGE);
	}
}
