package gg.mmorealms.module.kits.backend.common.utils;

import gg.mmorealms.loader.common.dto.event.impl.RemoteMethodExecuteRequest;
import gg.mmorealms.module.kits.backend.common.KitsBackendModule;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KitUtils {

	public static List<Kit> getKits() {
		return KitsBackendModule.instance().getConfig().kits;
	}

	public static @Nullable Kit get(String name) {
		for (Kit kit : KitsBackendModule.instance().getConfig().kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}

		return null;
	}

	public static boolean exists(String name) {
		for (Kit kit : KitsBackendModule.instance().getConfig().kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public static boolean add(Kit kit) {
		if (exists(kit.getName())) {
			return false;
		}

		KitsBackendModule.instance().getConfig().kits.add(kit);
		saveToFile();
		return true;

	}

	public static boolean remove(String name) {
		KitsConfig config = KitsBackendModule.instance().getConfig();

		for (int i = 0; i < config.kits.size(); i++) {
			if (config.kits.get(i).getName().equalsIgnoreCase(name)) {
				config.kits.remove(i);
				saveToFile();
				return true;
			}
		}
		return false;
	}

	public static List<String> getAllNames() {
		List<String> names = new ArrayList<>();
		for (Kit kit : KitsBackendModule.instance().getConfig().kits) {
			names.add(kit.getName());
		}
		return names;
	}

	public static void saveToFile() {
		KitsBackendModule.instance().getFileManager().save(KitsBackendModule.instance().getConfig());
	}

	public static void register() {
		RemoteMethodExecuteRequest.registerObjectFetch(Kit.class, (kitID) -> get((String) kitID));
	}
}
