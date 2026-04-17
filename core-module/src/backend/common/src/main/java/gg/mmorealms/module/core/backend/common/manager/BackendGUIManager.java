package gg.mmorealms.module.core.backend.common.manager;

import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;

public abstract class BackendGUIManager {

	public abstract void openGUI(GUI userGUI);

	public abstract void closeGUI(User user);

}
