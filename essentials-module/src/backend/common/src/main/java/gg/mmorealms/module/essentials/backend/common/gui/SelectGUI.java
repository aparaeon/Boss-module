package gg.mmorealms.module.essentials.backend.common.gui;

import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;

public class SelectGUI extends GUI {
	private final EssentialsConfig config = EssentialsBackendModule.instance().getConfig();

	public SelectGUI(User user) {
		super(user, new Settings().chestSize(6));
		open();
	}

	@Override
	public String getTitleString() {
		return "\uF812\uF205";
	}

	@Override
	public void setup() {
		setButton(config.selectGUI.teleportSpawn)
				.onClick(this::teleportSpawn);

		setButton(config.selectGUI.teleportRealm)
				.onClick(this::teleportRealm);

		setButton(config.selectGUI.teleportWild)
				.onClick(this::teleportWild);
	}

	public void teleportSpawn(ClickType click) {
		user.send(IServerLocation.of(ServerType.SPAWN));
	}

	public void teleportRealm(ClickType click) {
		EssentialsBackendModule.instance().getServer().getCommands().performPrefixedCommand(
				user.getPlayer().createCommandSourceStack(),
				"/realm tp"
		);
	}

	public void teleportWild(ClickType click) {
		EssentialsBackendModule.instance().getServer().getCommands().performPrefixedCommand(
			user.getPlayer().createCommandSourceStack(),
			"/rtp"
		);
	}
}
