package gg.mmorealms.module.realms.backend.common.dto;

import gg.mmorealms.module.realms.backend.common.dto.realm.Realm;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class HologramsHandler {

	private final Realm owningRealm;
	private final transient List<VirtualHologram> holograms = new ArrayList<>();

	public HologramsHandler(Realm owningRealm) {
		this.owningRealm = owningRealm;
	}

	public void addHologram(double x, double y, double z, float rotY, float rotX, Component text) {
		VirtualHologram hologram = new VirtualHologram(x, y, z, rotY, rotX, text, this.owningRealm.getOwnerUUID());
		this.holograms.add(hologram);

		for (ServerPlayer player : this.owningRealm.getActivePlayers()) {
			hologram.sendTo(player);
		}
	}

	public void removeHologram(double[] position) {
		for (VirtualHologram hologram : this.holograms) {
			if (hologram.isWithinError(position)) {
				this.holograms.remove(hologram);
				hologram.removeFrom(this.owningRealm.getActivePlayers());
				break;
			}
		}
	}

	public void sendTo(ServerPlayer player){
		for (VirtualHologram hologram : this.holograms) {
			hologram.sendTo(player);
		}
	}

	public void removeFrom(ServerPlayer player){
		for (VirtualHologram hologram : this.holograms) {
			hologram.removeFrom(player);
		}
	}

}
