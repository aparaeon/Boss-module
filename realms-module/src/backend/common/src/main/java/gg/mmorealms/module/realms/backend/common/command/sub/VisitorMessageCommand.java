package gg.mmorealms.module.realms.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"visitor_message"}, onlyFor = Command.OnlyFor.PLAYERS, parent = RealmCommand.class)
public class VisitorMessageCommand extends UserCommand {
	private @Inject RealmsConfig config;


	public VisitorMessageCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm currentRealm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_CHANGE_VISITOR_MESSAGE);
		if (currentRealm == null) {
			return;
		}

		giveBook(user.getPlayer(), currentRealm);
	}

	private void giveBook(ServerPlayer player, IRealm currentRealm) {
		boolean found = false;

		ItemStack item = CodecUtils.deserialize(ItemStack.CODEC, config.visitorMessageItem, CodecUtils.CodecErrorProcessor.ofNull());

		for (ItemStack stack : player.getInventory().items) {
			if (stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString()
					.equals(item.getComponents().get(DataComponents.CUSTOM_NAME).getString())
					&& stack.getOrDefault(DataComponents.MAX_STACK_SIZE, 0) == 99) {
				found = true;
				break;
			}
		}

		if (found) {
			return;
		}

		item.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(
				List.of(Filterable.passThrough(
						currentRealm.getSettings().getVisitorMessage())
				)
		));

		player.getInventory().add(item);
	}
}
