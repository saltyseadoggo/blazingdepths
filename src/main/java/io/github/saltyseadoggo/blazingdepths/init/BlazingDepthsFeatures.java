package io.github.saltyseadoggo.blazingdepths.init;

import io.github.saltyseadoggo.blazingdepths.features.DuneFeatureConfig;
import io.github.saltyseadoggo.blazingdepths.features.NetherDuneFeature;
import io.github.saltyseadoggo.blazingdepths.features.NonProtectedSimpleBlockStateProvider;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class BlazingDepthsFeatures {

    //A 'feature' is an arrangement of blocks constructed by code.

    //A 'configured feature' takes the 'feature' and provides values for whatever config variables the 'feature' accepts.
    //When we make our own feature, we get to decide what config variables it uses by making a WhateverFeatureConfig file.
    //We also decide how to make use of those config values in the logic used to build the feature.

    //A 'placed feature' takes a 'configured feature' and adds different 'placement modifiers' which define how it spawns in the world.
    //Another word for a 'placed feature' could be 'generated feature' because it's what actually gets generated in the world.
    //Every 'placement modifier' has different options, and it's some few of them which is necessary for them to spawn correctly and nice looking

    //Features
    //Feature formatting can be referenced from Feature.class
    public static final Feature<DuneFeatureConfig> DUNE_FEATURE =
            registerFeature("nether_dune_feature", new NetherDuneFeature(DuneFeatureConfig.CODEC.stable()));

    //Configured Features
    //Configured feature formatting can be referenced from NetherConfiguredFeatures.class, or any other ___ConfiguredFeatures.class
    public static final RegistryEntry<ConfiguredFeature<DuneFeatureConfig, ?>> SEARED_DUNE =
            registerCFeature("seared_dune", DUNE_FEATURE, new DuneFeatureConfig(
                    new NonProtectedSimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SAND.getDefaultState()),
                    new NonProtectedSimpleBlockStateProvider(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState())));

    //Placed Features
    public static final RegistryEntry<PlacedFeature> SEARED_DUNE_PLACED =
            //Placement modifiers copied from the huge crimson fungus, see TreePlacedFeatures
            registerPFeature("seared_dune", SEARED_DUNE, CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of());

    //The registry zone
    //Contains multipurpose registration methods that can register any feature we feed them
    //registerFeature for features, registerCFeature for configured features, registerPFeature for placed features

    //Feature registering method adapted from Feature.class
    public static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature) {
        return Registry.register(Registry.FEATURE, name, feature);
    }
    //Configured feature registering method adapted from ConfiguredFeatures.class
    public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> registerCFeature(String id, F feature, FC config) {
        return BuiltinRegistries.method_40360(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature(feature, config));
    }
    //Placed feature registering methods adapted from PlacedFeatures.class
    public static RegistryEntry<PlacedFeature> registerPFeature(String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, List<PlacementModifier> modifiers) {
        return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(RegistryEntry.upcast(registryEntry), List.copyOf(modifiers)));
    }
    public static RegistryEntry<PlacedFeature> registerPFeature(String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, PlacementModifier... modifiers) {
        return registerPFeature(id, registryEntry, List.of(modifiers));
    }

    public static void init() {}
}