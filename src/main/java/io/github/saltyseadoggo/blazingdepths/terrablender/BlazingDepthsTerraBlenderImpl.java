package io.github.saltyseadoggo.blazingdepths.terrablender;

import io.github.saltyseadoggo.blazingdepths.surfacerules.BlazingDepthsSurfaceRules;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import terrablender.api.TerraBlenderApi;
import terrablender.worldgen.TBSurfaceRuleData;

import java.util.Optional;

public class BlazingDepthsTerraBlenderImpl implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {}

    @Override
    public Optional<MaterialRules.MaterialRule> getDefaultNetherSurfaceRules() {
        return Optional.of(MaterialRules.sequence(BlazingDepthsSurfaceRules.makeRules(), TBSurfaceRuleData.nether()));
    }
}