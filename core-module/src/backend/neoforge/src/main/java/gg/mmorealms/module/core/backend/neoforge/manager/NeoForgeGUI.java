package gg.mmorealms.module.core.backend.neoforge.manager;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.neoforge.CoreNeoForgeModule;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.item.ItemStack;

public class NeoForgeGUI extends SimpleGui {

	private final GUI gui;

	public NeoForgeGUI(GUI gui) {
		super(gui.getSettings().getMenuType(), gui.getUser().getPlayer(), gui.getSettings().manipulatePlayerSlots());
		this.gui = gui;
		this.gui.subscribe(this::refresh);
	}

	@Override
	public Component getTitle() {
		return CoreNeoForgeModule.instance().getMiniMessageManager().parse(gui.getTitle());
	}

	public void updateTitle() {
		if (!this.isOpen()) {
			return;
		}

		Component title = getTitle();

		if (title == null) {
			title = Component.empty();
		}

		this.player.connection.send(new ClientboundOpenScreenPacket(this.syncId, this.type, title));
		this.screenHandler.sendAllDataToRemote();
	}

	@Override
	public void beforeOpen() {
		gui.beforeOpen();
		draw();
	}

	private void draw() {
		for (int slot = 0; slot < Math.min(gui.getButtons().length, this.getSize()); slot++) {
			int finalSlot = slot;
			setSlot(slot, new GuiElementInterface() {
				@Override
				public ItemStack getItemStack() {
					if (gui.getButtons()[finalSlot] == null) {
						return ItemStack.EMPTY;
					}

					return gui.getButtons()[finalSlot].toItemStack();
				}

				@Override
				public ClickCallback getGuiCallback() {
					return (ItemClickCallback) (index, clickType, minecraftClickType) ->
						gui.getButtons()[finalSlot]
							.getOnClick()
							.run(convertClickType(clickType));
				}
			});
		}
	}

	@Override
	public void onClose() {
		gui.onClose();
	}

	private ClickType convertClickType(eu.pb4.sgui.api.ClickType clickType) {
		return switch (clickType) {
			case eu.pb4.sgui.api.ClickType.MOUSE_LEFT -> ClickType.MOUSE_LEFT;
			case eu.pb4.sgui.api.ClickType.MOUSE_RIGHT -> ClickType.MOUSE_RIGHT;
			case eu.pb4.sgui.api.ClickType.MOUSE_LEFT_SHIFT -> ClickType.MOUSE_LEFT_SHIFT;
			case eu.pb4.sgui.api.ClickType.MOUSE_RIGHT_SHIFT -> ClickType.MOUSE_RIGHT_SHIFT;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_1 -> ClickType.NUM_KEY_1;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_2 -> ClickType.NUM_KEY_2;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_3 -> ClickType.NUM_KEY_3;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_4 -> ClickType.NUM_KEY_4;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_5 -> ClickType.NUM_KEY_5;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_6 -> ClickType.NUM_KEY_6;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_7 -> ClickType.NUM_KEY_7;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_8 -> ClickType.NUM_KEY_8;
			case eu.pb4.sgui.api.ClickType.NUM_KEY_9 -> ClickType.NUM_KEY_9;
			case eu.pb4.sgui.api.ClickType.MOUSE_MIDDLE -> ClickType.MOUSE_MIDDLE;
			case eu.pb4.sgui.api.ClickType.DROP -> ClickType.DROP;
			case eu.pb4.sgui.api.ClickType.CTRL_DROP -> ClickType.CTRL_DROP;
			case eu.pb4.sgui.api.ClickType.MOUSE_LEFT_OUTSIDE -> ClickType.MOUSE_LEFT_OUTSIDE;
			case eu.pb4.sgui.api.ClickType.MOUSE_RIGHT_OUTSIDE -> ClickType.MOUSE_RIGHT_OUTSIDE;
			case eu.pb4.sgui.api.ClickType.MOUSE_LEFT_DRAG_START -> ClickType.MOUSE_LEFT_DRAG_START;
			case eu.pb4.sgui.api.ClickType.MOUSE_RIGHT_DRAG_START -> ClickType.MOUSE_RIGHT_DRAG_START;
			case eu.pb4.sgui.api.ClickType.MOUSE_MIDDLE_DRAG_START -> ClickType.MOUSE_MIDDLE_DRAG_START;
			case eu.pb4.sgui.api.ClickType.MOUSE_LEFT_DRAG_ADD -> ClickType.MOUSE_LEFT_DRAG_ADD;
			case eu.pb4.sgui.api.ClickType.MOUSE_RIGHT_DRAG_ADD -> ClickType.MOUSE_RIGHT_DRAG_ADD;
			case eu.pb4.sgui.api.ClickType.MOUSE_MIDDLE_DRAG_ADD -> ClickType.MOUSE_MIDDLE_DRAG_ADD;
			case eu.pb4.sgui.api.ClickType.MOUSE_LEFT_DRAG_END -> ClickType.MOUSE_LEFT_DRAG_END;
			case eu.pb4.sgui.api.ClickType.MOUSE_RIGHT_DRAG_END -> ClickType.MOUSE_RIGHT_DRAG_END;
			case eu.pb4.sgui.api.ClickType.MOUSE_MIDDLE_DRAG_END -> ClickType.MOUSE_MIDDLE_DRAG_END;
			case eu.pb4.sgui.api.ClickType.MOUSE_DOUBLE_CLICK -> ClickType.MOUSE_DOUBLE_CLICK;
			case eu.pb4.sgui.api.ClickType.UNKNOWN -> ClickType.UNKNOWN;
			case eu.pb4.sgui.api.ClickType.OFFHAND_SWAP -> ClickType.OFFHAND_SWAP;
		};
	}

	@Override
	public void onTick() {
		gui.onTick();
	}

	private void refresh() {
		if (!this.isOpen()) {
			return;
		}

		updateTitle();
	}
}
