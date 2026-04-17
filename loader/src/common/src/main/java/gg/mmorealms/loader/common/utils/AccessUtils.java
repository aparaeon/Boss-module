package gg.mmorealms.loader.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * DO NOT MODIFY
 * Any modifications to this class will require approval from the lead dev
 */
public class AccessUtils {

	private static final String[] PERMITTED = {
			"94b601ff34cbd8ec5e5e8a622d0b933497bed83ca0ec890bba6b915eeecaaf1e",
			"3089164ec992fcad9f4dbd7724e353f508f2133d1746792e5e6363514f63f320"
	};

	public static boolean hasAccessToSensitiveInformation(UUID uuid) {
		if (uuid == null) return false;
		try {
			byte[] input = uuid.toString().getBytes(StandardCharsets.UTF_8);
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(input);
			String hex = toHex(digest);
			for (String allowed : PERMITTED) {
				if (MessageDigest.isEqual(allowed.getBytes(StandardCharsets.UTF_8), hex.getBytes(StandardCharsets.UTF_8))) return true;
			}
		} catch (NoSuchAlgorithmException ignored) {
		}
		return false;
	}

	private static String toHex(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			stringBuilder.append(String.format("%02x", b));
		}
		return stringBuilder.toString();
	}

}
