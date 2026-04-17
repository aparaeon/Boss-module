package com.raduvoinea.commandmanager.backend.neoforge;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class NeoForgeMiniMessageManager extends BackendMiniMessageManager{

	private final MinecraftServerAudiences audiences;

	public NeoForgeMiniMessageManager(MinecraftServer server) {
		this.audiences = MinecraftServerAudiences.of(server);
	}

	@SuppressWarnings("unused")
	public NeoForgeMiniMessageManager(MinecraftServerAudiences audiences) {
		this.audiences = audiences;
	}

	@Override
	public @NotNull Component toNative(@NotNull net.kyori.adventure.text.Component component) {
		return audiences.asNative(component);
	}

}