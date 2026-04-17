package gg.mmorealms.module.hunts.backend.fabric;

import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

@Getter
public class HuntsFabricModule extends HuntsBackendModule implements ModInitializer {

    @Getter
    @Accessors(fluent = true)
    private static HuntsFabricModule instance;

    public HuntsFabricModule() {
        HuntsFabricModule.instance = this;
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onEnable() {

    }

	@Override
	public void onInitialize() {
		this.setup();
	}
}
