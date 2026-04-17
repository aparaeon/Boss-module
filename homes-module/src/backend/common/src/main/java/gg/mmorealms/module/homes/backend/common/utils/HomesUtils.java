package gg.mmorealms.module.homes.backend.common.utils;

import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.dto.Home;
import gg.mmorealms.module.homes.backend.common.dto.IHomes;
import gg.mmorealms.module.homes.backend.common.dto.sub.RealmHome;
import gg.mmorealms.module.homes.backend.common.dto.sub.SpawnHome;
import gg.mmorealms.module.homes.backend.common.dto.sub.WildHome;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import net.minecraft.server.level.ServerPlayer;

public class HomesUtils {
	// 3 pages
	private static final int MAX_HOMES = 28 * 3;

	private final static HomesConfig CONFIG = HomesBackendModule.instance().getConfig();

	public static int getMaxHomes(ServerPlayer player) {
		int i;
		for (i = MAX_HOMES; i > 1; i--) {
			if (IUser.getByPlayer(player).hasPermission(CONFIG.homesPermission.parse("number", i).parse())) {
				break;
			}
		}
		return i;
	}

	public static void addHome(User user, String name) {
		IHomes homes = IHomes.getByUser(user);

		if (homes.getHomes().size() == getMaxHomes(user.getPlayer())) {
			user.sendMessage(CONFIG.lang.maxHomes);
			return;
		}

		if (homes.has(name)) {
			user.sendMessage(CONFIG.lang.alreadyExistsHome);
			return;
		}

		Home home = switch (BackendLoader.instance().getServerType()) {
			case SPAWN -> new SpawnHome(name, user.getLocation());
			case WILD -> new WildHome(name, user.getLocation());
			case REALMS -> createRealmHome(name, user);
			default -> {
				user.sendMessage(CONFIG.lang.invalidServerType);
				yield null;
			}
		};

		if (home == null) {
			return;
		}

		homes.add(home);
		user.sendMessage(CONFIG.lang.successAddHome.parse("name", name).parse());
	}

	private static RealmHome createRealmHome(String name, User user) {
		IRealm realm = IRealm.getAtLocation(user.getLocation());
		if (realm == null) {
			user.sendMessage(CONFIG.lang.notFoundRealmHome);
			return null;
		}

		if (!realm.checkPermission(user, RealmPermission.SET_HOME)) {
			return null;
		}

		return RealmHome.create(name, user.getLocation(), realm);
	}
}
