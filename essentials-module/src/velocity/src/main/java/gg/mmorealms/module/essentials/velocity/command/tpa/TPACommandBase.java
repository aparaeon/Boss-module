package gg.mmorealms.module.essentials.velocity.command.tpa;

import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class TPACommandBase extends VelocityCommand {

	private final boolean reversed;

	public TPACommandBase(CommonCommandManager commandManager, boolean reversed) {
		super(commandManager);
		this.reversed = reversed;
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		List<String> output = new ArrayList<>(recommendPlayersList());

		output.add("accept");
		output.add("deny");

		return output;
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String targetUsername = arguments.getFirst();

		if (targetUsername.equals(player.getUsername())) {
			sendMessage(player, "You can't teleport to yourself.");
			return;
		}

		Player target = EssentialsVelocityModule.instance().getProxy().getPlayer(targetUsername).orElse(null);

		if (target == null) {
			sendMessage(player, "Target user not found.");
			return;
		}

		EssentialsVelocityModule.instance().getTeleportManager().create(player, target, reversed);
	}

}
