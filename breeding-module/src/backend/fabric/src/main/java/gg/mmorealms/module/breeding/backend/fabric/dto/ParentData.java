package gg.mmorealms.module.breeding.backend.fabric.dto;

import com.cobblemon.mod.common.pokemon.Gender;
import com.mojang.serialization.Codec;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingCodecs;

public record ParentData(String name, Gender gender) {
    public static Codec<ParentData> CODEC = BreedingCodecs.PARENT_DATA_CODEC;

    public ParentData() {
        this("???", Gender.GENDERLESS);
    }
}
