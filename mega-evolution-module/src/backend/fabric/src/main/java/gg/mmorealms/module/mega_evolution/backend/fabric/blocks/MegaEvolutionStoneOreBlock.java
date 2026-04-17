package gg.mmorealms.module.mega_evolution.backend.fabric.blocks;

import gg.mmorealms.module.core.backend.fabric.blocks.PolymerStoneBlock;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionBlockStates;
import net.minecraft.world.level.block.state.BlockState;

public class MegaEvolutionStoneOreBlock extends PolymerStoneBlock {

    private final MegaEvolution evolution;

    public MegaEvolutionStoneOreBlock(MegaEvolution evolution) {
        super();
        this.evolution = evolution;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return MegaEvolutionBlockStates.MEGA_STONE_ORE_BLOCK_STATES.get(evolution);
    }

}
