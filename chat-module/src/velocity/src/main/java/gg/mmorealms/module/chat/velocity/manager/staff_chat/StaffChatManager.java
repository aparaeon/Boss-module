package gg.mmorealms.module.chat.velocity.manager.staff_chat;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.config.StaffChatConfig;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StaffChatManager {

	private final List<Player> onlineStaffMembers = Collections.synchronizedList(new ArrayList<>());
	private final List<UUID> activeStaffChat = Collections.synchronizedList(new ArrayList<>());
	private final VelocityMiniMessageManager miniMessageManager;
	private final StaffChatConfig config;

	public StaffChatManager() {
		this.miniMessageManager = ChatVelocityModule.instance().getMiniMessageManager();
		this.config = ChatVelocityModule.instance().getStaffChatConfig();
	}

	public synchronized void onStaffJoin(Player player) {
		if (!onlineStaffMembers.contains(player)) {
			onlineStaffMembers.add(player);
		}
	}

	public void onStaffLeave(Player player) {
		onlineStaffMembers.remove(player);
		activeStaffChat.remove(player.getUniqueId());
	}

	public void toggleStaffChat(Player player) {
		UUID playerUUID = player.getUniqueId();

		if (activeStaffChat.contains(playerUUID)) {
			activeStaffChat.remove(playerUUID);
		} else {
			activeStaffChat.add(playerUUID);
		}
	}

	public boolean isInStaffChat(Player player) {
		return activeStaffChat.contains(player.getUniqueId());
	}

	public void sendStaffChatMessage(Player sender, String message) {
		sendMessage(this.miniMessageManager.parse(
			config.staffChatMessageFormat
				.parse("short-rank", this.getShortRank(sender))
				.parse("username", sender.getUsername())
				.parse("message", message)
		));
	}

	public void sendMessage(String message) {
		sendMessage(this.miniMessageManager.parse(message));
	}

	public void sendMessage(Component message) {
		this.onlineStaffMembers.removeIf(player -> {
			if (!player.isActive()) {
				return true;
			}
			if (!player.hasPermission(ChatVelocityModule.STAFF_CHAT_PERMISSION)) {
				return true;
			}
			return false;
		});

		for (Player staffMember : onlineStaffMembers) {
			if (!staffMember.isActive()) {
				continue;
			}

			staffMember.sendMessage(message);
		}
	}

	private String getShortRank(Player player) {
		return LuckPermsUtils.getMetaValue(player.getUniqueId(), "short_prefix");
	}

}
