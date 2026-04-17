package gg.mmorealms.module.core.backend.common.dto.user;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.message_builder.GenericMessageBuilder;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.exceptions.AccessedOfflineUserException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity(name = "users")
@Table(indexes = {
	@Index(columnList = "username")
})
public class User implements IDatabaseEntry<UUID>, IUser {

	@Id
	private UUID uuid;

	private @Setter String username = "";

	public User(UUID uuid) {
		this.uuid = uuid;
	}

	public static User get(ServerPlayer player) {
		return IUser.getByPlayer(player);
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public SyncedDatabaseLoader<UUID, ?, ?, ?> getLoader() {
		return CoreBackendModule.instance().getUserLoader();
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	public @NotNull ServerPlayer getPlayer() {
		ServerPlayer player = CoreBackendModule.instance().getServer().getPlayerList().getPlayer(this.getUUID());

		if (player == null) {
			throw new AccessedOfflineUserException();
		}

		return player;
	}

	@Override
	public void sendMessage(String message) {
		sendMessage(message, false);
	}

	@Override
	public void sendActionMessage(String message) {
		sendMessage(message, true);
	}

	private void sendMessage(String message, boolean isActionBar) {
		try {
			ServerPlayer player = getPlayer();
			player.sendSystemMessage(CoreBackendModule.instance().getMiniMessageManager().parse(message), isActionBar);
		} catch (AccessedOfflineUserException ignored) {
		}
	}

	@Override
	public Boolean hasPermission(@NotNull String permission) {
		return LuckPermsUtils.checkPermission(ServerPlayer.class, getPlayer(), permission);
	}

	public Integer getCountPermission(@NotNull String basePermission, Range range) {
		if (!basePermission.endsWith(".")) {
			basePermission = basePermission + ".";
		}

		int min = range.getMin();
		int max = range.getMax();

		for (int i = max; i >= min; i--) {
			if (hasPermission(basePermission + i)) {
				return i;
			}
		}

		return 0;
	}

	public void sendMessage(GenericMessageBuilder<?> message) {
		this.sendMessage(message.toString());
	}

	public Location getBlockLocation() {
		//noinspection resource
		return Location.builder(
				getPlayer().getBlockX(),
				getPlayer().getBlockY(),
				getPlayer().getBlockZ()
			)
			.pitch(getPlayer().getXRot())
			.yaw(getPlayer().getYRot())
			.world(getPlayer().level().dimension().location().toString())
			.build();
	}

	public Location getLocation() {
		//noinspection resource
		return Location.builder(
				getPlayer().getX(),
				getPlayer().getY(),
				getPlayer().getZ()
			)
			.pitch(getPlayer().getXRot())
			.yaw(getPlayer().getYRot())
			.world(getPlayer().level().dimension().location().toString())
			.build();
	}

	@Override
	public void kick(String message) {
		ServerPlayer player = getPlayer();

		player.connection.disconnect(CoreBackendModule.instance().getMiniMessageManager().parse(message));
	}

	public static void executeForUser(String username, ArgLambda<User> executor, Lambda failExecutor) {
		IUser.executeForUser(username, executor, failExecutor, User.class);
	}

	public static @Nullable User unsafeGetByUUID(UUID uuid) {
		ServerPlayer player = CoreBackendModule.instance().getServer().getPlayerList().getPlayer(uuid);

		if (player == null) {
			return null;
		}

		return get(player);
	}

	public static @NotNull User unsafeGetByUUIDOrThrow(UUID uuid) throws AccessedOfflineUserException {
		User user = unsafeGetByUUID(uuid);

		if (user == null) {
			throw new AccessedOfflineUserException();
		}

		return user;
	}

}