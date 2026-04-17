package gg.mmorealms.module.hunts.backend.common.gui;

import com.raduvoinea.utils.lambda.lambda.LambdaExecutor;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;

import java.util.Collections;
import java.util.Map;

public abstract class HuntButton {
    protected final GUIButton enabledButton;
    protected final GUIButton disabledButton;

    public HuntButton(GUIButton enabledButton, GUIButton disabledButton) {
        this.enabledButton = enabledButton;
        this.disabledButton = disabledButton;
    }

    protected abstract boolean isEnabled(Hunts hunts, HuntType type);

    protected abstract boolean handleClick(ClickType clickType, User user, Hunts hunts, HuntType type);

    protected GUIButton getButton(Hunts hunts, HuntType type) {
        return isEnabled(hunts, type)
                ? getEnabledButton(hunts, type)
                : getDisabledButton(hunts, type);
    }

    protected GUIButton getEnabledButton(Hunts hunts, HuntType type) {
        return enabledButton;
    }

    protected GUIButton getDisabledButton(Hunts hunts, HuntType type) {
        return disabledButton;
    }

    protected Map<String, Object> getPlaceholders(Hunts hunts, HuntType type) {
        return isEnabled(hunts, type)
                ? getEnabledPlaceholders(hunts, type)
                : getDisabledPlaceholders(hunts, type);
    }

    protected Map<String, Object> getEnabledPlaceholders(Hunts hunts, HuntType type) {
        return Collections.emptyMap();
    }

    protected Map<String, Object> getDisabledPlaceholders(Hunts hunts, HuntType type) {
        return Collections.emptyMap();
    }

    public GUIButton create(User user, HuntType type) {
        return create(user, type, () -> {});
    }

    public GUIButton create(Hunts hunts, User user, HuntType type) {
        return create(hunts, user, type, () -> {});
    }

    public GUIButton create(User user, HuntType type, LambdaExecutor postClick) {
        Hunts hunts = Hunts.get(user);
        return create(hunts, user, type, postClick);
    }

    public GUIButton create(Hunts hunts, User user, HuntType type, LambdaExecutor postClick) {
        boolean allowed = isEnabled(hunts, type);

        return getButton(hunts, type)
                .placeholders(getPlaceholders(hunts, type))
                .onClick(clickType -> {

                    if (clickType.isLeft && allowed
                            && handleClick(clickType, user, hunts, type)) {
                        postClick.execute();
                    }

                });
    }
}