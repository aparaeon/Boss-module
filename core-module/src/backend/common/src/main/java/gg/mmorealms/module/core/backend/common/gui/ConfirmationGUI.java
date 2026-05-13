package gg.mmorealms.module.core.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.world.item.Items;

public abstract class ConfirmationGUI extends GUI {

	public ConfirmationGUI(User user) {
		super(user, new GUISettings().chestSize(3));
	}

	protected abstract void onConfirm(ClickType click);

	protected void onCancel(ClickType click) {
		close();
	}

	@Override
	public String getTitleString() {
		return "Confirm";
	}

	@Override
	public void draw() {
		setButton(confirmButton());
		setButton(cancelButton());
	}

	public GUIButton confirmButton() {
		return GUIButton.of(Items.GREEN_STAINED_GLASS_PANE)
				.name("<bold><green>Confirm")
				.position(1, 2)
				.onClick(this::onConfirm);
	}

	public GUIButton cancelButton() {
		return GUIButton.of(Items.RED_STAINED_GLASS_PANE)
				.name("<bold><red>Cancel")
				.position(1, 6)
				.onClick(this::onCancel);
	}

}