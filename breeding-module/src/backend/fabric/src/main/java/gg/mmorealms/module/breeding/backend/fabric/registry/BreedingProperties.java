package gg.mmorealms.module.breeding.backend.fabric.registry;

import net.minecraft.world.level.block.state.properties.IntegerProperty;


public class BreedingProperties {

    private BreedingProperties() { }

    public static IntegerProperty AVERAGE_IVS = IntegerProperty.create(BreedingDataKeys.AVERAGE_IVS_KEY, 0, 31);

}
