package gg.mmorealms.module.resource_pack.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.dependency_injection.exception.InjectionException;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.Environment;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.resource_pack.ResourcePackModuleBuildConstants;
import gg.mmorealms.module.resource_pack.common.ResourcePackComonModule;
import gg.mmorealms.module.resource_pack.velocity.manager.ResourcePackManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = ResourcePackModuleBuildConstants.ID,
		name = ResourcePackModuleBuildConstants.ID,
		version = ResourcePackModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class ResourcePackVelocityModule extends ResourcePackComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static ResourcePackVelocityModule instance;

	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject Environment environment;

	private ResourcePackManager resourcePackManager;

	public ResourcePackVelocityModule() {
		ResourcePackVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		resourcePackManager = export(new ResourcePackManager());
	}

	@Override
	public void onEnable() {

	}
}