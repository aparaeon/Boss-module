package gg.mmorealms.module.breeding.backend.fabric.registry;

import com.cobblemon.mod.common.pokemon.Gender;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.mmorealms.module.breeding.backend.fabric.dto.ParentData;

public class BreedingCodecs {

    private BreedingCodecs() {
    }

    public static final Codec<ParentData> PARENT_DATA_CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.STRING.fieldOf("name").forGetter(ParentData::name),
                    Gender.getCODEC().fieldOf("gender").forGetter(ParentData::gender)
            ).apply(inst, ParentData::new)
    );

}
