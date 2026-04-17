package gg.mmorealms.loader.common.dto.database.cache;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.database.ISavable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AutoSaveCache<Key, CachedObject extends ISavable> extends Cache<Key, CachedObject> {

	@SuppressWarnings("FieldCanBeLocal") // GX Prevention
	private final CancelableTimeTask task;
	private final Lambda preSaveExecutor;
	private final ClearCondition<Key, CachedObject> clearCondition;
	private boolean log = true;

	public AutoSaveCache(Class<Key> keyClass, Class<CachedObject> cachedObjectClass,
	                     Time autoSaveInterval, ClearCondition<Key, CachedObject> clearCondition) {
		this(keyClass, cachedObjectClass, autoSaveInterval, clearCondition, () -> {
		});
	}

	public AutoSaveCache(Class<Key> keyClass, Class<CachedObject> cachedObjectClass,
	                     Time autoSaveInterval, ClearCondition<Key, CachedObject> clearCondition, Lambda preSaveExecutor) {
		super(keyClass, cachedObjectClass);
		this.task = ScheduleUtils.runTaskTimer(() -> saveCache(false), autoSaveInterval);
		this.clearCondition = clearCondition;
		this.preSaveExecutor = preSaveExecutor;
	}

	public void disableLog() {
		this.log = false;
	}

	public void saveCache(boolean blocking) {
		if (log) {
			Logger.debug(new MessageBuilder("Auto-saving {cache}...")
				.parse("cache", this.getCachedObjectClass().getSimpleName())
				.parse()
			);
		}


		CompletableFuture<Void> completableFuture = ScheduleUtils.runTaskAsync(() -> {
			this.preSaveExecutor.run();
			List<Key> toRemove = Collections.synchronizedList(new ArrayList<>());

			this.forEach((identifier, cachedObject) -> {
				try {
					if (clearCondition.check(identifier, cachedObject)) {
						toRemove.add(identifier);
					}

					cachedObject.save();
				} catch (Throwable ignored) {
				}
			});

			try {
				toRemove.forEach(this::remove);
			} catch (Throwable ignored) {
			}

			if (log) {
				Logger.debug(new MessageBuilder("Auto-saved {cache} successfully!")
					.parse("cache", this.getCachedObjectClass().getSimpleName())
					.parse()
				);
			}
		});

		if (blocking) {
			completableFuture.join();
		}

	}

	public interface ClearCondition<Key, CachedObject> {
		boolean check(Key key, CachedObject cachedObject);
	}
}