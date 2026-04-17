package gg.mmorealms.module.core.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import lombok.Getter;

@Getter
public abstract class PagedGUI extends GUI {

	private int page;

	public PagedGUI(User user, Settings settings) {
		this(user, settings, 0);
	}

	public PagedGUI(User user, Settings settings, int page) {
		super(user, settings);
		this.page = page;
	}

	protected void previousPage(ClickType click) {
		page = processPage(page - 1);

		refresh();
	}

	protected void nextPage(ClickType click) {
		page = processPage(page + 1);

		refresh();
	}

	protected void setPage(int page) {
		this.page = processPage(page);

		refresh();
	}

	private int processPage(int page) {
		int pageCount = getPagesCount();

		if (this.getSettings().wrapPage()) {
			return ((page % pageCount) + pageCount) % pageCount;
		}

		return Math.clamp(page, 0, pageCount - 1);
	}

	protected int getPagesCount() {
		return 1_000_000;
	}

}
