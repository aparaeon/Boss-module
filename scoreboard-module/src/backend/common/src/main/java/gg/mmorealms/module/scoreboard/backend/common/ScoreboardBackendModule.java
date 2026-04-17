package gg.mmorealms.module.scoreboard.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.scoreboard.backend.common.manager.ScoreboardManager;
import gg.mmorealms.module.scoreboard.common.ScoreboardComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class ScoreboardBackendModule extends ScoreboardComonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static ScoreboardBackendModule instance;

	private @Inject MinecraftServer server;
	private @Inject BackendMiniMessageManager miniMessageManager;

	private ScoreboardManager scoreboardManager;

	public ScoreboardBackendModule() {
		ScoreboardBackendModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {
		this.scoreboardManager =  new ScoreboardManager();
	}

	@Override
	public void onEnable() throws ModuleException {

	}


}