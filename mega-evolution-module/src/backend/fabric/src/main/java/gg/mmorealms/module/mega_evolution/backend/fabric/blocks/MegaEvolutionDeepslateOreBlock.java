package gg.mmorealms.module.mega_evolution.backend.fabric.blocks;

import gg.mmorealms.module.core.backend.fabric.blocks.PolymerDeepslateBlock;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionBlockStates;
import net.minecraft.world.level.block.state.BlockState;

public class MegaEvolutionDeepslateOreBlock extends PolymerDeepslateBlock {

    private final MegaEvolution evolution;

    public MegaEvolutionDeepslateOreBlock(MegaEvolution evolution) {
        super();
        this.evolution = evolution;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return MegaEvolutionBlockStates.MEGA_DEEPSLATE_ORE_BLOCK_STATES.get(evolution);
    }

}
