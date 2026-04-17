package gg.mmorealms.module.homes.velocity.manager;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;

public class HomesVelocityUtils {
	// When the proxy starts, delete all the wild homes as this was most probably a server-wide restart
	// If we expect the proxy to crash, this should be deleted, and honestly we might have bigger problems if that is the case
	// But from what I can see, the proxy never crashed so this makes sense
	public static void deleteWildHomes() {
		try{
			DatabaseManager.instance().getSessionFactory().inTransaction((session -> {
				// I know this seems sketchy, but I need to delete the parent first, using the data from the child, so I don't have any other choice
				session.createNativeQuery("DELETE FROM homes_wild").executeUpdate();
				session.createNativeQuery("DELETE FROM homes WHERE id IN (SELECT id FROM homes_wild)").executeUpdate();
			}));
		}catch (Exception exception) {
			Logger.error(exception);
		}

	}
}
