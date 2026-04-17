package gg.mmorealms.module.mega_evolution.backend.fabric.interfaces;

public interface IMegaPokemonEntity {

    boolean isMegaEvolving();

    void mega_evolution$addMegaEvolutionLock();

    void mega_evolution$removeMegaEvolutionLock();

    void mega_evolution$setMegaEvolutionLock(boolean locked);

}
