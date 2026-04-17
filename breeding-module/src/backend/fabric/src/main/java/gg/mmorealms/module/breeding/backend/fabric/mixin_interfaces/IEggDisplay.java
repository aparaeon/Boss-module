package gg.mmorealms.module.breeding.backend.fabric.mixin_interfaces;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;

public interface IEggDisplay {
    ElementHolder getEggDisplayHolder();

    void setEggDisplayHolder(ElementHolder eggHolder);

    HolderAttachment getEggDisplayAttachment();

    void setEggDisplayAttachment(HolderAttachment holderAttachment);

    float getBreedingChanceBonus();

    void setBreedingChanceBonus(float bonus);

    default void incrementBreedingChanceBonus() {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();
        setBreedingChanceBonus(getBreedingChanceBonus() + config.breeding.breedingChanceBonus);
    }

    int getBreedingTickCounter();

    void setBreedingTickCounter(int tick);

    default void incrementBreedingTickCounter() {
        setBreedingTickCounter(getBreedingTickCounter() + 1);
    }
}
