package gg.mmorealms.module.core.backend.fabric.manager;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.gui.GuiInterface;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.manager.BackendGUIManager;

public class FabricGUIManager extends BackendGUIManager {

	@Override
	public void openGUI(GUI gui) {
		new FabricGUI(gui).open();
	}

	@Override
	public void closeGUI(User user) {
		GuiInterface currentGUI = GuiHelpers.getCurrentGui(user.getPlayer());

		if (currentGUI == null) {
			return;
		}

		currentGUI.close();
	}
}
