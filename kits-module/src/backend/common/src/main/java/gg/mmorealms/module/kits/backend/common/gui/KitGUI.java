package gg.mmorealms.module.kits.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.kits.backend.common.KitsBackendModule;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.exception.ClaimKitException;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;

import java.util.List;

public class KitGUI extends GUI {

	public KitGUI(User user) {
		super(user, new Settings().chestSize(6));
	}

	@Override
	public String getTitleString() {
		return KitsBackendModule.instance().getConfig().lang.kitGUITitle;
	}

	@Override
	public void setup() {
		setButton(KitsBackendModule.instance().getConfig().kitGUI.background);

		List<Kit> kits = KitUtils.getKits();

		for (Kit kit : kits) {
			if (kit.getSlot() == -1) {
				continue;
			}

			setButton(
					new GUIButton()
							.display(kit.getDisplayItem(), true)
							.displayName(kit.getName())
							.lore(kit.writeLore(getUser()))
							.position(kit.getSlot())
							.onClick((action) -> pressedKit(action, kit))
			);
		}
	}

	private void pressedKit(ClickType click, Kit kit) {
		switch (click) {
			case ClickType.MOUSE_LEFT -> giveItems(kit);
			case ClickType.MOUSE_RIGHT -> previewItems(kit);
		}
	}

	private void previewItems(Kit kit) {
		close();
		new PreviewKitGUI(getUser(), kit);
	}

	public void giveItems(Kit kit) {
		try {
			kit.claimKit(getUser(), false);
		} catch (ClaimKitException e) {
			user.sendMessage(e.getMessage());
		}
		refresh();
	}

}
