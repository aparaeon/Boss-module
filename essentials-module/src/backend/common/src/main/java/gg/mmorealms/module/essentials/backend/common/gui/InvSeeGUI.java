package gg.mmorealms.module.essentials.backend.common.gui;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.StreamableResource;
import gg.mmorealms.loader.common.dto.event.impl.StreamStartRequest;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import gg.mmorealms.module.essentials.backend.common.config.EssentialsConfig;
import gg.mmorealms.module.essentials.backend.common.dto.InventorySlot;
import gg.mmorealms.module.essentials.backend.common.dto.SerializablePlayerInventory;
import gg.mmorealms.module.essentials.backend.common.dto.event.AddItemEvent;
import gg.mmorealms.module.essentials.backend.common.dto.event.SetItemEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class InvSeeGUI extends GUI {

	public static final String PLAYER_INVENTORY_STREAM_TYPE = "player_inventory";

	private final UUID targetUUID;
	private final EssentialsConfig.InvSeeGUI config;

	private int ticks = 0;
	private int offlineStreams = 0;

	public InvSeeGUI(User user, UUID targetUUID) {
		super(
				user,
				new Settings()
						.chestSize(6)
						.manipulatePlayerSlots(true)
		);
		this.targetUUID = targetUUID;
		this.config = EssentialsBackendModule.instance().getConfig().invSeeGUI;
	}

	@Override
	public String getTitleString() {
		IUser target = IUser.getByUUID(targetUUID);
		return new MessageBuilder("{target}'s Inventory") // TODO Config
				.parse("target", target.getUsername())
				.parse();
	}

	public void setup() {
		setButton(config.background);

		String streamData = StreamableResource.streamData(PLAYER_INVENTORY_STREAM_TYPE + "#" + targetUUID);

		if (streamData == null) {
			setButton(config.temporaryOfflineStream);
			offlineStreams++;

			IUser target = IUser.getByUUID(targetUUID);
			new StreamStartRequest(target.getServerLocation().getServer(), PLAYER_INVENTORY_STREAM_TYPE, targetUUID.toString()).sendAndGet();
			return;
		}

		setButton(config.onlineStream);

		SerializablePlayerInventory targetInventory = CommonLoader.instance().fromJson(streamData, SerializablePlayerInventory.class);

		setTargetSlots(targetInventory.getArmor(), config.armorSlots, InventorySlot.Type.PLAYER_ARMOUR);
		setTargetSlots(targetInventory.getItems(), config.inventorySlots, InventorySlot.Type.PLAYER_INVENTORY);
		setTargetSlots(targetInventory.getHotbar(), config.hotbarSlots, InventorySlot.Type.PLAYER_HOTBAR);
		setTargetSlots(targetInventory.getOffhand(), config.offHandSlots, InventorySlot.Type.PLAYER_OFFHAND);

		ServerPlayer userPlayer = user.getPlayer();
		for (int index = 9; index < 36; index++) {
			ItemStack itemStack = userPlayer.getInventory().getItem(index);
			setButton(config.userItem, 54 + index - 9)
					.display(itemStack)
					.onClick(click -> this.addItem(click, itemStack));
		}

		for (int index = 0; index < 9; index++) {
			ItemStack itemStack = userPlayer.getInventory().getItem(index);
			setButton(config.userItem,
					54 + 27 + index)
					.display(itemStack)
					.onClick(click -> this.addItem(click, itemStack));
		}
	}

	private void setTargetSlots(List<ItemStack> itemStacks, List<Integer> slots, InventorySlot.Type slotType) {
		for (int index = 0; index < itemStacks.size(); index++) {
			ItemStack itemStack = itemStacks.get(index);
			int slot = slots.get(index);

			int finalIndex = index;
			setButton(config.targetItem, slot)
					.display(itemStack)
					.onClick(click -> this.removeItem(click, slotType, finalIndex));
		}
	}

	public void removeItem(ClickType click, InventorySlot.Type slotType, int slot) {
		IUser target = IUser.getByUUID(targetUUID);
		new SetItemEvent(target.getServerLocation().getServer(), target.getUUID(), new InventorySlot(slotType, slot,
				CodecUtils.serialize(ItemStack.CODEC, ItemStack.EMPTY)
		)).send();
	}

	public void addItem(ClickType click, ItemStack itemStack) {
		IUser target = IUser.getByUUID(targetUUID);
		new AddItemEvent(
				target.getServerLocation().getServer(),
				target.getUUID(),
				CodecUtils.serialize(ItemStack.CODEC, itemStack),
				InventorySlot.Type.PLAYER_INVENTORY
		).send();
	}

	@Override
	public void onTick() {
		if (offlineStreams >= config.offlineStreamThreshold) {
			setButton(config.offlineStream, config.statusIndex);
			return;
		}

		ticks++;

		if (ticks >= 4) {
			ticks = 0;
			refresh();
		}
	}

}
