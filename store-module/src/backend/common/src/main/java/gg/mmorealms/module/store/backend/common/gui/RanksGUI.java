package gg.mmorealms.module.store.backend.common.gui;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.store.backend.common.StoreBackendModule;
import gg.mmorealms.module.store.backend.common.dto.StoreRank;
import gg.mmorealms.module.store.backend.common.files.StoreConfig;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;

import java.util.ArrayList;
import java.util.List;

public class RanksGUI extends GenericStoreGUI<StoreRank> {

	private static final StoreConfig CONFIG = StoreBackendModule.instance().getConfig();

	public RanksGUI(User user, IUser target) {
		super(user, target, "\uF240", "\uF23F");
	}

	public int getDiscount() {
		StoreRank highestRank = this.getHighestRank();

		if (highestRank == null) {
			Logger.debug("highestRank is null");
			return 0;
		}

		Logger.debug("highestRank: " + highestRank.getRank() + " with price: " + highestRank.getPrice().amount());
		return highestRank.getPrice().amount();
	}

	@Override
	protected List<StoreRank> getAllEntries() {
		StoreRank highestRank = this.getHighestRank();
		Logger.debug("Highest rank for user " + this.target.getUsername() + ": " + (highestRank == null ? "None" : highestRank.getRank()));

		int highestRankIndex = highestRank == null ? -1 : CONFIG.ranks.indexOf(highestRank);
		highestRankIndex++;

		return CONFIG.ranks.subList(highestRankIndex, CONFIG.ranks.size());
	}

	private StoreRank getHighestRank() {
		net.luckperms.api.model.user.User lpUser = LuckPermsUtils.getUser(this.target.getUUID());
		List<String> inheritedGroups = new ArrayList<>();

		if (lpUser != null) {
			inheritedGroups = lpUser.getInheritedGroups(QueryOptions.builder(QueryMode.CONTEXTUAL).build())
					.stream()
					.map(Group::getName)
					.map(String::toLowerCase)
					.toList();
		}

		StoreRank highestRank = null;

		for (StoreRank rank : CONFIG.ranks) {
			if (inheritedGroups.contains(rank.getRank().toLowerCase())) {
				if (highestRank == null || CONFIG.ranks.indexOf(rank) > CONFIG.ranks.indexOf(highestRank)) {
					highestRank = rank;
				}
			}
		}

		return highestRank;
	}

}
