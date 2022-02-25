package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;

public class BlazingDepthsFeatures {

    //A 'feature' is an arrangement of blocks constructed by code.

    //A 'configured feature' takes the 'feature' and provides values for whatever config variables the 'feature' accepts.
    //When we make our own feature, we get to decide what config variables it uses by making a WhateverFeatureConfig file.
    //We also decide how to make use of those config values in the logic used to build the feature.

    //A 'placed feature' takes a 'configured feature' and adds different 'placement modifiers' which define how it spawns in the world.
    //Another word for a 'placed feature' could be 'generated feature' because it's what actually gets generated in the world.
    //Every 'placement modifier' has different options, and it's some few of them which is necessary for them to spawn correctly and nice looking

    //feature
    public static final Feature<DuneFeatureConfig> DUNE_FEATURE =  new SearedDuneFeature(DuneFeatureConfig.CODEC.stable());
    //configured feature
    public static final ConfiguredFeature<?, ?> SEARED_DUNE_FEATURE = DUNE_FEATURE.configure(new DuneFeatureConfig(
        new NonProtectedSimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SAND.getDefaultState()),
        new NonProtectedSimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState())
    ));
    //placed feature
    public static final PlacedFeature SEARED_DUNE_PLACED_FEATURE = SEARED_DUNE_FEATURE.withPlacement();

    public static void init() {
        //feature
        Registry.register(Registry.FEATURE, BlazingDepths.makeIdentifier("dune_feature"), DUNE_FEATURE);
        //configured feature
        RegistryKey<ConfiguredFeature<?, ?>> SEARED_DUNE_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, BlazingDepths.makeIdentifier("seared_dune_feature"));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, SEARED_DUNE_FEATURE_KEY.getValue(), SEARED_DUNE_FEATURE);
        //placed feature
        RegistryKey<PlacedFeature> SEARED_DUNE_PLACED_FEATURE_KEY = RegistryKey.of(Registry.PLACED_FEATURE_KEY, BlazingDepths.makeIdentifier("seared_dune_feature"));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, SEARED_DUNE_PLACED_FEATURE_KEY.getValue(), SEARED_DUNE_PLACED_FEATURE);
    }
    
}
