package gg.mmorealms.module.core.common.dto.privatebin.enums;

import lombok.Getter;

@Getter
public enum Compression {

	NONE("none"),
	ZLIB("zlib");

	private final String compression;

	Compression(String compression) {
		this.compression = compression;
	}

}
