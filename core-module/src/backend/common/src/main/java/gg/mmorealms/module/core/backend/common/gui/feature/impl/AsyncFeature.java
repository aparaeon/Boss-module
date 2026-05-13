package gg.mmorealms.module.core.backend.common.gui.feature.impl;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.backend.common.gui.feature.IGUIFeature;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class AsyncFeature implements IGUIFeature {

	private GUIButton[] snapshot = new GUIButton[100];
	private CompletableFuture<Void> task = CompletableFuture.completedFuture(null);

	@Override
	public synchronized boolean draw(GUI gui, Iterator<IGUIFeature> remainingFeatures, boolean sendUpdate) {
		task = task.thenRunAsync(() -> {
			while(remainingFeatures.hasNext()) {
				IGUIFeature next = remainingFeatures.next();
				next.draw(gui, remainingFeatures, false);
			}

			snapshot = gui.getLiveButtons().clone();

			gui.sendUpdate();
		});

		return true;
	}

	@Override
	public GUIButton[] transformButtons(GUIButton[] buttons) {
		return snapshot;
	}
}