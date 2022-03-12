package io.github.saltyseadoggo.blazingdepths.terrablender;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import com.mojang.datafixers.util.Pair;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.surfacerules.BlazingDepthsSurfaceRules;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import terrablender.api.*;
import terrablender.worldgen.TBSurfaceRuleData;

import java.util.function.Consumer;

public class BlazingDepthsTerraBlenderImpl implements TerraBlenderApi {
    //As per Terra Blender's documentation, all of its shit has to be done in one class.
    //See: https://github.com/Glitchfiend/TerraBlender/wiki/Getting-started

    @Override
    public void onTerraBlenderInitialized() {
        //Register our biome provider containing our biomes
        Regions.register(new BlazingDepthsBiomeProvider());
        //Add our surface rules
        SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.NETHER, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK,
                1, BlazingDepthsSurfaceRules.makeRules());
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, "blazing_depths",
                TBSurfaceRuleData.nether());
    }

    //Class in a class? I didn't know this was possible but this is how the Terra Blender docs say to do it.
    public static class BlazingDepthsBiomeProvider extends Region {
        //The Terra Blender documentation uses the Mojang mappings class name ResourceLocation. With Yarn, it is Identifier.
        public BlazingDepthsBiomeProvider() {
            super(new Identifier(BlazingDepths.MOD_ID, "biome_provider"), RegionType.NETHER, 10);
        }

        //ResourceKey in the Terra Blender docs example is a Mojang mappings name; its Yarn name is RegistryKey.
        @Override
        public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {
            this.addBiome(mapper,
                    //Temperature, humidity (unused by Nether biomes), continentalness (unused by Nether biomes)
                    MultiNoiseUtil.ParameterRange.of(0.3F), MultiNoiseUtil.ParameterRange.of(0.0F), MultiNoiseUtil.ParameterRange.of(0.0F),
                    //Erosion, weirdness, depth (all unused by Nether biomes)
                    MultiNoiseUtil.ParameterRange.of(0.0F), MultiNoiseUtil.ParameterRange.of(0.0F), MultiNoiseUtil.ParameterRange.of(0.0F),
                    0.0F, BlazingDepthsBiomes.SEARED_DUNES_KEY);
        }
    }
}