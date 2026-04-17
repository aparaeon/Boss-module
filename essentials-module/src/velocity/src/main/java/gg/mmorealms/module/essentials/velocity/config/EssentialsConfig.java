package gg.mmorealms.module.essentials.velocity.config;

import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.util.ArrayList;
import java.util.List;

public class EssentialsConfig {

	public boolean whitelistEnabled = true;
	public List<String> whitelist = List.of(
			"_LightDream",
			"LightDreamDev",
			"Phantommmm",
			"Alkatraz090",
			"AlkatrazDev",
			"ZeroDelusions",
			"TouchMe" // Alt account of ZeroDelusions
	);

	// https://webui.advntr.dev/
	public MessageBuilderList motd = new MessageBuilderList(List.of(
			"<reset>          <b><gold>⚡ <gradient:#ff0000:white:aqua>ᴄᴏʙʙʟᴇᴍᴏɴ</gradient> <gold>ᴍᴍᴏ ʀᴇᴀʟᴍs ⚡<reset>",
			"<reset>                          <#ff0000><b>- Beta -"
	));

	public void addToWhitelist(String name) {
		if (!ArrayList.class.isAssignableFrom(whitelist.getClass())) {
			whitelist = new ArrayList<>(whitelist);
		}
		whitelist.add(name);
	}

}
