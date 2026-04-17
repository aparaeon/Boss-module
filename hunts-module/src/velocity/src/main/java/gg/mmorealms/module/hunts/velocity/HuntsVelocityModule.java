package gg.mmorealms.module.hunts.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.hunts.HuntsModuleBuildConstants;
import gg.mmorealms.module.hunts.common.HuntsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;


@Plugin(
        id = HuntsModuleBuildConstants.ID,
        name = HuntsModuleBuildConstants.ID,
        version = HuntsModuleBuildConstants.VERSION,
        authors = {"ZeroDelusions"}
)
public class HuntsVelocityModule extends HuntsCommonModule implements VelocityModule {

    @Getter
    @Accessors(fluent = true)
    private static HuntsVelocityModule instance;

    public HuntsVelocityModule() {
        HuntsVelocityModule.instance = this;
    }

    @Override
    public void onInit() { }

    @Override
    public void onEnable() { }

}
