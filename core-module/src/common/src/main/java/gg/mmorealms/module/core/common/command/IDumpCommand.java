package gg.mmorealms.module.core.common.command;

import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnLambda;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.LoadedModule;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.module.core.common.CoreCommonModule;
import gg.mmorealms.module.core.common.dto.CustomDump;
import gg.mmorealms.module.core.common.dto.privatebin.enums.PasteFormat;
import gg.mmorealms.module.core.common.dto.privatebin.models.Paste;
import gg.mmorealms.module.core.common.files.CommonCoreConfig;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.*;

public interface IDumpCommand {

	HashMap<Module, List<CustomDump>> ADDITIONAL_DUMPS = new HashMap<>();

	static void registerAdditionalDump(Module module, String title, ReturnLambda<String> dumper) {
		ADDITIONAL_DUMPS.putIfAbsent(module, new ArrayList<>());
		ADDITIONAL_DUMPS.get(module).add(new CustomDump(title, dumper));
	}

	CommonCoreConfig.Lang getLang();

	String getHostname();

	int getPort();

	int getTotalOnlyPlayers();

	int getLocallyOnlyPlayers();

	@SneakyThrows(IOException.class)
	default String generate() {
		List<String> modules = new ArrayList<>();
		modules.addAll(this.getLang().dumpModuleHeader.parse());
		modules.addAll(
			CoreCommonModule.instance().getModuleManager().getLoadedModules().stream()
				.map(this::dumpModule)
				.sorted(String::compareTo)
				.toList()
		);
		List<String> caches = DatabaseLoader.getALL().stream()
			.map(this::dumpDatabaseLoader)
			.sorted(String::compareTo)
			.toList();
		List<String> commands = CommonLoader.instance().getCommandManager().getCommands().stream()
			.map(this::dumpCommand)
			.flatMap(Collection::stream)
			.sorted()
			.toList();
		List<String> additionalDumps = ADDITIONAL_DUMPS.entrySet().stream()
			.map((Map.Entry<Module, List<CustomDump>> entry) -> this.dumpAdditionalMultiple(entry.getKey(), entry.getValue()))
			.toList();

		String dump = this.getLang().dumpTemplate
			.parse("date", DateUtils.getDate("dd/MM/yyyy HH:mm:ss"))
			.parse("server_name", CoreCommonModule.instance().getServerID())
			.parse("version", "N/A")
			.parse("hostname", this.getHostname())
			.parse("port", this.getPort())
			.parse("total_online_players", this.getTotalOnlyPlayers())
			.parse("locally_online_players", this.getLocallyOnlyPlayers())
			.parse("modules", String.join("\n", modules))
			.parse("caches", String.join("\n\n", caches))
			.parse("commands", String.join("\n", commands))
			.parse("other_dumps", String.join("\n\n", additionalDumps))
			.parse();

		Paste paste = new Paste("https://privatebin.net/")
			.setMessage(dump)
			.setPasteFormat(PasteFormat.MARKDOWN)
			.encrypt();

		String url = paste.send();
		return new MessageBuilder("Your dump is available <green><click:open_url:{url}>here</click>")
			.parse("url", url)
			.parse();
	}

	private String dumpAdditionalMultiple(Module module, List<CustomDump> customDumps) {
		List<String> dumps = new ArrayList<>();
		dumps.add(
			this.getLang().dumpAdditionalHeader
				.parse("module", module.id())
				.parse()
		);
		dumps.addAll(
			customDumps.stream()
				.sorted((first, second) -> first.title().compareToIgnoreCase(second.title()))
				.map(this::dumpAdditional)
				.toList()
		);
		return String.join("\n", dumps);
	}

	private String dumpAdditional(CustomDump customDump) {
		return getLang().customDumpEntryTemplate
			.parse("title", customDump.title())
			.parse("data", customDump.dumper().run())
			.parse();
	}

	private String dumpModule(LoadedModule module) {
		return getLang().dumpModuleTemplate
			.parse("module_id", module.getAnnotation().id())
			.parse("version", module.getAnnotation().version())
			.parse("authors", String.join(", ", module.getAnnotation().authors()))
			.parse("dependencies", String.join(", ", Arrays.stream(module.getDependencies()).map(moduleID -> "`" + moduleID.id() + "`").toList()))
			.parse("status", module.getState().toString())
			.parse();
	}

	private String dumpDatabaseLoader(DatabaseLoader<?, ?, ?> databaseLoader) {
		List<String> cacheEntries = new ArrayList<>();
		databaseLoader.getCache().forEach((key, value) ->
			cacheEntries.add(getLang().dumpCacheEntryTemplate
				.parse("id", CommonLoader.instance().getGsonSettings().getUserFacingGsonHolder().value().toJson(key))
				.parse("json", CommonLoader.instance().getGsonSettings().getUserFacingGsonHolder().value().toJson(value))
				.parse())
		);

		return getLang().dumpCacheTemplate
			.parse("cache", databaseLoader.getCache().getCachedObjectClass().getSimpleName())
			.parse("entries", String.join("\n", cacheEntries))
			.parse("count", String.valueOf(databaseLoader.getCache().size()))
			.parse();
	}

	private List<String> dumpCommand(CommonCommand command) {
		return command.getFullCommandAndSubCommands().stream().map(cmd ->
			getLang().dumpCommandTemplate
				.parse("command", cmd)
				.parse()
		).toList();
	}


}
