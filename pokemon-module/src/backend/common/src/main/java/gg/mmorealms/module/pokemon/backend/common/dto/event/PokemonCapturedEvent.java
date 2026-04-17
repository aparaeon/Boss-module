package gg.mmorealms.module.pokemon.backend.common.dto.event;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class PokemonCapturedEvent extends LocalEvent {
    private final ServerPlayer player;
    private final IPokemon pokemon;
    @Nullable private final Entity entity;
}
