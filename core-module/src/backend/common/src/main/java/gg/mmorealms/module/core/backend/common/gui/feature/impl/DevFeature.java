package gg.mmorealms.module.core.backend.common.gui.feature.impl;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.gui.feature.IGUIFeature;
import net.minecraft.world.item.Items;

public class DevFeature implements IGUIFeature {

	private final GUISettings.DevSettings settings;
	private int x = 0;
	private int y = 0;

	public DevFeature(GUISettings.DevSettings settings) {
		this.settings = settings;
	}

	@Override
	public String transformTitle(String title) {
		char yChar = (char) ('\uF300' + y);
		char xChar = (char) ('\uF801' + x);
		return "" + xChar + yChar;
	}

	@Override
	public void draw(GUI gui) {
		if (settings.placeAllSlots()) {
			for (int i = 0; i < 54; i++) {
				gui.setButton(GUIButton.of(Items.GLASS_PANE).name(""), i).onClick(click -> {
				});
			}
		}

		gui.setButton(GUIButton.of(Items.BARRIER).name("<"), 0).onClick(() -> {
			x++;
			gui.initDraw();
		});
		gui.setButton(GUIButton.of(Items.BARRIER).name(">"), 1).onClick(() -> {
			x--;
			gui.initDraw();
		});
		gui.setButton(GUIButton.of(Items.BARRIER).name("v"), 2).onClick(() -> {
			y--;
			gui.initDraw();
		});
		gui.setButton(GUIButton.of(Items.BARRIER).name("^"), 3).onClick(() -> {
			y++;
			gui.initDraw();
		});

		char xChar = (char) ('\uF801' + x);
		gui.getUser().sendMessage(
			new MessageBuilder("X: {x} Y: {y}")
				.parse("x", toHex(xChar))
				.parse("y", y)
		);
	}

	private String toHex(int n) {
		return "\\u" + Integer.toHexString(n).replace("0x", "");
	}
}