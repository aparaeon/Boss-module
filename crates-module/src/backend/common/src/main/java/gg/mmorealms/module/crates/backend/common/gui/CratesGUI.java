package gg.mmorealms.module.crates.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.exceptions.PermissionException;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import gg.mmorealms.module.crates.backend.common.config.CratesConfig;
import gg.mmorealms.module.crates.backend.common.database.ICrateKeys;
import gg.mmorealms.module.crates.backend.common.dto.Crate;

public class CratesGUI extends GUI {

	private static final CratesConfig CONFIG = CratesBackendModule.instance().getConfig();

	public CratesGUI(User user) {
		super(user, new Settings().chestSize(6));

		for (Crate crate : CONFIG.crates) {
			crate.bake();
		}
	}

	@Override
	public String getTitleString() {
		return CONFIG.cratesGUI.title;
	}

	@Override
	public void setup() {
		ICrateKeys crateKeys = ICrateKeys.getByUser(this.user);

		for (Crate crate : CONFIG.crates) {
			int keys = crateKeys.getKeys(crate.getId());

			setButton(crate.getDisplayItem())
					.onClick((click) -> previewCrate(click, crate))
					.placeholder("keys", keys)
					.placeholder("price", CONFIG.getCratePriceMessage(keys, crate.price)
							.parse("price", NumberUtils.formatNumberWithCommas(crate.getPrice().amount()))
							.parse("keys", keys)
					);
		}
	}

	private void previewCrate(ClickType click, Crate crate) {
		try {
			if (InventoryUtils.hasFullInventory(user)) {
				user.sendMessage(CONFIG.fullInventory);
				this.close();
				return;
			}

			new PreviewCrateGUI(getUser(), crate).open();
		} catch (PermissionException exception) {
			user.sendMessage(exception.getMessage());
		}
	}

}
