package gg.mmorealms.module.core.backend.common.gui.feature.interfaces;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.gui.IGUI;
import gg.mmorealms.module.core.backend.common.gui.feature.impl.PagedFeature;

public interface IPagedGUI extends IGUI {

	default int getPage() {
		PagedFeature pagedFeature = this.getPagedFeature();

		if (pagedFeature == null) {
			Logger.error(new MessageBuilder("Attempted to access page information from {class} which does not have the PagedFeature.")
				.parse("class", this.getClass())
			);
			return 0;
		}

		return pagedFeature.getPage();
	}

	default void setPage(int page) {
		PagedFeature pagedFeature = this.getPagedFeature();

		if (pagedFeature == null) {
			Logger.error(new MessageBuilder("Attempted to access page information from {class} which does not have the PagedFeature.")
				.parse("class", this.getClass())
			);
			return;
		}

		pagedFeature.setPage(page);
	}

	default void backPage(ClickType click) {
		this.processPage(this.getPage() - 1);
		this.initDraw();
	}

	default void nextPage(ClickType click) {
		this.processPage(this.getPage() + 1);
		this.initDraw();
	}

	default void jumpToPage(int target) {
		this.processPage(target);
		this.initDraw();
	}

	private void processPage(int page) {
		int pageCount = this.getPagesCount();

		if (this.getSettings().paged().wrap()) {
			this.setPage(((page % pageCount) + pageCount) % pageCount);
		}

		this.setPage(Math.clamp(page, 0, pageCount - 1));
	}

	default int getPagesCount() {
		return 100_000_00;
	}

}
