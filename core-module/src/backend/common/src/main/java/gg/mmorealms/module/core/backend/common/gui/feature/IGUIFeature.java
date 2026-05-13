package gg.mmorealms.module.core.backend.common.gui.feature;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;

import java.util.Iterator;

public interface IGUIFeature {

	/**
	 * @param sendUpdate Should be passed as true just to the last gui feature
	 * @return Whether to cancel the remaining features (and not draw them) - this will be true when the feature will handle the drawing
	 * itself (e.g. AsyncFeature) and thus the remaining features should not draw themselves as they will be drawn by the
	 * feature itself when it is ready
	 */
	default boolean draw(GUI gui, Iterator<IGUIFeature> remainingFeaturesIterator, boolean sendUpdate) {
		draw(gui);
		return false;
	}

	default void draw(GUI gui) {

	}

	default void onTick(GUI gui) {
	}

	default String transformTitle(String title) {
		return title;
	}

	default GUIButton[] transformButtons(GUIButton[] buttons) {
		return buttons;
	}
}