package gg.mmorealms.module.modpack_rewards.backend.common.config;


import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.util.List;

public class ModpackRewardsConfig {

	public List<String> allowedModpacks = List.of(
			"modrinth"
	);

	public MessageBuilderList modpackCommands = new MessageBuilderList(List.of(
			"say {user} [Rejoin] Thanks for using our `{modpack}` modpack!"
	));

	public MessageBuilderList nonModpackCommands = new MessageBuilderList(List.of(
			"say {user}, why you not using our `{modpack}` modpack anymore?"
	));


}
