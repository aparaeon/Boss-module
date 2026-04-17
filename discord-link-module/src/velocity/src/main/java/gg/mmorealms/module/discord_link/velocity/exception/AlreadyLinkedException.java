package gg.mmorealms.module.discord_link.velocity.exception;

public class AlreadyLinkedException extends Exception {

	public AlreadyLinkedException() {
		super("Your account is already linked to a discord account"); // TODO Config
	}

}
