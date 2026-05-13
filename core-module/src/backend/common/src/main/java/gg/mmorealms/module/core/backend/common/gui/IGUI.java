package gg.mmorealms.module.core.backend.common.gui;

import gg.mmorealms.module.core.backend.common.gui.feature.IGUIFeature;
import gg.mmorealms.module.core.backend.common.gui.feature.impl.PagedFeature;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IGUI {

	void sendUpdate();

	void initDraw();

	GUISettings getSettings();

	@Nullable PagedFeature getPagedFeature();

	List<IGUIFeature> getFeatures();

	default <Feature extends IGUIFeature> @Nullable Feature getFeature(Class<Feature> featureClass) {
		for (IGUIFeature feature : this.getFeatures()) {
			if (featureClass.isAssignableFrom(feature.getClass())) {
				return featureClass.cast(feature);
			}
		}

		return null;
	}

}
