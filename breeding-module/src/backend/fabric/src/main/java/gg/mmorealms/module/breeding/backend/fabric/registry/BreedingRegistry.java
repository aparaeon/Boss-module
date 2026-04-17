package gg.mmorealms.module.breeding.backend.fabric.registry;

public class BreedingRegistry {

    private BreedingRegistry() {
    }

    public static void register() {
        BreedingBlockStates.register();

        BreedingBlocks.register();
        BreedingBlockEntities.register();

        BreedingItems.register();
    }

}
