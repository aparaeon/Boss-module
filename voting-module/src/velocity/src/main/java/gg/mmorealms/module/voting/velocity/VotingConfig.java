package gg.mmorealms.module.voting.velocity;

import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.util.List;

public class VotingConfig {

	public MessageBuilderList voteCommands = new MessageBuilderList(List.of(
			"velocitybroadcast <click:open_url:'https://mmorealms.gg/vote'><hover:show_text:'<yellow>Click to vote!<newline>Help trigger a Vote Party!'><gray><green><bold>{user}</bold></green> voted & got a <gold><bold>Vote Key</bold></gold>! <green><bold>VoteParty</bold></green> in <aqua><bold>{votes_left}</bold></aqua> votes<gray>!</gray></gray></hover></click>",
			"execute_on_backend {user} crate admin give_virtual_key {user} 1 vote"
	));

	public int votePartyTarget = 1000;

	public MessageBuilderList votePartyCommands = new MessageBuilderList(List.of(
			"velocitybroadcast <newline><bold><gradient:#00ff00:#00cc66>★ VOTE PARTY TRIGGERED! ★</gradient></bold><newline><newline><gray>A <gold><bold>Legendary Pokémon</bold></gold> is attempting to spawn...</gray><newline><gray>It may <red>fail</red>... or it may <green>succeed</green>.</gray><newline><newline><yellow><bold>Stay alert — you might be the lucky one!</bold></yellow><newline><newline>",
			"legendaryspawn withPlayers"
	));

}
