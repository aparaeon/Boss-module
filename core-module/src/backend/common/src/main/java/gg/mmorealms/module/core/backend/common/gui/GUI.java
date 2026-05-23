package gg.mmorealms.module.core.backend.common.gui;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgsLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.feature.IGUIFeature;
import gg.mmorealms.module.core.backend.common.gui.feature.impl.AsyncFeature;
import gg.mmorealms.module.core.backend.common.gui.feature.impl.AutoRefreshFeature;
import gg.mmorealms.module.core.backend.common.gui.feature.impl.DevFeature;
import gg.mmorealms.module.core.backend.common.gui.feature.impl.PagedFeature;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Getter
public abstract class GUI implements IGUI {

	protected final User user;

	private final GUISettings settings;
	private final GUIButton[] buttons = new GUIButton[100];

	private boolean closed = false;
	private Lambda updateCallback = () -> {
	};

	private final List<IGUIFeature> features = new ArrayList<>();
	private final PagedFeature pagedFeature; // kept as to be able to be referenced from IPagedGUI

	public GUI(User user, GUISettings settings) {
		this.user = user;
		this.settings = settings;

		if (settings.async().enabled()) {
			features.add(new AsyncFeature());
		}
		if (settings.autoRefresh().enabled()) {
			features.add(new AutoRefreshFeature(settings.autoRefresh()));
		}
		if (settings.dev().enabled()) {
			features.add(new DevFeature(settings.dev()));
		}
		if (settings.paged().enabled()) {
			this.pagedFeature = new PagedFeature(settings.paged());
			features.add(pagedFeature);
		} else {
			this.pagedFeature = null;
		}

		this.features.add(new OriginalFeature());
	}

	public final String getTitle() {
		String title = getTitleString();
		for (IGUIFeature feature : features) {
			title = feature.transformTitle(title);
		}
		return "<white>" + title;
	}

	public abstract String getTitleString();

	public final void refresh() {
		this.initDraw();
	}

	public final void initDraw() {
		Arrays.fill(this.buttons, null);

		Iterator<IGUIFeature> featuresIterator = features.iterator();

		while (featuresIterator.hasNext()) {
			IGUIFeature feature = featuresIterator.next();
			if (feature.draw(this, featuresIterator, !featuresIterator.hasNext())) {
				break;
			}
		}
	}

	public void onTick() {
		for (IGUIFeature feature : features) {
			feature.onTick(this);
		}
	}

	public void onClose() {
	}

	protected abstract void draw();

	// -------------------- Button snapshot --------------------

	public GUIButton[] getButtons() {
		GUIButton[] result = buttons;

		for (IGUIFeature feature : features) {
			result = feature.transformButtons(result);
		}

		return result;
	}

	/**
	 * This will return the buttons in their non-transformed state -
	 * These buttons have not been touched by any feature
	 */
	public GUIButton[] getLiveButtons() {
		return buttons;
	}

	// -------------------- Button actions --------------------

	protected void nop(ClickType clickType) {
	}

	protected void closeOnClick(ClickType clickType) {
		close();
	}

	protected void close() {
		if (this.closed) {
			return;
		}

		this.closed = true;
		CoreBackendModule.instance().getGuiManager().closeGUI(this.user);
	}

	public void open(ClickType clickType) {
		open();
	}

	public void refresh(ClickType clickType) {
		this.refresh();
	}

	protected final void underDevelopment(ClickType clickType) {
		this.user.sendMessage(CoreBackendModule.instance().getConfig().lang.underDevelopment);
	}

	protected final void error(ClickType clickType) {
		this.user.sendMessage(CoreBackendModule.instance().getConfig().lang.guiError);
	}

	// -------------------- Button management --------------------

	public GUIButton setButton() {
		return setButton(GUIButton.empty());
	}

	public GUIButton setButton(int slot) {
		return setButton(GUIButton.empty(), slot);
	}

	public GUIButton setButton(@NotNull GUIButton base) {
		GUIButton button = base.copy().markAsPlacedInGUI();

		for (Integer slot : button.getPosition().slots()) {
			if (slot < 0 || slot >= buttons.length) {
				Logger.error("Tried to set a button at an invalid slot: " + slot + " in GUI: " + getClass().getSimpleName());
				return button;
			}
			buttons[slot] = button;
		}

		return button;
	}

	public GUIButton setButton(@Nullable GUIButton base, int slot) {
		if (base == null) {
			buttons[slot] = null;
			return null;
		}

		GUIButton button = base.copy().markAsPlacedInGUI();
		buttons[slot] = button;
		return button;
	}

	public GUIButton setButton(@NotNull ItemStack base, int slot) {
		GUIButton button = GUIButton.of(base).position(slot);
		buttons[slot] = button;
		return button;
	}

	public void setPlayerInventory(GUIButton template, ArgsLambda<ClickType, Integer> executor, List<ItemStack> items) {
		if (!settings.manipulatePlayerSlots()) {
			Logger.warn("Attempted to manipulate player slots when that setting is disabled by " + getClass());
			return;
		}

		for (int index = 9; index < 36; index++) {
			ItemStack itemStack = items.get(index);
			int slot = 54 + index - 9;

			if (itemStack == null) {
				setButton((GUIButton) null, slot);
				continue;
			}

			int finalIndex = index;
			setButton(template.copy().display(itemStack).position(slot)
				.onClick(click -> executor.run(click, finalIndex)));
		}

		for (int index = 0; index < 9; index++) {
			ItemStack itemStack = items.get(index);
			int slot = 54 + 27 + index;

			if (itemStack == null) {
				setButton((GUIButton) null, slot);
				continue;
			}

			int finalIndex = index;
			setButton(template.copy().display(itemStack).position(slot)
				.onClick(click -> executor.run(click, finalIndex)));
		}
	}

	public void setPlayerInventory(GUIButton template, ArgsLambda<ClickType, Integer> executor) {
		setPlayerInventory(template, executor, this.user.getPlayer().getInventory().items);
	}

	public void setButtons(List<GUIButton> buttons) {
		for (GUIButton button : buttons) {
			setButton(button);
		}
	}

	public void setButtons(GUIButton template, List<ItemStack> itemStacks, List<Integer> slots,
	                       ArgsLambda<ClickType, Integer> executor) {
		for (int index = 0; index < Math.min(itemStacks.size(), slots.size()); index++) {
			int finalIndex = index;
			setButton(template.copy()
				.position(slots.get(index))
				.display(itemStacks.get(index))
				.onClick(click -> executor.run(click, finalIndex)));
		}
	}

	// -------------------- Utility --------------------

	protected void playSound(SoundEvent sound, float volume, float pitch) {
		ServerPlayer player = user.getPlayer();
		//noinspection resource
		player.connection.send(new ClientboundSoundPacket(
			Holder.direct(sound), SoundSource.MASTER,
			player.getX(), player.getY(), player.getZ(),
			volume, pitch, player.level().getRandom().nextLong()
		));
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

	public void registerUpdateCallback(Lambda updateCallback) {
		this.updateCallback = updateCallback;
	}

	public static class OriginalFeature implements IGUIFeature {
		@Override
		public boolean draw(GUI gui, Iterator<IGUIFeature> remainingFeatures, boolean sendUpdate) {
			gui.draw();

			if (sendUpdate) {
				gui.sendUpdate();
			}

			return false;
		}
	}

	@Override
	public void sendUpdate() {
		this.updateCallback.run();
	}
}