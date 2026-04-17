package gg.mmorealms.module.analytics.velocity.manager;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;
import gg.mmorealms.module.analytics.velocity.AnalyticsVelocityModule;

public class PacketHandler implements PacketListener {
	@Override
	public void onPacketReceive(PacketReceiveEvent event) {
		User user = event.getUser();

		switch (event.getPacketType()) {
			case PacketType.Play.Client.PLAYER_POSITION -> {
				WrapperPlayClientPlayerPosition wrapperPlayClientPlayerPosition = new WrapperPlayClientPlayerPosition(event);
				Vector3d position = wrapperPlayClientPlayerPosition.getPosition();
				onUserMove(user, position.getX(), position.getY(), position.getZ(), null, null);
			}
			case PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION -> {
				WrapperPlayClientPlayerPositionAndRotation wrapperPlayClientPlayerPositionAndRotation = new WrapperPlayClientPlayerPositionAndRotation(event);
				Vector3d position = wrapperPlayClientPlayerPositionAndRotation.getPosition();
				onUserMove(user, position.getX(), position.getY(), position.getZ(), wrapperPlayClientPlayerPositionAndRotation.getYaw(), wrapperPlayClientPlayerPositionAndRotation.getPitch());
			}
			case PacketType.Play.Client.PLAYER_ROTATION -> {
				WrapperPlayClientPlayerRotation wrapperPlayClientPlayerRotation = new WrapperPlayClientPlayerRotation(event);
				onUserMove(user, null, null, null, wrapperPlayClientPlayerRotation.getYaw(), wrapperPlayClientPlayerRotation.getPitch());
			}
			default -> {

			}
		}
	}

	public void onUserMove(User user, Double x, Double y, Double z, Float yaw, Float pitch) {
		if (x != null && y != null && z != null) {
			AnalyticsVelocityModule.instance().getAfkManager().recordMovement(user.getUUID(), x, y, z);
		}

		if (yaw != null && pitch != null) {
			AnalyticsVelocityModule.instance().getAfkManager().recordMouseMovement(user.getUUID(), yaw, pitch);
		}
	}
}