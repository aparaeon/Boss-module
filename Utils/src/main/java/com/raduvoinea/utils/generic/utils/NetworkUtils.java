package com.raduvoinea.utils.generic.utils;

import com.raduvoinea.utils.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class NetworkUtils {

	public static @NotNull String getHostname() {
		String hostname;

		hostname = System.getenv("DEV_HOSTNAME");

		if (hostname != null) {
			return hostname;
		}

		try {
			hostname = ShellUtils.executeAndCapture("/usr/bin/hostname --fqdn");
		} catch (IOException | InterruptedException exception) {
			hostname = System.getenv("HOSTNAME");

			if (hostname == null) {
				Logger.error("There was an error while trying to get the hostname. Environment variable HOSTNAME does not exist");
				throw new RuntimeException("Failed to get hostname");
			}
		}

		hostname = hostname.replace("\n", "").strip();

		return hostname;
	}

}
