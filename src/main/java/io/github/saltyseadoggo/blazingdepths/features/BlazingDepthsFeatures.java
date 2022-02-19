package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class BlazingDepthsFeatures {

    //feature
    public static final Feature<DuneFeatureConfig> DUNE_FEATURE =  new SearedDuneFeature(DuneFeatureConfig.CODEC.stable());
    //configured feature
    public static final ConfiguredFeature<?, ?> SEARED_DUNE_FEATURE = DUNE_FEATURE.configure(new DuneFeatureConfig(
        new NonProtectedSimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SAND.getDefaultState()),
        new NonProtectedSimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState())));

    public void init() {
        Identifier SEARED_DUNE_FEATURE_ID = BlazingDepths.makeIdentifier("seared_dune_feature");

        //feature
        Registry.register(Registry.FEATURE, SEARED_DUNE_FEATURE_ID, DUNE_FEATURE);
        //configured feature
        RegistryKey<ConfiguredFeature<?, ?>> SEARED_DUNE_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
        SEARED_DUNE_FEATURE_ID);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, SEARED_DUNE_FEATURE_KEY.getValue(), SEARED_DUNE_FEATURE);
    }
    
}
