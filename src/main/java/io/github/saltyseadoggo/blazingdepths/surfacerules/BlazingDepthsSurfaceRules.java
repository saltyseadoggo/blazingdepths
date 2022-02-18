package io.github.saltyseadoggo.blazingdepths.surfacerules;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public class BlazingDepthsSurfaceRules {
    
    public static MaterialRules.MaterialRule makeRules() {
        //If wrong biome, don't apply this surface rule
        return MaterialRules.condition(MaterialRules.biome(BlazingDepthsBiomes.SEARED_DUNES_KEY),
			MaterialRules.sequence(
                //Place seared sand on the surface.
                //STONE_DEPTH_FLOOR is the Yarn version of ON_FLOOR in Mojang mappings.
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, MaterialRules.block(BlazingDepthsBlocks.SEARED_SAND.getDefaultState())),
                //Place seared sandstone underneath.
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH, MaterialRules.block(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState())),
                //Place seared sandstone on the ceiling.
                MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING_WITH_SURFACE_DEPTH, MaterialRules.block(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState()))
            )
        );
    }
}
