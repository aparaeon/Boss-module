package gg.mmorealms.module.realms.backend.common.manager;

import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class RealmsUtils {
	public static final RealmsConfig CONFIG = RealmsBackendModule.instance().getConfig();

	public static IRealm getCurrentRealm(Location location) {
		return IRealm.getAtLocation(location);
	}

	public static IRealm getCurrentRealm(IUser user, RealmPermission permission) {
		IRealm realm = IRealm.getAtLocation(user.getLocation());
		if (realm == null) {
			user.sendMessage(CONFIG.lang.notInARealm);
			return null;
		}

		if (!realm.checkPermission(user, permission)) {
			return null;
		}

		return realm;
	}

	public static List<UUID> getMembership(UUID userUuid) {
		String jsonPath = "$.\"" + userUuid.toString() + "\"";

		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			session.setDefaultReadOnly(true);

			return session.createNativeQuery("""
							    SELECT "ownerUUID"
							    FROM realms
							    WHERE jsonb_path_exists(members, CAST(:jsonPath AS jsonpath))
							""", UUID.class)
					.setParameter("jsonPath", jsonPath)
					.getResultList();
		}
	}
}
