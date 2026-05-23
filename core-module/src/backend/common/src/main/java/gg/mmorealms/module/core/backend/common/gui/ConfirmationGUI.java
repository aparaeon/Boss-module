package gg.mmorealms.module.core.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.world.item.Items;

public abstract class ConfirmationGUI extends GUI {

	private enum State {
		UNKNOWN, CONFIRMED, CANCELED
	}

	private State state = State.UNKNOWN;

	public ConfirmationGUI(User user) {
		super(user, new GUISettings().chestSize(3));
	}

	protected abstract void onConfirm();

	protected abstract void onCancel();

	@Override
	public String getTitleString() {
		return "Confirm";
	}

	@Override
	public void draw() {
		setButton(confirmButton());
		setButton(cancelButton());
	}

	public GUIButton confirmButton() {
		return GUIButton.of(Items.GREEN_STAINED_GLASS_PANE)
			.name("<bold><green>Confirm")
			.position(1, 2)
			.onClick(this::handleConfirmButton);
	}

	public GUIButton cancelButton() {
		return GUIButton.of(Items.RED_STAINED_GLASS_PANE)
			.name("<bold><red>Cancel")
			.position(1, 6)
			.onClick(this::handleCancelButton);
	}

	private void handleConfirmButton() {
		if (this.state != State.UNKNOWN) {
			return;
		}

		this.state = State.CONFIRMED;
		this.onConfirm();
	}

	private void handleCancelButton() {
		if (this.state != State.UNKNOWN) {
			return;
		}

		this.state = State.CANCELED;
		this.onCancel();
		this.close();
	}

	@Override
	public void onClose() {
		if (this.state != State.UNKNOWN) {
			return;
		}

		this.state = State.CANCELED;
		this.onCancel();
	}


}