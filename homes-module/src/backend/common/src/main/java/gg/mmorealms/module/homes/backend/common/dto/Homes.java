package gg.mmorealms.module.homes.backend.common.dto;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.homes.backend.common.HomesBackendModule;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.manager.HomesLoader;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "home_users")
@NoArgsConstructor
@Setter
@Getter
public class Homes implements IDatabaseEntry<UUID>, IHomes {
	@Id
	@NotNull
	protected UUID uuid;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "user_uuid") // Foreign key
	protected List<Home> homesList = new ArrayList<>();

	public Homes(@NotNull UUID uuid) {
		this.uuid = uuid;

		getLoader().cache(uuid, this);
	}

	public Boolean isEmpty() {
		return homesList.isEmpty();
	}

	@Override
	public List<Home> getHomes() {
		return homesList;
	}

	@Override
	public @Nullable Home get(String name) {
		for (Home home : homesList) {
			if (home.getName().equalsIgnoreCase(name)) {
				return home;
			}
		}

		return null;
	}

	@Override
	public Home get(int index) throws IndexOutOfBoundsException {
		return homesList.get(index);
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public HomesLoader getLoader() {
		return HomesBackendModule.instance().getHomesLoader();
	}

	public void add(@NotNull Home home) {
		homesList.add(home);
	}

	/**
	 * Removes the home with the specified name (optional operation).
	 * Returns the element removed from the list.
	 *
	 * @param name the name of the element to be removed
	 * @return if there was an element removed
	 */
	@Override
	public Boolean remove(String name) {
		Home toRemove = get(name);

		if (toRemove == null) {
			return false;
		}

		homesList.remove(toRemove);
		return true;
	}

	@Override
	public void removeAll() {
		homesList.clear();
	}

	@Override
	public Boolean has(String name) {
		for (Home home : homesList) {
			if (home.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	public String toPrettyString() {
		HomesConfig config = HomesBackendModule.instance().getConfig();

		if (homesList.isEmpty()) {
			return config.lang.noHomeMessageListCommand
					.parse("user", IUser.getByUUID(uuid).getUsername())
					.parse();
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(config.lang.headerListCommand
				.parse("user", IUser.getByUUID(uuid).getUsername())
		);

		for (Home home : homesList) {
			String name = home.getName();
			stringBuilder.append(config.lang.entryListCommand.parse("name", name));
		}

		return stringBuilder.toString();
	}

	public int size() {
		return homesList.size();
	}

	@Override
	public List<String> getNames() {
		List<String> homesNames = new ArrayList<>();
		for (Home home : homesList) {
			homesNames.add(home.getName());
		}

		return homesNames;
	}

	@Override
	public void save() {
		try {
			IDatabaseEntry.super.save();
		} catch (DatabaseSaveException e) {
			// This happens when there were multiple cache commits executed at the same time
			// After one saves the entry correctly, the rest will get this exception, so we can safely ignore it
			if (e.getCause() instanceof ConstraintViolationException constraintException &&
					constraintException.getConstraintName().equalsIgnoreCase("unique_name_to_uuid")) {
				return;
			}

			Logger.error(e);
		}
	}
}
