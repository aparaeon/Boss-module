package gg.mmorealms.module.core.common.dto.privatebin.enums;

import lombok.Getter;

@Getter
public enum PasteFormat {

	PLAINTEXT("plaintext"),
	SYNTAX_HIGHLIGHTING("syntaxhighlighting"),
	MARKDOWN("markdown");

	private final String pasteFormat;

	PasteFormat(String pasteFormat) {
		this.pasteFormat = pasteFormat;
	}

}
