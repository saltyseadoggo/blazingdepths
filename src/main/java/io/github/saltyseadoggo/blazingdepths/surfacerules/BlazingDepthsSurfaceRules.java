package io.github.saltyseadoggo.blazingdepths.surfacerules;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public class BlazingDepthsSurfaceRules {
    
    public static final MaterialRules.MaterialRule SEARED_DUNES_SURFACE =
            //Only apply this rule to the Seared Dunes biome
        MaterialRules.condition(MaterialRules.biome(BlazingDepthsBiomes.SEARED_DUNES_KEY),
        MaterialRules.sequence (
            MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, MaterialRules.block(Blocks.RED_SAND.getDefaultState())),
            MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH, MaterialRules.block(Blocks.RED_SANDSTONE.getDefaultState()))
        )
    );
}
