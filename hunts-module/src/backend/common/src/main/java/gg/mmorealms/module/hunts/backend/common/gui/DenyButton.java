package gg.mmorealms.module.hunts.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;

public class DenyButton extends HuntButton {

    public DenyButton(GUIButton enabledButton, GUIButton disabledButton) {
        super(enabledButton, disabledButton);
    }

    @Override
    protected boolean isEnabled(Hunts hunts, HuntType type) {
        return hunts.canDenyHunt(type);
    }

    @Override
    protected boolean handleClick(ClickType clickType, User user, Hunts hunts, HuntType type) {
        hunts.denyHunt(type);
        return true;
    }

}
