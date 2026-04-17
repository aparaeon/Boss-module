package gg.mmorealms.module.essentials.velocity.config;

import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.generic.dto.Pair2;

import java.util.List;

public class SimpleCommandsConfig {
	public static SimpleCommandsConfig INSTANCE;

	public List<Pair2<String, String>> commands = List.of(
			new Pair2<>("command_name", "<green>Message that the user gets")
	);

	public static void init(FileManager fileManager) {
		INSTANCE = fileManager.load(SimpleCommandsConfig.class);
	}
}