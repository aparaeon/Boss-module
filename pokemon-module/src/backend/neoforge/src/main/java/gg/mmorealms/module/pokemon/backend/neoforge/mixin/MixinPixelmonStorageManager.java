package gg.mmorealms.module.pokemon.backend.neoforge.mixin;

import com.pixelmonmod.pixelmon.api.economy.BankAccountManager;
import com.pixelmonmod.pixelmon.api.storage.*;
import com.raduvoinea.utils.logger.Logger;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(value = PixelmonStorageManager.class, remap = false)
public abstract class MixinPixelmonStorageManager implements StorageManager, BankAccountManager {

	@Shadow
	@Final
	protected Map<UUID, PlayerPartyStorage> parties;

	@Shadow
	@Final
	protected Map<UUID, PCStorage> pcs;

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer)) return;
		UUID uuid = event.getEntity().getUUID();
		if (this.parties.containsKey(uuid)) {
			this.parties.get(uuid).tryUpdatePlayerName();
		} else {
			getParty(uuid);
		}
		if (this.pcs.containsKey(uuid)) {
			PCStorage pc = this.pcs.get(uuid);
			pc.setPlayer(uuid, event.getEntity().getName().getString());
			for (ServerPlayer player : pc.trackingPlayers()) {
				pc.initialize(player);
			}
		} else {
			getPCForPlayer(uuid);
		}
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerLoad(PlayerEvent.LoadFromFile event) {
		if (!(event.getEntity() instanceof ServerPlayer)) {
			return;
		}

		UUID uuid = event.getEntity().getUUID();
		PlayerPartyStorage partyStorage = StorageProxy.getSaveAdapter().load(uuid, PlayerPartyStorage.class, ServerLifecycleHooks.getCurrentServer().registryAccess()).join();
		if (partyStorage != null) {
			this.parties.put(uuid, partyStorage);
			Logger.debug("Loaded party for " + uuid);
		}

		PCStorage pc = StorageProxy.getSaveAdapter().load(uuid, PCStorage.class, ServerLifecycleHooks.getCurrentServer().registryAccess()).join();
		if (pc != null) {
			this.pcs.put(uuid, pc);
			Logger.debug("Loaded PC for " + uuid);
		}
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		UUID playerUUID = event.getEntity().getUUID();
		PlayerPartyStorage party = this.parties.get(event.getEntity().getUUID());
		PCStorage pc = this.pcs.get(event.getEntity().getUUID());

		if (party != null) {
			StorageProxy.getSaveAdapter().save(party, ServerLifecycleHooks.getCurrentServer().registryAccess());
			Logger.debug("Saved party for " + playerUUID);
		}

		if (pc != null) {
			StorageProxy.getSaveAdapter().save(pc, ServerLifecycleHooks.getCurrentServer().registryAccess());
			Logger.debug("Saved PC for " + playerUUID);
		}
	}
}
