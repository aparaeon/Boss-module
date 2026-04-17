package gg.mmorealms.module.hunts.backend.common.dto;

import lombok.Getter;

@Getter
public enum HuntType {
    BEGINNER,
    CHALLENGING,
    INSANE;

    private final String friendlyName;

    HuntType() {
        this.friendlyName = name().toLowerCase();
    }

    HuntType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
