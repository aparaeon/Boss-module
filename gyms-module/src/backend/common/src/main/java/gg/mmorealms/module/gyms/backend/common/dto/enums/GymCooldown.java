package gg.mmorealms.module.gyms.backend.common.dto.enums;

import gg.mmorealms.loader.common.utils.StringUtils;

public class GymCooldown {
	public static String win(String gymName) {
		return StringUtils.toSnakeCase(gymName) + "_win";
	}

	public static String loss(String gymName) {
		return StringUtils.toSnakeCase(gymName) + "_loss";
	}
}
