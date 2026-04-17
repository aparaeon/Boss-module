package gg.mmorealms.loader.common.utils;

import com.google.gson.JsonObject;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.CommonLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class MojangUtils {

	private static final String USER_SESSION_BASE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final String USER_PROFILE_BASE_URL = "https://api.mojang.com/users/profiles/minecraft/";

	private static final HashMap<String, UUID> USERNAME_TO_UUID_CACHE = new HashMap<>();
	private static final HashMap<UUID, String> UUID_TO_USERNAME_CACHE = new HashMap<>();

	private static final int MAX_CACHE_SIZE = 1_000;

	public static @NotNull String getUsernameOrUUID(@NotNull UUID uuid) {
		String username = getUsername(uuid);

		if (username == null) {
			username = uuid.toString();
		}

		return username;
	}

	public static @Nullable String getUsername(@Nullable UUID uuid) {
		if (uuid == null) {
			return null;
		}

		if (UUID_TO_USERNAME_CACHE.containsKey(uuid)) {
			return UUID_TO_USERNAME_CACHE.get(uuid);
		}

		String url = USER_SESSION_BASE_URL + uuid.toString().replace("-", "");
		try {
			URL mojangUrl = URI.create(url).toURL();
			HttpURLConnection connection = (HttpURLConnection) mojangUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			if (connection.getResponseCode() == 200) {
				InputStream inputStream = connection.getInputStream();
				Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
				String response = scanner.hasNext() ? scanner.next() : "";
				scanner.close();

				JsonObject jsonObject = CommonLoader.instance().getGsonSettings().getInternalGsonHolder().value().fromJson(response, JsonObject.class);
				String username = jsonObject.get("name").getAsString();

				cache(username, uuid);
				return username;
			}
		} catch (Exception exception) {
			Logger.error(exception);
		}

		return null;
	}

	public static @Nullable UUID getUUID(@Nullable String username) {
		if (username == null || username.isEmpty()) {
			return null;
		}

		if (USERNAME_TO_UUID_CACHE.containsKey(username)) {
			return USERNAME_TO_UUID_CACHE.get(username);
		}

		String url = USER_PROFILE_BASE_URL + username;
		try {
			URL mojangUrl = URI.create(url).toURL();
			HttpURLConnection connection = (HttpURLConnection) mojangUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			if (connection.getResponseCode() == 200) {
				InputStream inputStream = connection.getInputStream();
				Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
				String response = scanner.hasNext() ? scanner.next() : "";
				scanner.close();

				JsonObject jsonObject = CommonLoader.instance().getGsonSettings().getInternalGsonHolder().value().fromJson(response, JsonObject.class);
				UUID uuid = UUID.fromString(jsonObject.get("id").getAsString()
					.replaceFirst("(.{8})(.{4})(.{4})(.{4})(.{12})", "$1-$2-$3-$4-$5"));

				cache(username, uuid);
				return uuid;
			}
		} catch (Exception exception) {
			Logger.error(exception);
		}

		return null;
	}

	// Some basic caching to avoid too many requests to Mojang's API. Maybe implement a better caching strategy later.
	private static void cache(String username, UUID uuid) {
		if (USERNAME_TO_UUID_CACHE.size() >= MAX_CACHE_SIZE || UUID_TO_USERNAME_CACHE.size() >= MAX_CACHE_SIZE) {
			USERNAME_TO_UUID_CACHE.clear();
			UUID_TO_USERNAME_CACHE.clear();
		}

		if (username != null && uuid != null) {
			USERNAME_TO_UUID_CACHE.put(username, uuid);
			UUID_TO_USERNAME_CACHE.put(uuid, username);
		}
	}

}
