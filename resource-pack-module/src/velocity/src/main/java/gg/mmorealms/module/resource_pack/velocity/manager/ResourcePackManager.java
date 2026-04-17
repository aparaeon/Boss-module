package gg.mmorealms.module.resource_pack.velocity.manager;

import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import gg.mmorealms.module.resource_pack.velocity.utils.HashUtils;
import net.kyori.adventure.resource.ResourcePackRequest;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourcePackManager {

	private static final String PACK_URL = "RESOURCE_PACK_URL";
	private static final String INTERNAL_PACK_URL = "INTERNAL_RESOURCE_PACK_URL";

	private final String packURL;
	private final UUID packUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private String packHashString;

	public ResourcePackManager() {
		this.packURL = SecretsUtils.getEnvironmentVariable(PACK_URL);
		String packInternalURL = SecretsUtils.getEnvironmentVariable(INTERNAL_PACK_URL);

		if (this.packURL == null) {
			throw new RuntimeException("RESOURCE_PACK_URL environment variable is not set!");
		}

		if (packInternalURL == null) {
			throw new RuntimeException("INTERNAL_RESOURCE_PACK_URL environment variable is not set!");
		}

		Logger.log("Using resource pack URL (internal): " + packInternalURL);
		Logger.log("Using resource pack URL (external): " + packURL);

		while (true) {
			try {
				Logger.log("Attempting to download resource pack to compute hash from " + packInternalURL);
				byte[] packHash = HashUtils.computeSha1FromUrl(packInternalURL);
				packHashString = HashUtils.toHex(packHash);
				Logger.log("Resource pack hash: " + packHashString);
				break;
			} catch (Exception exception) {
				Logger.error(exception);
				Logger.warn("Failed to download resource pack to compute hash, retrying in 5s...");
				try {
					//noinspection BusyWait
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void send(Player player) {
		if (!player.isActive()) {
			return;
		}

		boolean found = false;

		for (ResourcePackInfo resourcePack : player.getAppliedResourcePacks()) {
			if (resourcePack.getId().equals(packUUID)) {
				found = true;
				break;
			}
		}

		for (ResourcePackInfo resourcePack : player.getPendingResourcePacks()) {
			if (resourcePack.getId().equals(packUUID)) {
				found = true;
				break;
			}
		}

		if (found) {
			return;
		}

		net.kyori.adventure.resource.ResourcePackInfo resourcePackInfo = net.kyori.adventure.resource.ResourcePackInfo.resourcePackInfo(
				packUUID,
				URI.create(packURL),
				packHashString
		);

		ResourcePackRequest build = ResourcePackRequest.resourcePackRequest()
				.packs(resourcePackInfo)
				.required(true)
				.build();

		player.sendResourcePacks(build);
	}

}
