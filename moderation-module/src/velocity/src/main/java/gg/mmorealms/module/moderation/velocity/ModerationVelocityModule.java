package gg.mmorealms.module.moderation.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.moderation.ModerationModuleBuildConstants;
import gg.mmorealms.module.moderation.common.ModerationCommonModule;
import gg.mmorealms.module.moderation.velocity.config.ModerationConfig;
import gg.mmorealms.module.moderation.velocity.manager.UserPunishmentsDatabaseLoader;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = ModerationModuleBuildConstants.ID,
		name = ModerationModuleBuildConstants.ID,
		version = ModerationModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class ModerationVelocityModule extends ModerationCommonModule implements VelocityModule {

	public static final String SEE_PUNISHMENT_BROADCAST_PERMISSION = "mmorealms.moderation.see_punishment_broadcast";

	@Getter
	@Accessors(fluent = true)
	private static ModerationVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;

	private ModerationConfig config; // exported
	private UserPunishmentsDatabaseLoader userPunishmentsDatabaseLoader;

	public ModerationVelocityModule() {
		ModerationVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(ModerationConfig.class));

		this.userPunishmentsDatabaseLoader = new UserPunishmentsDatabaseLoader();
	}

	@Override
	public void onEnable() {

	}

}
