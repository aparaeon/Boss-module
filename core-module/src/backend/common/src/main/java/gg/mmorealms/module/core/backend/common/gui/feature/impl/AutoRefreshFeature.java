package gg.mmorealms.module.core.backend.common.gui.feature.impl;

import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.gui.feature.IGUIFeature;
import gg.mmorealms.module.core.backend.common.gui.feature.interfaces.IAutoRefreshGUI;

public class AutoRefreshFeature implements IGUIFeature {

	private final GUISettings.AutoRefreshSettings settings;
	private int currentTick = 0;

	public AutoRefreshFeature(GUISettings.AutoRefreshSettings settings) {
		this.settings = settings;
	}

	@Override
	public void onTick(GUI gui) {
		if (currentTick < settings.tickInterval()) {
			currentTick++;
			return;
		}

		IAutoRefreshGUI autoRefreshGUI = (IAutoRefreshGUI) gui;

		if (!autoRefreshGUI.shouldAutoRefresh()) {
			return;
		}

		currentTick = 0;
		gui.initDraw();
	}
}