package gg.mmorealms.module.breeding.backend.fabric.dto.event;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@Getter
public class PokemonEggCanHatchEvent extends LocalEvent {

    private final ServerPlayer player;
    private final ItemStack itemStack;

    public PokemonEggCanHatchEvent(ServerPlayer player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

}
