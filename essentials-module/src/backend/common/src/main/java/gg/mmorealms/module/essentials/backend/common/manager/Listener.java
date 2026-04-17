package gg.mmorealms.module.essentials.backend.common.manager;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerUseItemEvent;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.common.dto.StreamableResource;
import gg.mmorealms.loader.common.dto.event.impl.StreamStartRequest;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.exceptions.AccessedOfflineUserException;
import gg.mmorealms.module.core.common.dto.NetworkLocation;
import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import gg.mmorealms.module.essentials.backend.common.dto.SerializableEnderChest;
import gg.mmorealms.module.essentials.backend.common.dto.SerializablePlayerInventory;
import gg.mmorealms.module.essentials.backend.common.dto.event.AddItemEvent;
import gg.mmorealms.module.essentials.backend.common.dto.event.SetItemEvent;
import gg.mmorealms.module.essentials.backend.common.dto.event.UserJoinMessageEvent;
import gg.mmorealms.module.essentials.backend.common.gui.EnderChestSeeGUI;
import gg.mmorealms.module.essentials.backend.common.gui.InvSeeGUI;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;
import gg.mmorealms.module.essentials.common.dto.event.UserNetworkLocationRequest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class Listener {

	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject MinecraftServer server;
	private @Inject EssentialsConfig config;

	@EventHandler
	private void onUserJoinMessageEvent(UserJoinMessageEvent event) {
		Component parsedMessage = miniMessageManager.toNative(miniMessageManager.toComponent(
				config.lang.firstJoinMessage
						.parse("name", event.getName())
		));

		for (ServerPlayer player : EssentialsBackendModule.instance().getServer().getPlayerList().getPlayers()) {
			player.sendSystemMessage(parsedMessage);
		}
	}

	@EventHandler
	public void onUserNetworkLocationRequest(UserNetworkLocationRequest event) {
		IUser user = IUser.getByUUID(event.getUuid());

		event.setResult(new NetworkLocation(BackendLoader.instance().getServerID(), user.getLocation()));
	}

	@EventHandler
	public void onCommandExecuteEvent(CommandExecuteEvent event) {
		CommandSourceStack commandStack = server.createCommandSourceStack();

		if (event.getExecuteAs() != null) {
			ServerPlayer player = server.getPlayerList().getPlayerByName(event.getExecuteAs());

			if (player == null) {
				Logger.warn("Tried to execute command as a player that is not online: " + event.getExecuteAs());
				return;
			}

			commandStack = player.createCommandSourceStack();
		}

		Logger.debug(new MessageBuilder("Executing command {command} as {execute_as}")
				.parse("command", event.getCommand())
				.parse("execute_as", commandStack.getTextName())
				.parse()
		);
		server.getCommands().performPrefixedCommand(
				commandStack,
				event.getCommand()
		);
	}

	@EventHandler
	private void onPlayerUseItemEvent(PlayerUseItemEvent event) {
		ServerPlayer player = event.getPlayer();
		ItemStack stack = player.getMainHandItem();

		if (!(stack.getItem() instanceof CompassItem)) {
			return;
		}

		ItemStack compass = CodecUtils.deserialize(
				ItemStack.CODEC,
				config.selectCompass,
				CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY)
		);
		Component compassName = compass.get(DataComponents.CUSTOM_NAME);

		if (!stack.getComponents().getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString().equals(compassName.getString())
				|| !stack.getComponents().getOrDefault(DataComponents.MAX_STACK_SIZE, 0).equals(compass.get(DataComponents.MAX_STACK_SIZE))) {
			return;
		}

		EssentialsBackendModule.instance().getServer().getCommands().performPrefixedCommand(
				player.createCommandSourceStack(),
				"/select"
		);

		event.setResult(false);
	}

	@EventHandler
	public void onStreamStartRequest$playerInventory(StreamStartRequest event) {
		if (!event.getStreamType().equals(InvSeeGUI.PLAYER_INVENTORY_STREAM_TYPE)) {
			return;
		}

		UUID targetUUID = UUID.fromString(event.getStreamID());
		StreamableResource.create(
				event.getStreamChannel(),
				Time.milliseconds(200), // TODO Config
				Time.seconds(1),
				() -> {
					try {
						IUser targetPrimitive = IUser.getByUUID(targetUUID);

						if (!(targetPrimitive instanceof User target)) {
							Logger.warn("Attempted to stream inventory for a non-user target: " + targetUUID);
							return null;
						}

						SerializablePlayerInventory playerInventory = new SerializablePlayerInventory(
								target.getPlayer().getInventory().items.subList(9, 36),
								target.getPlayer().getInventory().items.subList(0, 9),
								target.getPlayer().getInventory().armor,
								target.getPlayer().getInventory().offhand
						);

						return EssentialsBackendModule.instance().toJson(playerInventory);
					} catch (AccessedOfflineUserException exception) {
						return null;
					}
				}
		);

		event.setResult(true);
	}

	@EventHandler
	public void onStreamStartRequest$playerEnderChest(StreamStartRequest event) {
		if (!event.getStreamType().equals(EnderChestSeeGUI.PLAYER_ENDER_CHEST_STREAM_TYPE)) {
			return;
		}

		UUID targetUUID = UUID.fromString(event.getStreamID());
		StreamableResource.create(
				event.getStreamChannel(),
				Time.milliseconds(200), // TODO Config
				Time.seconds(1),
				() -> {
					try {
						IUser targetPrimitive = IUser.getByUUID(targetUUID);

						if (!(targetPrimitive instanceof User target)) {
							Logger.warn("Attempted to stream inventory for a non-user target: " + targetUUID);
							return null;
						}

						SerializableEnderChest enderChest = new SerializableEnderChest(target.getPlayer().getEnderChestInventory().getItems());

						return EssentialsBackendModule.instance().toJson(enderChest);
					} catch (AccessedOfflineUserException exception) {
						return null;
					}
				}
		);

		event.setResult(true);
	}

	@EventHandler
	public void onSetItemEvent(SetItemEvent event) {
		ServerPlayer player = server.getPlayerList().getPlayer(event.getTargetUUID());

		if (player == null) {
			Logger.warn("Tried to set item on a player that is not online: " + event.getTargetUUID());
			return;
		}

		ItemStack itemStack = CodecUtils.deserialize(ItemStack.CODEC, event.getSlot().getItemJson(), CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));

		switch (event.getSlot().getType()) {
			case PLAYER_INVENTORY -> player.getInventory().items.set(event.getSlot().getSlot() + 9, itemStack);
			case PLAYER_HOTBAR -> player.getInventory().items.set(event.getSlot().getSlot(), itemStack);
			case PLAYER_ARMOUR -> player.getInventory().armor.set(3 - event.getSlot().getSlot(), itemStack);
			case PLAYER_OFFHAND -> player.getInventory().offhand.set(event.getSlot().getSlot(), itemStack);
			case PLAYER_ENDER_CHEST ->
					player.getEnderChestInventory().getItems().set(event.getSlot().getSlot(), itemStack);
		}
	}

	@EventHandler
	public void onAddItemEvent(AddItemEvent event) {
		ServerPlayer player = server.getPlayerList().getPlayer(event.getTargetUUID());

		if (player == null) {
			Logger.warn("Tried to set item on a player that is not online: " + event.getTargetUUID());
			return;
		}

		ItemStack itemStack = CodecUtils.deserialize(ItemStack.CODEC, event.getItemJson(), CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));

		switch (event.getInventoryType()) {
			case PLAYER_INVENTORY, PLAYER_HOTBAR -> player.getInventory().add(itemStack);
			case PLAYER_ARMOUR, PLAYER_OFFHAND ->
					player.getInventory().add(itemStack); // TODO Maybe add some custom logic here
			case PLAYER_ENDER_CHEST -> player.getEnderChestInventory().addItem(itemStack);
		}
	}
}
