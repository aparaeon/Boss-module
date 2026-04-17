package gg.mmorealms.module.core.backend.common.gui;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.world.item.Items;

public abstract class DevGUI extends GUI {

	private int devX = 0;
	private int devY = 0;
	private boolean placeAllSlots;

	public DevGUI(User user, Settings settings, boolean placeAllSlots) {
		super(user, settings);
		this.placeAllSlots = placeAllSlots;
	}

	private String toHex(int n) {
		return "\\u" + Integer.toHexString(n).replace("0x", "");
	}

	@Override
	public String getTitleString() {
		char yOffset = '\uF300';
		char xOffset = '\uF801';

		char y = (char) (yOffset + this.devY);
		char x = (char) (xOffset + this.devX);

		return "" + x + y;
	}

	@Override
	protected void refresh() {
		char yOffset = '\uF300';
		char xOffset = '\uF801';

		char y = (char) (yOffset + this.devY);
		char x = (char) (xOffset + this.devX);

		getUser().sendMessage(
				new MessageBuilder("X: {x} Y: {y}")
						.parse("x", toHex(x))
						.parse("y", this.devY)
		);

		setup();
//		open();
		super.refresh();
	}

	@Override
	public void setup() {
		if (placeAllSlots) {
			for (int i = 0; i < 54; i++) {
				setButton(
						new GUIButton()
								.display(Items.GLASS_PANE)
								.displayName(""),
						i
				).onClick(this::nop);
			}
		}

		setButton(
				new GUIButton()
						.display(Items.BARRIER)
						.displayName("<"),
				0
		).onClick(() -> {
			this.devX++;
			refresh();
		});

		setButton(
				new GUIButton()
						.display(Items.BARRIER)
						.displayName(">"),
				1
		).onClick(() -> {
			this.devX--;
			refresh();
		});

		setButton(
				new GUIButton()
						.display(Items.BARRIER)
						.displayName("v"),
				2
		).onClick(() -> {
			this.devY--;
			refresh();
		});

		setButton(
				new GUIButton()
						.display(Items.BARRIER)
						.displayName("^"),
				3
		).onClick(() -> {
			this.devY++;
			refresh();
		});
	}

}
