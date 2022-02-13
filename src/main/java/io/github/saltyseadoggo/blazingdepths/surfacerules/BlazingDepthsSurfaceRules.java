package io.github.saltyseadoggo.blazingdepths.surfacerules;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public class BlazingDepthsSurfaceRules {
    
    public static MaterialRules.MaterialRule makeRules()
    {
            //If wrong biome, don't apply this surface rule
        return MaterialRules.condition(MaterialRules.biome(BlazingDepthsBiomes.SEARED_DUNES_KEY),
			MaterialRules.sequence(
                    //STONE_DEPTH_FLOOR is the Yarn version of ON_FLOOR in Mojang mappings.
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, MaterialRules.block(Blocks.RED_SAND.getDefaultState()))
            )
        );
    }
}
