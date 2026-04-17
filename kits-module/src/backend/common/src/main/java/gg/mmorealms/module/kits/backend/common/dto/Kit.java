package gg.mmorealms.module.kits.backend.common.dto;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.common.dto.event.impl.RemoteMethodExecuteRequest;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import gg.mmorealms.module.kits.backend.common.KitsBackendModule;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.exception.ClaimKitException;
import gg.mmorealms.module.kits.backend.common.utils.KitUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Kit {
	private String name;

	private Time cooldown;

	private List<JsonElement> items = new ArrayList<>();
	private List<String> commands = new ArrayList<>();

	private JsonElement displayItem;
	private List<String> lore = new ArrayList<>();
	private List<String> previewLore = new ArrayList<>();

	private int slot;

	private transient Codec<ItemStack> codec = ItemStack.CODEC;

	public Kit(String name, int slot, Time cooldown, List<ItemStack> items, ItemStack displayItem) {
		this.name = name;
		this.slot = slot;
		this.cooldown = cooldown;
		this.items.addAll(CodecUtils.serialize(codec, items));

		if (displayItem != null && !displayItem.isEmpty()) {
			displayItem.setCount(1);
			this.displayItem = CodecUtils.serialize(codec, displayItem);
			return;
		}

		if (!items.isEmpty()) {
			ItemStack firstItem = items.getFirst();
			firstItem.setCount(1);
			this.displayItem = CodecUtils.serialize(codec, firstItem);
		}
	}

	public int getSize() {
		return items.size();
	}

	public List<ItemStack> getItems() {
		return CodecUtils.deserialize(codec, items, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	public ItemStack getDisplayItem() {
		return CodecUtils.deserialize(codec, displayItem, CodecUtils.CodecErrorProcessor.of(() -> ItemStack.EMPTY));
	}

	public void addCommand(String command) {
		commands.add(command);
		KitUtils.saveToFile();
	}

	public String removeCommand(int index) {
		if (index < 0 || index > commands.size()) {
			return "Index out of Bounds";
		}

		String toReturn = commands.remove(index);
		KitUtils.saveToFile();
		return toReturn;
	}

	public void addLore(String lore) {
		this.lore.add(lore + "<reset>");
		KitUtils.saveToFile();
	}

	public String removeLore(int index) {
		if (index < 0 || index > lore.size()) {
			return "Index out of Bounds";
		}

		String toReturn = lore.remove(index);
		KitUtils.saveToFile();
		return toReturn;
	}

	public boolean checkPermission(IUser user) {
		User user1 = (User) user;
		KitsConfig config = KitsBackendModule.instance().getConfig();

		return LuckPermsUtils.checkPermission(
				ServerPlayer.class,
				user1.getPlayer(),
				config.baseKitPermission
						.parse("name", this.getName())
						.parse()
		);
	}

	public void claimKit(IUser userPrimitive, Boolean force) throws ClaimKitException {
		if (!(userPrimitive instanceof User user)) {
			if (!userPrimitive.isOnlineOnNetwork()) {
				return;
			}

			new RemoteMethodExecuteRequest<>(
					userPrimitive.getServerLocation().getServer(), Kit.class.getName(),
					"claimKit", List.of(UUID.class, Boolean.class),
					this.name, userPrimitive.getUUID(), force
			).sendAndGet();
			return;
		}

		KitsConfig config = KitsBackendModule.instance().getConfig();

		if (!force && !checkPermission(user)) {
			throw new ClaimKitException(config.lang.noPermissionKit);
		}

		String kitPermission = config.baseKitPermission.parse("name", this.getName()).parse();
		IBackendCooldowns cooldown = IBackendCooldowns.getByUser(user);
		cooldown.get(kitPermission);
		if (cooldown.get(kitPermission) == Long.MAX_VALUE) {
			throw new ClaimKitException(config.lang.redeemedOneTimeKit);
		}

		long remainingTime = cooldown.getRemaining(kitPermission);
		if (remainingTime > 0) {
			throw new ClaimKitException(config.lang.cooldownMessage
					.parse("time", Time.milliseconds(remainingTime).toString())
					.parse("name", this.getName()));
		}

		int freeSlots = InventoryUtils.getFreeSlots(user);
		if (freeSlots < items.size()) {
			throw new ClaimKitException(config.lang.noSpaceKit.parse("space", items.size()), true);
		}

		Logger.info(new MessageBuilder("{user} ({uuid}) claimed kit {kit}")
				.parse("user", user.getUsername())
				.parse("uuid", user.getUUID().toString())
				.parse("kit", this.getName())
		);

		List<ItemStack> items = this.getItems();
		for (ItemStack item : items) {
			user.getPlayer().addItem(item);
		}

		MinecraftServer server = KitsBackendModule.instance().getServer();

		CommandSourceStack commandSourceStack = server.createCommandSourceStack();
		for (String command : this.getCommands()) {
			server.getCommands().performPrefixedCommand(commandSourceStack,
					new MessageBuilder(command)
							.parse("user", user.getUsername())
							.parse("uuid", user.getUUID())
							.parse()
			);
		}

		if (this.getCooldown().toMilliseconds() == 0) {
			cooldown.set(kitPermission, -1);
			throw new ClaimKitException(config.lang.receivedMessage.parse("name", this.getName()));
		}

		cooldown.set(kitPermission, this.getCooldown());
		throw new ClaimKitException(config.lang.receivedMessage.parse("name", this.getName()));
	}

	public void claimKit(UUID userUUID, Boolean force) throws ClaimKitException {
		claimKit(IUser.getByUUID(userUUID), force);
	}

	public List<String> writeLore(User user) {
		KitsConfig config = KitsBackendModule.instance().getConfig();

		List<String> displayLore = new ArrayList<>();
		displayLore.add("");
		if (this.lore != null && !this.lore.isEmpty()) {
			displayLore.addAll(this.lore);
			displayLore.add("");
		}
		String kitPermission = config.baseKitPermission.parse("name", this.getName()).parse();
		IBackendCooldowns cooldown = IBackendCooldowns.getByUser(user);

		// One time kit
		if (this.getCooldown().toMilliseconds() == 0) {
			// Redeemed kit
			if (cooldown.get(kitPermission) == Long.MAX_VALUE) {
				displayLore.add(config.lang.redeemedOneTimeKit);
			} else {
				displayLore.add(config.lang.oneTimeKit);
				if (!checkPermission(user)) {
					displayLore.add(config.lang.noPermissionKit);
				}
			}

			return displayLore;
		}

		long remainingTime = cooldown.getRemaining(kitPermission);
		if (remainingTime <= 0) {
			displayLore.add(config.lang.offCooldown
					.parse("time", this.getCooldown().toString())
					.parse()
			);
			displayLore.add(config.lang.redeemableKit);
		} else {
			displayLore.add(config.lang.onCooldown
					.parse("time", Time.milliseconds(remainingTime).toString())
					.parse()
			);
			displayLore.add(config.lang.onCooldownKit
					.parse("time", Time.milliseconds(remainingTime).toString())
					.parse()
			);
		}

		if (!this.checkPermission(user)) {
			displayLore.add(config.lang.noPermissionKit);
		}

		return displayLore;
	}

	private String index(List<String> list) {
		KitsConfig config = KitsBackendModule.instance().getConfig();

		StringBuilder indexedList = new StringBuilder();

		int index = 0;
		for (String line : list) {
			indexedList.append(config.lang.indexedList
					.parse("index", index)
					.parse("field", line)
					.parse());
			index++;
		}

		return indexedList.toString();
	}

	public String toDetailedString() {
		return KitsBackendModule.instance().getConfig().lang.detailedDescription
				.parse("name", name)
				.parse("cooldown", cooldown)
				.parse("commands", index(commands))
				.parse("lore", index(lore))
				.parse();
	}
}
