package gg.mmorealms.module.economy.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.ConfirmationGUI;
import gg.mmorealms.module.economy.common.dto.Price;

public abstract class PriceConfirmationGUI extends ConfirmationGUI {

	private final Price price;

	public PriceConfirmationGUI(User user, Price price) {
		super(user);
		this.price = price;
	}

	@Override
	public GUIButton confirmButton() {
		return super.confirmButton()
			.lore(
				"",
				"<white><b>Price: " + this.price.toString()
			);
	}
}
