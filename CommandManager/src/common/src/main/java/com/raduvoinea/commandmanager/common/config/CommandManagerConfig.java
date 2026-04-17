package com.raduvoinea.commandmanager.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;

public class CommandManagerConfig {

	public String basePermission = "project.command";
	public MessageBuilder simpleUsage = new MessageBuilder("/{command} {arguments}");
	public MessageBuilder complexUsage = new MessageBuilder("""
			Usage: {simple_usage}
			
			Sub commands:
			{sub_commands}
			"""
	);

    /*
    Usage: /discord

    Sub commands:
    /discord link {code}
    /discord sync
    /discord unlink
    /discord admin unlink {user}
    /discord admin {user}



     */


}
