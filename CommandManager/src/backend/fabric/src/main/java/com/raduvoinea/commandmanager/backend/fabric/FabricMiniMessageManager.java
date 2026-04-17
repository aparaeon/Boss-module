package com.raduvoinea.commandmanager.backend.fabric;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.commandmanager.common.manager.CommonMiniMessageManager;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;


public class FabricMiniMessageManager extends BackendMiniMessageManager{

	private final FabricAudiences audiences;

	public FabricMiniMessageManager(MinecraftServer server) {
		this.audiences = FabricServerAudiences.of(server);
	}

	@SuppressWarnings("unused")
	public FabricMiniMessageManager(FabricAudiences audiences) {
		this.audiences = audiences;
	}

	@Override
	public @NotNull Component toNative(@NotNull net.kyori.adventure.text.Component component) {
		return audiences.toNative(component);
	}

}