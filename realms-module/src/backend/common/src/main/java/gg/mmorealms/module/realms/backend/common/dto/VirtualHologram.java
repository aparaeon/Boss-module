package gg.mmorealms.module.realms.backend.common.dto;

import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class VirtualHologram {

	private static final ExecutorService NETWORK_EXECUTOR = Executors.newSingleThreadExecutor();
	private static final AtomicInteger NEXT_ID = new AtomicInteger(-1);
	private static ServerLevel WORLD = null;
	private static final double ERROR_MARGIN = 2.0;
	private static final double MAX_DISTANCE = 200;

	private final int id;
	private UUID uuid;

	private final double x;
	private final double y;
	private final double z;
	private final float yRot;
	private final float xRot;
	private final Component text;
	private final UUID realmOwner;
	private List<SynchedEntityData.DataValue<?>> dataValues;

	public VirtualHologram(double x, double y, double z, float yRot, float xRot, Component text, UUID realmOwner) {
		this.id = NEXT_ID.decrementAndGet();
		this.uuid = UUID.randomUUID();

		this.x = x;
		this.y = y;
		this.z = z;
		this.yRot = yRot;
		this.xRot = xRot;
		this.text = text;
		this.realmOwner = realmOwner;

		if (WORLD == null) {
			WORLD = LocationUtils.getWorld();
		}

		Display.TextDisplay throwaway = new Display.TextDisplay(EntityType.TEXT_DISPLAY, WORLD);
		throwaway.setText(text);
		this.dataValues = throwaway.getEntityData().getNonDefaultValues();
		if (this.dataValues == null) {
			this.dataValues = Collections.emptyList();
		}
	}

	public ClientboundAddEntityPacket toAddEntityPacket() {
		return new ClientboundAddEntityPacket(
			this.getId(),
			this.getUuid(),
			this.getX(),
			this.getY(),
			this.getZ(),
			this.getXRot(),
			this.getYRot(),
			EntityType.TEXT_DISPLAY,
			0,
			Vec3.ZERO,
			0.0
		);
	}

	public ClientboundRemoveEntitiesPacket toRemoveEntityPacket() {
		return new ClientboundRemoveEntitiesPacket(new IntArrayList(new int[]{this.id}));
	}

	public ClientboundSetEntityDataPacket toSetEntityDataPacket() {
		return new ClientboundSetEntityDataPacket(
			this.id,
			this.dataValues
		);
	}

	public void sendTo(Collection<ServerPlayer> players) {
		NETWORK_EXECUTOR.submit(() -> {
			for (ServerPlayer player : players) {
				this.sendTo(player);
			}
		});
	}

	public void sendTo(ServerPlayer player) {
		player.connection.send(this.toAddEntityPacket());
		player.connection.send(new ClientboundSetEntityDataPacket(id, dataValues));
	}

	public void removeFrom(Collection<ServerPlayer> players) {
		NETWORK_EXECUTOR.submit(() -> {
			for (ServerPlayer player : players) {
				this.removeFrom(player);
			}
		});
	}

	public void removeFrom(ServerPlayer player) {
		player.connection.send(this.toRemoveEntityPacket());
	}

	public boolean isWithinError(double[] position) {
		return this.isWithinError(position[0], position[1]);
	}

	public boolean isWithinError(double x, double z) {
		return Math.abs(this.x - x) < 2.0 && Math.abs(this.z - z) < 2.0;
	}

	public int getDistanceSquared(double x, double z){
		double dx = this.x - x;
		double dz = this.z - z;
		return (int) (dx * dx + dz * dz);
	}
}