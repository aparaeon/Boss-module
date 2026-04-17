package gg.mmorealms.module.hunts.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;

public class HuntBackground extends HuntButton {

    public HuntBackground(GUIButton enabledButton, GUIButton disabledButton) {
        super(enabledButton, disabledButton);
    }

    @Override
    protected boolean isEnabled(Hunts hunts, HuntType type) {
        if (hunts.isHuntOnCooldown(type)) {
            return false;
        }

        if (!hunts.hasActiveHunt()) {
            return true;
        }

        return hunts.isActiveHunt(type);
    }

    @Override
    protected boolean handleClick(ClickType clickType, User user, Hunts hunts, HuntType type) {
        return false;
    }

}
