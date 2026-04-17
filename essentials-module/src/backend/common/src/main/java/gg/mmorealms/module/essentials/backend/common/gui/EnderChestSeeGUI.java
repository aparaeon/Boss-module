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
import gg.mmorealms.module.essentials.backend.common.dto.SerializableEnderChest;
import gg.mmorealms.module.essentials.backend.common.dto.event.AddItemEvent;
import gg.mmorealms.module.essentials.backend.common.dto.event.SetItemEvent;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class EnderChestSeeGUI extends GUI {

	public static final String PLAYER_ENDER_CHEST_STREAM_TYPE = "player_ender_chest";

	private final UUID targetUUID;
	private final EssentialsConfig.EnderChestSeeGUI config;

	private int ticks = 0;
	private int offlineStreams = 0;

	public EnderChestSeeGUI(User user, UUID targetUUID) {
		super(
				user,
				new Settings()
						.chestSize(4)
						.manipulatePlayerSlots(true)
		);
		this.targetUUID = targetUUID;
		this.config = EssentialsBackendModule.instance().getConfig().enderChestSeeGUI;
	}

	@Override
	public String getTitleString() {
		IUser target = IUser.getByUUID(targetUUID);
		return new MessageBuilder("{target}'s Ender Chest") // TODO Config
				.parse("target", target.getUsername())
				.parse();
	}


	@Override
	public void setup() {
		setButton(config.backgroundItem);

		String streamData = StreamableResource.streamData(PLAYER_ENDER_CHEST_STREAM_TYPE + "#" + targetUUID);

		if (streamData == null) {
			setButton(config.temporaryOfflineStream);
			offlineStreams++;

			IUser target = IUser.getByUUID(targetUUID);
			new StreamStartRequest(target.getServerLocation().getServer(), PLAYER_ENDER_CHEST_STREAM_TYPE, targetUUID.toString()).sendAndGet();
			return;
		}

		setButton(config.onlineStream);

		SerializableEnderChest targetEnderChest = CommonLoader.instance().fromJson(streamData, SerializableEnderChest.class);
		setButtons(
				config.targetItem,
				targetEnderChest.getItems(),
				config.itemsSlot,
				(ClickType click, Integer index) -> removeItem(click, index, InventorySlot.Type.PLAYER_ENDER_CHEST)
		);

		setPlayerInventory(config.userItem, this::addItem);
	}

	public void removeItem(ClickType click, int slot, InventorySlot.Type type) {
		IUser target = IUser.getByUUID(targetUUID);
		new SetItemEvent(target.getServerLocation().getServer(), target.getUUID(), new InventorySlot(type, slot,
				CodecUtils.serialize(ItemStack.CODEC, ItemStack.EMPTY)
		)).send();
	}

	public void addItem(ClickType click, int slot) {
		IUser target = IUser.getByUUID(targetUUID);

		new AddItemEvent(
				target.getServerLocation().getServer(),
				target.getUUID(),
				CodecUtils.serialize(ItemStack.CODEC, user.getPlayer().getInventory().getItem(slot)),
				InventorySlot.Type.PLAYER_ENDER_CHEST
		).send();
	}

	@Override
	public void onTick() {
		if (offlineStreams >= config.offlineStreamThreshold) {
			setButton(config.offlineStream);
			return;
		}

		ticks++;

		if (ticks >= 4) {
			ticks = 0;
			refresh();
		}
	}

}
