package com.raduvoinea.commandmanager.velocity.manager;

import com.raduvoinea.commandmanager.common.manager.CommonMiniMessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public class VelocityMiniMessageManager extends CommonMiniMessageManager<Component> {

	@Override
	public @NotNull Component toNative(@NotNull Component component) {
		return component;
	}

}
