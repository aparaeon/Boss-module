package gg.mmorealms.module.pokedex_rewards.backend.common.gui;

import com.raduvoinea.utils.lambda.lambda.ArgLambdaExecutor;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.pokedex_rewards.backend.common.PokedexRewardsBackendModule;
import gg.mmorealms.module.pokedex_rewards.backend.common.dto.PokedexRewardsData;
import gg.mmorealms.module.pokedex_rewards.backend.common.dto.enums.RewardStatus;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokedex;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class PokedexMilestone {

	private final GUIButton displayItem;
	private final double milestone;
	private final List<String> commands;
	private final int requiredFreeSpace;

	public PokedexMilestone(GUIButton displayItem, double milestone, List<String> commands, int requiredFreeSpace) {
		this.displayItem = displayItem;
		this.milestone = milestone;
		this.commands = commands;
		this.requiredFreeSpace = requiredFreeSpace;
	}

	private void handleClick(ClickType clickType, IUser user) {
		PokedexRewardsData rewardsData = PokedexRewardsData.get(user);

		if (rewardsData.getStatus(this.milestone) != RewardStatus.AVAILABLE) {
			return;
		}

		if (user instanceof User localUser) {
			award(localUser);
		}
	}

	private void award(User user) {
		if (InventoryUtils.getFreeSlots(user) < requiredFreeSpace) {
			user.sendMessage("<red>You don't have enough free space to claim this milestone!.");
			return;
		}

		MinecraftServer server = PokedexRewardsBackendModule.instance().getServer();
		int permissionLevel = 4;

		CommandSourceStack source = server
				.createCommandSourceStack()
				.withSuppressedOutput()
				.withPermission(permissionLevel);

		for (String command : commands) {
			String parsed = new MessageBuilder(command)
					.parse("user", user.getUsername())
					.parse();

			server.getCommands().performPrefixedCommand(source, parsed);
		}

		PokedexRewardsData data = PokedexRewardsData.get(user);
		data.markClaimed(milestone);
	}

	public GUIButton toGUIButton(User user, ArgLambdaExecutor<ClickType> postClick) {
		PokedexRewardsData data = PokedexRewardsData.get(user);
		RewardStatus status = data.getStatus(milestone);

		IPokedex pokedex = IPokedex.get(user.getPlayer());
		int max = (int) (pokedex.getMaxSize() * milestone);

		if (max == 0) {
			Logger.error("Unknown milestone: " + milestone);
		}

		int caught = pokedex.getSize();
		boolean achieved = caught >= max;

		String countString = (achieved ? "<green>" + max : "<red>" + caught) + "<gray>"; // TODO Config
		String maxString = (max == 0 ? "<red>ERROR" : (achieved ? "<green>" : "<aqua>") + max); // TODO Config

		double percentage = NumberUtils.getPercentage(caught, max);

		if (percentage > 100) {
			percentage = 100.0;
		}

		return this.displayItem
				.clone()
				.onClick(click -> {
					handleClick(click, user);
					postClick.execute(click);
				})
				.placeholder("user", user)
				.placeholder("status", status.getDisplayName())
				.placeholder("count", countString)
				.placeholder("max", maxString)
				.placeholder("percentage", NumberUtils.formatNumberWithDecimalPlaces(percentage, 2));
	}
}
