package gg.mmorealms.module.essentials.backend.common.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.ICooldownCommand;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"repair"}, onlyFor = Command.OnlyFor.PLAYERS)
public class RepairCommand extends UserCommand implements ICooldownCommand {

	@Getter
	private final String COMMAND_COOLDOWN_KEY = "REPAIR_COOLDOWN";

	private @Inject EssentialsConfig essentialsConfig;
	private @Inject CoreConfig coreConfig;

	public RepairCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		if (isCommandOnCooldown(user)) {
			return;
		}

		ItemStack itemStack = user.getPlayer().getMainHandItem();
		if (itemStack.isEmpty()) {
			user.sendMessage("You must hold an item in your main hand to repair it!"); // TODO Config
			return;
		}

		if (!itemStack.isDamaged()) {
			user.sendMessage("Your item is not damaged and does not need repairing!"); // TODO Config
			return;
		}

		if (!itemStack.isDamageableItem()) {
			user.sendMessage("Your item cannot be repaired!"); // TODO Config
			return;
		}

		itemStack.setDamageValue(0);

		setCommandCooldown(user, essentialsConfig.repairCooldown);
		user.sendMessage("Your item has been repaired!"); // TODO Config
	}

}
