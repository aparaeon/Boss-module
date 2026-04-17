package gg.mmorealms.module.kits.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.kits.backend.common.KitsBackendModule;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import gg.mmorealms.module.kits.backend.common.exception.ClaimKitException;
import net.minecraft.world.item.ItemStack;

public class PreviewKitGUI extends GUI {
	private final static int[] SLOTS = {
			0, 1, 2, 3, 4, 5, 6, 7, 8,
			9, 10, 11, 12, 13, 14, 15, 16, 17,
			18, 19, 20, 21, 22, 23, 24, 25, 26,
			27, 28, 29, 30, 31, 32, 33, 34, 35
	};

	private final Kit kit;
	private final KitsConfig config;

	public PreviewKitGUI(User user, Kit kit) {
		super(user, new Settings().chestSize(5));
		this.kit = kit;
		this.config = KitsBackendModule.instance().getConfig();

		open();
	}

	@Override
	public String getTitleString() {
		return kit.getName();
	}

	@Override
	public void setup() {
		setButton(config.previewGUI.background);

		KitsConfig config = KitsBackendModule.instance().getConfig();
		setButton(
				config.previewGUI.back
						.onClick(this::close)
		);

		setButton(
				config.previewGUI.claim
						.lore(kit.writeLore(this.user))
						.onClick((click) -> claimKit(click, kit))
		);

		if (!kit.getPreviewLore().isEmpty()) {
			setButton(
					config.previewGUI.lore
							.lore(kit.getPreviewLore())
			);
		}

		int slotsIndex = 0;
		for (ItemStack item : kit.getItems()) {
			String name = item.getDisplayName().getString();
			name = name.substring(1, name.length() - 1);
			setButton(
					new GUIButton()
							.display(item)
							.displayName(name)
							.position(SLOTS[slotsIndex])
			);

			slotsIndex++;
		}
	}

	private void claimKit(ClickType click, Kit kit) {
		try {
			kit.claimKit(getUser(), false);
		} catch (ClaimKitException e) {
			user.sendMessage(e.getMessage());
		}
		this.refresh();
	}

	@Override
	public void onClose() {
		new KitGUI(getUser()).open();
	}
}
