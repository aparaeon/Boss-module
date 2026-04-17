package gg.mmorealms.module.core.backend.common.dto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class ServerProperties extends HashMap<String, String> {

	public ServerProperties(File file) throws IOException {
		String data = Files.readString(file.toPath());
		String[] lines = data.split("\n");

		for (String line : lines) {
			String nonCommentedLine = line.split("#")[0].strip();
			String[] split = nonCommentedLine.split("=");
			if (split.length == 2) {
				put(split[0].strip(), split[1].strip());
			}
		}
	}

}
