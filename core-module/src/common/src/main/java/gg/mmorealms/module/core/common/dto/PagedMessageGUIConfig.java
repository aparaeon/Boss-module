package gg.mmorealms.module.core.common.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
public class PagedMessageGUIConfig {

	public String tableColor;
	public String accentColor;
	public String buttonsColor;

	public final String title;

	public boolean numberedList;
	public int entriesPerPage;

	public static PagedMessageGUIConfig.Builder builder(String title) {
		return new PagedMessageGUIConfig.Builder(title);
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	public static class Builder {
		public String tableColor = "<gold>";
		public String accentColor = "<yellow>";
		public String buttonsColor = "<red>";
		public String title;
		public boolean numberedList = true;
		public int entriesPerPage = 15;

		public Builder(String title) {
			this.title = title;
		}

		public PagedMessageGUIConfig build() {
			return new PagedMessageGUIConfig(
					tableColor,
					accentColor,
					buttonsColor,
					title,
					numberedList,
					entriesPerPage
			);
		}
	}
}
