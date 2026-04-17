package gg.mmorealms.module.resource_pack.velocity.utils;

import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

public class HashUtils {

	public static byte[] computeSha1FromUrl(String url) throws Exception {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		try (InputStream is = new URL(url).openStream()) {
			byte[] buffer = new byte[8192];
			int read;
			while ((read = is.read(buffer)) != -1) {
				sha1.update(buffer, 0, read);
			}
		}

		return sha1.digest();
	}

	public static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

}


