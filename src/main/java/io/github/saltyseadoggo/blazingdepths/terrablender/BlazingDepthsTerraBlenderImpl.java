package io.github.saltyseadoggo.blazingdepths.terrablender;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import com.mojang.datafixers.util.Pair;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.surfacerules.BlazingDepthsSurfaceRules;
import terrablender.api.*;
import terrablender.worldgen.TBSurfaceRuleData;

import java.util.function.Consumer;

    //Due to Fabric API lacking surface rule adding functionality, we need an external dependency to add surface rules.
    //Both Terra Blender and Quilt Standard Libraries can handle this. Many mods already need Terra Blender, so I chose the former for end user convenience.

public class BlazingDepthsTerraBlenderImpl implements TerraBlenderApi {
    //As per Terra Blender's documentation, all of its functions have to be done in one class.
    //See: https://github.com/Glitchfiend/TerraBlender/wiki/Getting-started

    @Override
    public void onTerraBlenderInitialized() {
        //Add our surface rules
        SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.NETHER, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK,
                1, BlazingDepthsSurfaceRules.makeRules());
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, "blazing_depths",
                TBSurfaceRuleData.nether());
    }
}