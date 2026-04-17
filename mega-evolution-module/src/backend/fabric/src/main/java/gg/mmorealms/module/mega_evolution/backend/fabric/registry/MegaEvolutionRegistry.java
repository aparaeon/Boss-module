package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

public class MegaEvolutionRegistry {

    private MegaEvolutionRegistry() { }

    public static void register() {
        MegaEvolutionBlockStates.register();
        MegaEvolutionBlocks.register();
        MegaEvolutionItems.register();
        MegaEvolutionShowdownItems.register();
    }

}