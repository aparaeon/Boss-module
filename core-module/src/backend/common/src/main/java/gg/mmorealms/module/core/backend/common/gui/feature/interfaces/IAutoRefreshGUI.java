package gg.mmorealms.module.core.backend.common.gui.feature.interfaces;

public interface IAutoRefreshGUI {

	default boolean shouldAutoRefresh() {
		return true;
	}

}
