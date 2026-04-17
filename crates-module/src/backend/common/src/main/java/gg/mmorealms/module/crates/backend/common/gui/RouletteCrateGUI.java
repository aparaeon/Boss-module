package gg.mmorealms.module.crates.backend.common.gui;

import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.exceptions.PermissionException;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import gg.mmorealms.module.crates.backend.common.config.CratesConfig;
import gg.mmorealms.module.crates.backend.common.dto.Crate;
import gg.mmorealms.module.crates.backend.common.dto.CrateItem;

import java.util.ArrayList;
import java.util.List;

public class RouletteCrateGUI extends GUI {

	private static final MessageBuilder OPEN_CRATE_PERMISSION = new MessageBuilder("mmorealms.crate.open.{id}");
	private static final CratesConfig BASE_CONFIG = CratesBackendModule.instance().getConfig();
	private static final CratesConfig.CrateGUI GUI_CONFIG = BASE_CONFIG.crateGUI;

	private final Crate crate;
	private final List<CrateItem> items = new ArrayList<>();

	private int totalTicks;
	private int ticksSinceUpdate;
	private boolean rewarded = false;

	public RouletteCrateGUI(User user, Crate crate) throws PermissionException {
		super(user, GUI_CONFIG.settings);

		if (!user.hasPermission(
				OPEN_CRATE_PERMISSION
						.parse("id", crate.id)
						.parse()
		)) {
			throw new PermissionException("You do not have permission to open this crate."); // TODO Config
		}

		this.crate = crate;
		Logger.log(new MessageBuilder("User {user} opened crate {crate}")
				.parse("user", user.getUsername())
				.parse("crate", crate.name)
				.parse()
		);
	}

	private void update() {
		if (this.items.size() >= GUI_CONFIG.lootPositions.size()) {
			this.items.removeLast();
		}
		this.items.addFirst(RandomUtils.getRandom(this.crate.crateItems));

		for (int i = 0; i < Math.min(GUI_CONFIG.lootPositions.size(), this.items.size()); i++) {
			int position = GUI_CONFIG.lootPositions.get(i);
			CrateItem item = items.get(i);

			setButton(item.getDisplayItem(), position);
		}
	}

	private void reward() {
		rewarded = true;
		CrateItem crateItem = this.items.get(3);
		reward(crateItem);
	}

	@Override
	public void onTick() {
		if (rewarded) {
			update();
			return;
		}

		totalTicks++;
		ticksSinceUpdate++;

		if (totalTicks >= GUI_CONFIG.maxTicks) {
			reward();
			return;
		}

		for (Pair2<Range, Integer> pair : GUI_CONFIG.animationDelays) {
			Range range = pair.first();
			Integer delay = pair.second();

			if (range.contains(totalTicks) && ticksSinceUpdate >= delay) {
				update();
				ticksSinceUpdate = 0;
				return;
			}
		}
	}

	@Override
	public String getTitleString() {
		return GUI_CONFIG.title
				.parse("name", crate.name)
				.parse();
	}

	@Override
	public void setup() {
		setButton(GUI_CONFIG.background);
		setButton(GUI_CONFIG.indicator);
	}

	@Override
	public void onClose() {
		if (rewarded) {
			return;
		}

		CrateItem reward = RandomUtils.getRandomWeighed(this.crate.crateItems);
		reward(reward);
	}

	private void reward(CrateItem reward) {
		List<String> commands = reward.getRewardCommands()
				.parse("user", this.user.getUsername())
				.parse();

		for (String command : commands) {
			CratesBackendModule.instance().getServer().getCommands().performPrefixedCommand(
					CratesBackendModule.instance().getServer().createCommandSourceStack(),
					command
			);
		}

		user.sendMessage(new MessageBuilder("You have received {name}") // TODO Config
				.parse("name", reward.getDisplayItem().getDisplayName())
		);
	}
}
