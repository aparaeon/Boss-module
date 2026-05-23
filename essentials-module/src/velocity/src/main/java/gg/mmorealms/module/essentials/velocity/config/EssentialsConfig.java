package gg.mmorealms.module.essentials.velocity.config;

import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.essentials.velocity.dto.WhitelistState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EssentialsConfig {

	public WhitelistState defaultWhitelistState = new WhitelistState(
			true,
			new ArrayList<>(),
			new ArrayList<>(),
			new HashSet<>(){{
				add("_LightDream");
				add("LightDreamDev");
				add("Phantommmm");
				add("Alkatraz090");
				add("AlkatrazDev");
				add("ZeroDelusions");
				add("TouchMe");
			}}
	);

	// https://webui.advntr.dev/
	public MessageBuilderList motd = new MessageBuilderList(List.of(
			"<reset>          <b><gold>⚡ <gradient:#ff0000:white:aqua>ᴄᴏʙʙʟᴇᴍᴏɴ</gradient> <gold>ᴍᴍᴏ ʀᴇᴀʟᴍs ⚡<reset>",
			"<reset>                          <#ff0000><b>- Beta -"
	));

}
