package gg.mmorealms.module.tutorial.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.tutorial.TutorialModuleBuildConstants;
import gg.mmorealms.module.tutorial.common.TutorialCoreModule;
import gg.mmorealms.module.tutorial.velocity.files.TutorialConfig;
import gg.mmorealms.module.tutorial.velocity.manager.TutorialManager;
import gg.mmorealms.module.tutorial.velocity.manager.TutorialProgressLoader;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = TutorialModuleBuildConstants.ID,
		name = TutorialModuleBuildConstants.ID,
		version = TutorialModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class TutorialVelocityModule extends TutorialCoreModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static TutorialVelocityModule instance;

	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;
	private @Inject ProxyServer proxy;

	private TutorialProgressLoader tutorialProgressLoader;
	private TutorialManager tutorialManager;
	private TutorialConfig config;

	public TutorialVelocityModule() {
		TutorialVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = this.fileManager.load(TutorialConfig.class);

		this.tutorialProgressLoader = new TutorialProgressLoader();
		this.tutorialManager = new TutorialManager();
	}

	@Override
	public void onEnable() {
	}
}
