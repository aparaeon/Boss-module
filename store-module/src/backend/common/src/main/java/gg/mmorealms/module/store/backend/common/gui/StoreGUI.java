package gg.mmorealms.module.store.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import gg.mmorealms.module.store.backend.common.files.StoreConfig;

public class StoreGUI extends GUI {

	private static final StoreConfig CONFIG = StoreBackendModule.instance().getConfig();

	public StoreGUI(User user) {
		super(
				user,
				new Settings()
						.chestSize(6)
		);
	}

	@Override
	public String getTitleString() {
		return "\uF80A\uF230";
	}

	@Override
	public void setup() {
		setButton(GUIButton.empty().position(0, 0, 5, 3)) // ranks
				.displayName("Ranks")
				.onClick((__) -> new RanksGUI(user, user).open());
		setButton(GUIButton.empty().position(0, 5, 4, 3)) // pokemon eggs
				.displayName("Pokemon Eggs")
				.onClick(this::underDevelopment);
		setButton(GUIButton.empty().position(3, 0, 4, 3)) // crate keys
				.displayName("Crate Keys")
				.onClick((__) -> new KeysGUI(user, user).open());
		setButton(GUIButton.empty().position(3, 4, 3, 2)) // currency
				.displayName("Currency")
				.onClick(this::underDevelopment);
		setButton(GUIButton.empty().position(5, 4, 3, 1)) // items
				.displayName("Items")
				.onClick(this::underDevelopment);
		setButton(GUIButton.empty().position(3, 7, 2, 3)) // gems
				.displayName("Gems")
				.onClick(this::showStore);
	}

	public void showStore(ClickType click) {
		this.user.sendMessage(CONFIG.lang.storeMessage);
	}
}
