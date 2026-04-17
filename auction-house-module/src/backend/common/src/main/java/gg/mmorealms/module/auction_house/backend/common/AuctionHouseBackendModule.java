package gg.mmorealms.module.auction_house.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.auction_house.backend.common.config.AuctionHouseConfig;
import gg.mmorealms.module.auction_house.backend.common.manager.AuctionHouseEntriesManager;
import gg.mmorealms.module.auction_house.common.AuctionHouseCommonModule;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class AuctionHouseBackendModule extends AuctionHouseCommonModule implements BackendModule {

	public static final String AUCTION_HOUSE_MAX_ENTRIES_PERMISSION_BASE = "mmorealms.auction_house.entries.";
	public static final String AUCTION_HOUSE_OVERRIDE_COOLDOWN_PERMISSION = "mmorealms.auction_house.cooldown_override";
	public static final String AUCTION_HOUSE_COOLDOWN_NAME = "ah_cooldown";

	@Getter
	@Accessors(fluent = true)
	private static AuctionHouseBackendModule instance;

	private @Inject FileManager fileManager;
	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject RedisManager redisManager;

	private AuctionHouseConfig config; // exported
	private AuctionHouseEntriesManager entriesManager;

	public AuctionHouseBackendModule() {
		instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(this.fileManager.load(AuctionHouseConfig.class));
		this.entriesManager = new AuctionHouseEntriesManager();
	}

	@Override
	public void onEnable() {
	}

	public static boolean hasActiveCooldown(User user) {
		if (user.hasPermission(AUCTION_HOUSE_OVERRIDE_COOLDOWN_PERMISSION)) {
			return false;
		}

		IBackendCooldowns cooldown = IBackendCooldowns.getByUser(user);
		return cooldown.isActive(AUCTION_HOUSE_COOLDOWN_NAME);
	}

}