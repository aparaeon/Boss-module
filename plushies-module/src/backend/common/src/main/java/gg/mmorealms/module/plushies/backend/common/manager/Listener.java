package gg.mmorealms.module.plushies.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerAttackEntityEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.PlayerBlockInteractEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.block_user.PlayerBlockPlaceEvent;
import gg.mmorealms.module.core.backend.common.utils.InventoryUtils;
import gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule;
import gg.mmorealms.module.plushies.backend.common.utils.PlushieUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class Listener {

	@EventHandler(order = 10)
	public void onPlayerBlockInteractEvent(PlayerBlockInteractEvent event) {
		if (!event.getResultBoolean()) {
			return;
		}

		handlePlushieSpawn(event.getUseItem(), event.getHitLocation(), event.getPlayer());
	}

	@EventHandler(order = 10)
	public void onPlayerBlockPlaceEvent(PlayerBlockPlaceEvent event) {
		if (!event.getResultBoolean()) {
			return;
		}

		handlePlushieSpawn(event.getUseItem(), event.getHitLocation(), event.getPlayer());
	}

	@EventHandler(order = 1_000_000)
	public void onPlayerAttackEntityEvent(PlayerAttackEntityEvent event) {
		if (!event.getResultBool()) {
			return;
		}

		ServerPlayer player = event.getPlayer();
		Entity entity = event.getEntity();

		if (!entity.getTags().contains(PlushiesBackendModule.PLUSHIE_TAG)) {
			return;
		}

		IPokemon pokemon = PokemonBackendModule.instance().getPlatformImplementation().getPokemonFromEntity(entity);

		if (pokemon == null) {
			return;
		}

		if (InventoryUtils.hasFullInventory(event.getPlayer())) {
			event.setResult(false);
			return;
		}

		if (pokemon.getAspects().contains("shiny")) {
			pokemon.setShiny(true);
		}

		player.addItem(PlushieUtils.createPlushieItemStack(pokemon));
		entity.remove(Entity.RemovalReason.KILLED);

		event.setResult(false);
	}

	public static void handlePlushieSpawn(ItemStack handItem, Vec3 pos, ServerPlayer player) {
		IPokemon pokemon = PokemonBackendModule.instance().getPlatformImplementation().getPokemonFromItem(handItem);

		if (pokemon == null) {
			return;
		}

		PlushieUtils.spawnPlushie(player, pos, pokemon);
		handItem.setCount(0);
	}


}
