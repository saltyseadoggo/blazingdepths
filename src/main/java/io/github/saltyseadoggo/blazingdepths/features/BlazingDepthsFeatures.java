package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class BlazingDepthsFeatures {

    //feature
    public static final Feature<DuneFeatureConfig> DUNE_FEATURE =  new SearedDuneFeature(DuneFeatureConfig.CODEC.stable());
    //configured feature
    public static final ConfiguredFeature<?, ?> SEARED_DUNE_CONFIGURED_FEATURE = DUNE_FEATURE.configure(new DuneFeatureConfig(
        new SimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SAND.getDefaultState()),
        new SimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState())));

    public void init() {
        Registry.register(Registry.FEATURE, BlazingDepths.makeIdentifier("seared_dune_feature"), DUNE_FEATURE);
    }
    
}
