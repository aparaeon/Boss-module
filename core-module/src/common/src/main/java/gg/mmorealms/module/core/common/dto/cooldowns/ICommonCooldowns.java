package gg.mmorealms.module.core.common.dto.cooldowns;

import com.raduvoinea.utils.file_manager.utils.DateUtils;
import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.loader.common.dto.database.ISavable;
import org.jetbrains.annotations.NotNull;

public interface ICommonCooldowns extends ISavable {

	// =============================== LOCAL METHODS ===============================

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	default boolean hasExpired(String type) {
		return getRemaining(type) <= 0;
	}

	@SuppressWarnings("unused")
	default boolean isActive(String type) {
		return getRemaining(type) > 0;
	}

	default boolean isPresent(String type) {
		return get(type) != 0;
	}

	default String getFormattedTime(String type) {
		return DateUtils.convertToPeriod(getRemaining(type));
	}

	default void set(String type, Time time) {
		set(type, time.toMilliseconds());
	}

	default void remove(String type) {
		set(type, 0);
	}

	default long getRemaining(String type) {
		return get(type) - System.currentTimeMillis();
	}

	// =============================== REMOTE METHODS ===============================

	void set(String type, long time);

	Long get(@NotNull String id);

}
