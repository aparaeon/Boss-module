package gg.mmorealms.module.core.backend.common.gui.feature.impl;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.GUISettings;
import gg.mmorealms.module.core.backend.common.gui.feature.IGUIFeature;
import gg.mmorealms.module.core.backend.common.gui.feature.interfaces.IPagedGUI;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

public class PagedFeature implements IGUIFeature {

	private final GUISettings.PagedSettings settings;

	@Getter
	@Setter
	private int page = 0;

	public PagedFeature(GUISettings.PagedSettings settings) {
		this.settings = settings;
	}

	@Override
	public void draw(GUI gui) {
		IPagedGUI pagedGUI = (IPagedGUI) gui;
		GUIButton backPageButton = settings.backPageButton();
		GUIButton nextPageButton = settings.nextPageButton();

		if (backPageButton != null) {
			gui.setButton(backPageButton)
				.onClick(pagedGUI::backPage);
		}

		if (nextPageButton != null) {
			gui.setButton(nextPageButton)
				.onClick(pagedGUI::nextPage);
		}
	}
}