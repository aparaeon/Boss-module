package gg.mmorealms.module.core.common.files;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.util.List;

public class CommonCoreConfig {

	public PagedMessageLang pagedMessageLang = new PagedMessageLang();

	public static class PagedMessageLang {
		public MessageBuilder format = new MessageBuilder("{table_color} ===============   {accent_color}{title}{table_color}   ==============<newline>" +
				"{messages}<newline>" +
				"<newline>{prev_action} {button_color}Previous<reset>   {table_color}=====   {accent_color}Page {page}/{max_page}   {table_color}=====   {next_action}{button_color}Next<newline>");

		public MessageBuilder entry = new MessageBuilder("{optional_index}{message}<newline>");
		public MessageBuilder index = new MessageBuilder("<gray>{index}: ");

		public String firstPageHoverMessage = "This is the first page";
		public String lastPageHoverMessage = "This is the last page";
	}

	public static class Lang {
		public MessageBuilder dumpTemplate = new MessageBuilder("""
				# MMORealms Dump - {date}
				
				## Server Information
				- **Server Name**: {server_name}
				- **Version**: {version}
				- **Host**: {hostname}:{port}
				- **Online Players**: {total_online_players} ({locally_online_players} players)
				
				
				
				## Modules
				{modules}
				
				
				
				## Database Cache
				{caches}
				
				
				
				## Commands
				{commands}
				
				
				
				## Other Dumps
				{other_dumps}
				"""
		);
		public MessageBuilderList dumpModuleHeader = new MessageBuilderList(List.of(
				"| Module ID | Version | Author(s) | Status | Dependencies |",
				"|-----------|---------|-----------|--------|--------------|"
		));
		public MessageBuilder dumpModuleTemplate = new MessageBuilder("| `{module_id}` | {version} | {authors} | {status} | {dependencies} |");
		public MessageBuilder dumpCommandTemplate = new MessageBuilder("- `{command}`");
		public MessageBuilder dumpCacheTemplate = new MessageBuilder(
				"""
						### {cache} ({count}):
						{entries}
						"""
		);
		public MessageBuilder dumpCacheEntryTemplate = new MessageBuilder(
				"""
						#### {id}:
						```json
						{json}
						```
						"""
		);
		public MessageBuilder dumpAdditionalHeader = new MessageBuilder("### {module}");
		public MessageBuilder customDumpEntryTemplate = new MessageBuilder("""
				### {title}
				{data}
				""");
	}

}
