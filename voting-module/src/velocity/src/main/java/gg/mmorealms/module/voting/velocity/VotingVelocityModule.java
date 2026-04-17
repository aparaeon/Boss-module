package gg.mmorealms.module.voting.velocity;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.voting.VotingModuleBuildConstants;
import gg.mmorealms.module.voting.common.VotingCommonModule;
import gg.mmorealms.module.voting.velocity.manager.VoteManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = VotingModuleBuildConstants.ID,
		name = VotingModuleBuildConstants.ID,
		version = VotingModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class VotingVelocityModule extends VotingCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static VotingVelocityModule instance;

	private @Inject ServerManager serverManager;
	private @Inject ProxyServer proxy;
	private @Inject FileManager fileManager;

	private VoteManager voteManager; // exported
	private VotingConfig config; // exported

	public VotingVelocityModule() {
		VotingVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(VotingConfig.class));
		this.voteManager = export(new VoteManager());
	}

	@Override
	public void onEnable() {
	}
}
