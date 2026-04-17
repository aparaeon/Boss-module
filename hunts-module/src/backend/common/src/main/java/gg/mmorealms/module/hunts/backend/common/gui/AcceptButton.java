package gg.mmorealms.module.hunts.backend.common.gui;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.dto.HuntData;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;

public class AcceptButton extends HuntButton {

    public AcceptButton(GUIButton enabledButton, GUIButton disabledButton) {
        super(enabledButton, disabledButton);
    }

    @Override
    protected boolean isEnabled(Hunts hunts, HuntType type) {
        return hunts.canAcceptHunt(type);
    }

    @Override
    protected boolean handleClick(ClickType clickType, User user, Hunts hunts, HuntType type) {
        hunts.acceptHunt(type);

        HuntsConfig config = HuntsBackendModule.instance().getConfig();
        HuntData huntData = hunts.getActiveHuntData();
        if (huntData == null) {
            return true;
        }

        String speciesName = huntData.speciesName();

        MessageBuilder message = config.lang
                .huntAccepted
                .parse("target", speciesName);
        user.sendMessage(message);

        return true;
    }
}