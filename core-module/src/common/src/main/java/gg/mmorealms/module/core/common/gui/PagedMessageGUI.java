package gg.mmorealms.module.core.common.gui;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.CoreCommonModule;
import gg.mmorealms.module.core.common.dto.PagedMessageGUIConfig;
import gg.mmorealms.module.core.common.files.CommonCoreConfig;

import java.util.ArrayList;
import java.util.List;

public class PagedMessageGUI {
	private final List<String> messages = new ArrayList<>();
	private final int pages;

	public PagedMessageGUI(String message, PagedMessageGUIConfig pageConfig, String baseCommand) {
		this(message.lines().toList(), pageConfig, baseCommand);
	}

	public PagedMessageGUI(List<String> message, PagedMessageGUIConfig pageConfig, String baseCommand) {
		this.pages = (int) Math.ceil((double) message.size() / pageConfig.entriesPerPage);

		if (!baseCommand.startsWith("/")) {
			baseCommand = "/" + baseCommand;
		}

		for (int page = 0; page < pages; page++) {
			int upperLimit = Integer.min(pageConfig.entriesPerPage * (page + 1), message.size());
			if (page == pages - 1) {
				upperLimit = message.size();
			}

			StringBuilder entries = new StringBuilder();

			CommonCoreConfig config = CoreCommonModule.instance().getConfig();
			for (int i = pageConfig.entriesPerPage * page; i < upperLimit; i++) {
				MessageBuilder entry = config.pagedMessageLang.entry
						.parse("message", message.get(i));

				String optionalIndex = !message.get(i).isEmpty() && pageConfig.numberedList ?
						config.pagedMessageLang.index.parse("index", i + 1).parse() :
						"";
				entry = entry.parse("optional_index", optionalIndex);

				entries.append(entry);
			}

			entries.append("<reset>");

			String prevAction;
			if (page == 0) {
				prevAction = new MessageBuilder("<hover:show_text:'{message}'>")
						.parse("message", config.pagedMessageLang.firstPageHoverMessage)
						.parse();

			} else {
				prevAction = new MessageBuilder("<click:run_command:{command} {page}>")
						.parse("command", baseCommand)
						.parse("page", page)
						.parse();
			}

			String nextAction;
			if (page == pages - 1) {
				nextAction = new MessageBuilder("<hover:show_text:'{message}'>")
						.parse("message", config.pagedMessageLang.lastPageHoverMessage)
						.parse();
			} else {
				nextAction = new MessageBuilder("<click:run_command:{command} {page}>")
						.parse("command", baseCommand)
						.parse("page", page + 2)
						.parse();
			}

			String bakedMessage = config.pagedMessageLang.format
					.parse("table_color", pageConfig.tableColor)
					.parse("accent_color", pageConfig.accentColor)
					.parse("title", pageConfig.title)
					.parse("messages", entries)
					.parse("prev_action", prevAction)
					.parse("page", page + 1)
					.parse("max_page", pages)
					.parse("button_color", pageConfig.buttonsColor)
					.parse("next_action", nextAction)
					.parse();

			messages.add(bakedMessage);
		}

	}

	public int convert(String pageStr) {
		int page = 0;
		if (pageStr != null && !pageStr.isEmpty()) {
			try {
				page = Integer.parseInt(pageStr) - 1;
			} catch (NumberFormatException ignored) {
				// Use default in this case
			}

			if (page < 0) {
				page = 0;
			}

			if (page >= pages) {
				page = pages - 1;
			}
		}

		return page;
	}

	public void send(Object player, String page) {
		send(player, convert(page));
	}

	public void send(Object player, int page) {
		CoreCommonModule.instance().sendMessage(player, messages.get(page));
	}
}