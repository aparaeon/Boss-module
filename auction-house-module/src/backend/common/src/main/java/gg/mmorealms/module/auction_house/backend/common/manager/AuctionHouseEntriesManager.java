package gg.mmorealms.module.auction_house.backend.common.manager;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.auction_house.backend.common.AuctionHouseBackendModule;
import gg.mmorealms.module.auction_house.backend.common.config.AuctionHouseConfig;
import gg.mmorealms.module.auction_house.backend.common.dto.database.AuctionHouseEntry;
import gg.mmorealms.module.auction_house.backend.common.exception.RateLimitException;
import gg.mmorealms.module.auction_house.common.event.AuctionHouseEntryAddedEvent;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionHouseEntriesManager {
	public static List<AuctionHouseEntry> entries;

	private static final AuctionHouseConfig config = AuctionHouseBackendModule.instance().getConfig();
	private static final List<Long> globalRequests = new ArrayList<>();

	private static long lastDatabaseCheck = 0;

	public AuctionHouseEntriesManager() {
		attemptRefreshEntriesFromDatabase();
	}

	// TODO Maybe move to a thread instead of checking if it is time to update every time an entry is requested
	private void attemptRefreshEntriesFromDatabase() {
		if (CommonLoader.DUMMY_MODE) {
			return;
		}


		long currentTimeMillis = System.currentTimeMillis();
		if (((currentTimeMillis - lastDatabaseCheck) < config.invalidateCacheTime.toMilliseconds())) {
			return;
		}

		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			entries = session.createQuery("from auction_house_entries where date > :date order by date desc", AuctionHouseEntry.class)
					.setParameter("date", currentTimeMillis - AuctionHouseBackendModule.instance().getConfig().auctionDuration.toMilliseconds())
					.list();
		}

		lastDatabaseCheck = currentTimeMillis;
	}

	public static void checkRateLimit() throws RateLimitException {
		globalRequests.add(System.currentTimeMillis());

		if (globalRequests.size() > config.globalRateLimit) {
			long oldest = globalRequests.removeFirst();
			if (System.currentTimeMillis() - oldest < config.globalRateLimitTime) {
				throw new RateLimitException(config.lang.rateLimitMessage);
			}
		}
	}

	public List<AuctionHouseEntry> getAll() throws RateLimitException {
		attemptRefreshEntriesFromDatabase();

		return entries;
	}

	public List<AuctionHouseEntry> getForPlayer(UUID ownerUUID, Integer page, Integer count) throws RateLimitException {
		checkRateLimit();

		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			return session.createQuery("from auction_house_entries where ownerUUID = :ownerUUID order by date desc", AuctionHouseEntry.class)
					.setParameter("ownerUUID", ownerUUID)
					.setFirstResult(page * count)
					.setMaxResults(count)
					.list();
		}
	}

	public List<AuctionHouseEntry> getForCategory(String category) throws RateLimitException {
		attemptRefreshEntriesFromDatabase();

		List<AuctionHouseEntry> requestedEntries = new ArrayList<>();
		for (AuctionHouseEntry entry : entries) {
			if (entry.getCategory().equals(category)) {
				requestedEntries.add(entry);
			}
		}

		return requestedEntries;
	}

	public void deleteEntryFromCache(AuctionHouseEntry entry) {
		if (entries == null) {
			return;
		}

		entries.remove(entry);
	}

	public void deleteEntryFromCache(Long id) {
		if (entries == null) {
			return;
		}

		if (!entries.remove(AuctionHouseEntry.getDummy(id))) {
			Logger.error("Did not found an auction house entry in cache with id " + id + " to delete");
		}
	}

	public boolean hasEntryInCache(AuctionHouseEntry entry) {
		if (entries == null) {
			return false;
		}

		return entries.contains(entry);
	}

	public void addEntryInCache(AuctionHouseEntry entry, boolean sendBroadcast) {
		if (sendBroadcast) {
			new AuctionHouseEntryAddedEvent(AuctionHouseBackendModule.instance().toJson(entry)).send();
		}

		if (entries == null) {
			Logger.info("The auction entry with id " + entry.getId() + " has been added to the database.");
			return;
		}

		entries.addFirst(entry);
		Logger.info("The auction entry with id " + entry.getId() + " has been added to the cache.");
	}
}
