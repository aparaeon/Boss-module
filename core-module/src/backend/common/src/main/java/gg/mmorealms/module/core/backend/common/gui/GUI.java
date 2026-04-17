package gg.mmorealms.module.core.backend.common.gui;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgsLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class GUI {

	private final Settings settings;
	@SuppressWarnings("MismatchedReadAndWriteOfArray")
	private final GUIButton[] buttons = new GUIButton[100];
	private boolean closed = false;
	private Lambda onRefresh = () -> {

	};

	protected final User user;

	public GUI(User user, Settings settings) {
		this.user = user;
		this.settings = settings;
	}

	public final String getTitle() {
		return "<white>" + getTitleString();
	}

	public boolean isIncludingPlayer() {
		return this.settings.manipulatePlayerSlots;
	}

	public void beforeOpen() {
		setup();
	}

	protected void refresh() {
		Arrays.fill(buttons, null); // Clear all buttons

		setup();
		onRefresh.run();
	}

	public abstract void setup();

	public abstract String getTitleString();

	public void onTick() {
	}

	public void onClose() {

	}

	// -------------------- Button Actions --------------------

	protected void nop(ClickType clickType) {

	}

	protected void closeOnClick(ClickType clickType) {
		close();
	}

	protected void close() {
		CoreBackendModule.instance().getGuiManager().closeGUI(this.user);
		this.closed = true;
	}

	public void open(ClickType clickType) {
		open();
	}

	protected void refreshOnClick(ClickType clickType) {
		refresh();
	}

	protected final void underDevelopment(ClickType clickType) {
		user.sendMessage(CoreBackendModule.instance().getConfig().lang.underDevelopment);
	}

	protected final void error(ClickType clickType) {
		user.sendMessage(CoreBackendModule.instance().getConfig().lang.guiError);
	}

	// -------------------- Button Management --------------------

	protected GUIButton setButton() {
		return setButton(GUIButton.empty());
	}

	protected GUIButton setButton(int slot) {
		return setButton(GUIButton.empty(), slot);
	}

	protected GUIButton setButton(@NotNull GUIButton base) {
		GUIButton button = base.clone().markAsPlacedInGUI();

		for (Integer slot : button.getPosition().slots()) {
			if (slot < 0 || slot >= buttons.length) {
				Logger.error("Tried to set a button at an invalid slot: " + slot + " in GUI: " + this.getClass().getSimpleName());
				return button;
			}

			buttons[slot] = button;
		}

		return button;
	}

	protected GUIButton setButton(@NotNull GUIButton base, int slot) {
		GUIButton button = base.clone().markAsPlacedInGUI();

		buttons[slot] = button;

		return button;
	}

	protected void setPlayerInventory(GUIButton template, ArgsLambda<ClickType, Integer> executor) {
		if (!settings.manipulatePlayerSlots) {
			Logger.warn("Attempted to manipulate player slots when that setting is disabled by " + this.getClass());
			return;
		}

		ServerPlayer userPlayer = user.getPlayer();
		for (int index = 9; index < 36; index++) {
			ItemStack itemStack = userPlayer.getInventory().getItem(index);

			int finalIndex = index;

			setButton(template.clone()
				.display(itemStack, false)
				.position(54 + index - 9)
				.onClick(click ->
					executor.run(click, finalIndex)
				)
			);
		}

		for (int index = 0; index < 9; index++) {
			ItemStack itemStack = userPlayer.getInventory().getItem(index);

			int finalIndex = index;

			setButton(
				template.clone()
					.display(itemStack, false)
					.position(54 + 27 + index)
					.onClick(click ->
						executor.run(click, finalIndex)
					)
			);
		}
	}

	protected void setButtons(List<GUIButton> buttons) {
		for (GUIButton button : buttons) {
			setButton(button);
		}
	}

	protected void setButtons(GUIButton template, List<ItemStack> itemStacks, List<Integer> slots, ArgsLambda<ClickType, Integer> executor) {
		for (int index = 0; index < Math.min(itemStacks.size(), slots.size()); index++) {
			ItemStack itemStack = itemStacks.get(index);
			int slot = slots.get(index);

			int finalIndex = index;

			setButton(
				template.clone()
					.position(slot)
					.display(itemStack, false)
					.onClick(click -> executor.run(click, finalIndex))
			);
		}
	}

	// -------------------- Utility Methods --------------------

	protected void playSound(SoundEvent sound, float volume, float pitch) {
		ServerPlayer player = user.getPlayer();

		//noinspection resource
		player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.MASTER, player.getX(), player.getY(), player.getZ(), volume, pitch, player.level().getRandom().nextLong()));
	}

	public void open(boolean force) {
		if (force) {
			this.closed = false;
		}

		if (this.closed) {
			return;
		}

		CoreBackendModule.instance().getGuiManager().openGUI(this);
	}

	public void open() {
		open(false);
	}

	public void subscribe(Lambda onRefresh) {
		this.onRefresh = onRefresh;
	}

	// -------------------- Classes --------------------

	@Setter
	@Accessors(fluent = true, chain = true)
	@Getter
	public static class Settings {
		private boolean manipulatePlayerSlots = false;
		private boolean wrapPage = false;
		private String menuTypeKey;

		public Settings() {
			ResourceLocation menyTypeResourceLocation = BuiltInRegistries.MENU.getKey(MenuType.GENERIC_9x1);

			if (menyTypeResourceLocation == null) {
				throw new RuntimeException("Failed to get the default menu type from BuiltInRegistries.MENU (MenuType.GENERIC_9x1).");
			}

			this.menuTypeKey = menyTypeResourceLocation.toString();
		}

		public Settings chestSize(int chestSize) {
			MenuType<ChestMenu> menuType = getChestMenuType(chestSize);

			if (menuType == null) {
				Logger.error("Failed to get the menu type for chest size: " + chestSize + ". Using default MenuType.GENERIC_9x1 instead.");
				return this;
			}

			ResourceLocation menyTypeResourceLocation = BuiltInRegistries.MENU.getKey(menuType);

			if (menyTypeResourceLocation == null) {
				Logger.error("Failed to get the menu type for chest size: " + chestSize + ". Using default MenuType.GENERIC_9x1 instead.");
				return this;
			}

			this.menuTypeKey = menyTypeResourceLocation.toString();
			return this;
		}

		private MenuType<ChestMenu> getChestMenuType(int size) {
			return switch (size) {
				case 1 -> MenuType.GENERIC_9x1;
				case 2 -> MenuType.GENERIC_9x2;
				case 3 -> MenuType.GENERIC_9x3;
				case 4 -> MenuType.GENERIC_9x4;
				case 5 -> MenuType.GENERIC_9x5;
				case 6 -> MenuType.GENERIC_9x6;
				default -> null;
			};
		}

		public MenuType<?> getMenuType() {
			MenuType<?> menuType = BuiltInRegistries.MENU.get(ResourceLocation.parse(this.menuTypeKey));

			if (menuType == null) {
				Logger.error("Failed to get the menu type for key: " + this.menuTypeKey + ". Using default MenuType.GENERIC_9x1 instead.");
				return MenuType.GENERIC_9x1;
			}

			return menuType;
		}

	}
}
