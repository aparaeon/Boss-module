package gg.mmorealms.module.breeding.backend.fabric.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerMovedEvent;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.dto.event.PokemonEggCanHatchEvent;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingComponents;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingItems;
import gg.mmorealms.module.breeding.backend.fabric.utils.PokemonEggUtils;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;


public class Listener {

    @EventHandler
    public void onPlayerMovedEvent(PlayerMovedEvent event) {
        ServerPlayer player = event.getPlayer();

        float distance = computeEffectiveDistance(player, event.getDistance());

        player.getInventory().compartments.stream()
                .flatMap(List::stream)
                .forEach(stack -> handleEggStack(player, distance, stack));
    }

    @EventHandler
    public void onPokemonEggCanHatchEvent(PokemonEggCanHatchEvent event) {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();

        UUID playerUUID = event.getPlayer().getUUID();
        IUser user = IUser.getByUUID(playerUUID);

        String eggName = event.getItemStack().getDisplayName().getString();

        user.sendMessage(config.lang.canHatchMessage
                .parse("pokemonEgg", eggName));
    }


    private static Float computeEffectiveDistance(ServerPlayer player, double rawDistance) {
        return (float) rawDistance * (PokemonEggUtils.hasEggCycleBoostAbility(player) ? 2f : 1f);
    }

    private static void handleEggStack(ServerPlayer player, float distance, ItemStack itemStack) {
        if (!itemStack.is(BreedingItems.POKEMON_EGG)) {
            return;
        }

        Float steps = itemStack.get(BreedingComponents.STEPS),
                stepsGoal = itemStack.get(BreedingComponents.STEPS_GOAL);

        if (steps == null || stepsGoal == null) {
            return;
        }

        boolean canHatch = steps >= stepsGoal;
        if (canHatch) {
            return;
        }

        float newSteps = steps + distance;
        itemStack.set(BreedingComponents.STEPS, newSteps);

        if (newSteps >= stepsGoal) {
            new PokemonEggCanHatchEvent(player, itemStack).fireAsync();
        }
    }

}
