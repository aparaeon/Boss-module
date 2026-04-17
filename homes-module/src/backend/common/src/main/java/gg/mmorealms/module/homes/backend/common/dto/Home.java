package gg.mmorealms.module.homes.backend.common.dto;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.loader.common.dto.location.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity(name = "homes")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_uuid"}, name = "unique_name_to_uuid"))
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "home_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Home implements IDatabaseEntry<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	@Column(nullable = false)
	protected String name;
	@Getter
	@JdbcTypeCode(SqlTypes.JSON)
	protected Location location;

	public Home(String name, Location location) {
		this.name = name;
		this.location = location;
	}

	public abstract void teleport(User user);

	@Override
	public Long getIdentifier() {
		return id;
	}

	@Override
	public DatabaseLoader<Long, ?, ?> getLoader() {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
