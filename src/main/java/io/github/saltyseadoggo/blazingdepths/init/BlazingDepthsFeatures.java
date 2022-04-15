package io.github.saltyseadoggo.blazingdepths.init;

import io.github.saltyseadoggo.blazingdepths.features.*;
import io.github.saltyseadoggo.blazingdepths.features.config.BigRootFeatureConfig;
import io.github.saltyseadoggo.blazingdepths.features.config.DuneFeatureConfig;
import io.github.saltyseadoggo.blazingdepths.features.config.FloatingSandFixerConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlazingDepthsFeatures {

    //A 'feature' is an arrangement of blocks constructed by code.

    //A 'configured feature' takes the 'feature' and provides values for whatever config variables the 'feature' accepts.
    //When we make our own feature, we get to decide what config variables it uses by making a WhateverFeatureConfig file.
    //We also decide how to make use of those config values in the logic used to build the feature.

    //A 'placed feature' takes a 'configured feature' and adds different 'placement modifiers' which define how it spawns in the world.
    //Another word for a 'placed feature' could be 'generated feature' because it's what actually gets generated in the world.
    //Every 'placement modifier' has different options, and it's some few of them which is necessary for them to spawn correctly and nice looking

    //Set the minimum and maximum heights of the dunes.
    static UniformIntProvider duneHeight = UniformIntProvider.create(2, 7);
    //Do the same for length (the longer side)...
    static UniformIntProvider duneLength = UniformIntProvider.create(18, 24);
    //... and the width (the shorter side).
    static UniformIntProvider duneWidth = UniformIntProvider.create(10, 14);

    //Features
    //Feature formatting can be referenced from Feature.class
    public static final Feature<DuneFeatureConfig> DUNE_FEATURE =
            registerFeature("nether_dune_feature", new NetherDuneFeature(DuneFeatureConfig.CODEC.stable()));
    public static final Feature<FloatingSandFixerConfig> FLOATING_SAND_FIXER_FEATURE =
            registerFeature("floating_sand_fixer_feature", new FloatingSandFixerFeature(FloatingSandFixerConfig.CODEC.stable()));
    public static final Feature<BigRootFeatureConfig> BIG_ROOT_FEATURE =
            registerFeature("big_root_feature", new BigRootFeature(BigRootFeatureConfig.CODEC.stable()));

    //Configured Features
    //Configured feature formatting can be referenced from NetherConfiguredFeatures.class, or any other ___ConfiguredFeatures.class
    public static final RegistryEntry<ConfiguredFeature<DuneFeatureConfig, ?>> SEARED_DUNE =
            registerCFeature("seared_dune", DUNE_FEATURE, new DuneFeatureConfig(
                    BlockStateProvider.of(BlazingDepthsBlocks.SEARED_SAND.getDefaultState()),
                    BlockStateProvider.of(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState()),
                    duneHeight, duneLength, duneWidth));
    public static final RegistryEntry<ConfiguredFeature<FloatingSandFixerConfig, ?>> FLOATING_SEARED_SAND_FIXER =
            registerCFeature("floating_seared_sand_fixer", FLOATING_SAND_FIXER_FEATURE, new FloatingSandFixerConfig(
                    BlockStateProvider.of(BlazingDepthsBlocks.SEARED_SAND.getDefaultState()),
                    BlockStateProvider.of(BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState())));
    public static final RegistryEntry<ConfiguredFeature<BigRootFeatureConfig, ?>> BIG_CRIMSON_ROOT =
            registerCFeature("big_crimson_root", BIG_ROOT_FEATURE, new BigRootFeatureConfig(
                    BlockStateProvider.of(Blocks.NETHER_WART_BLOCK.getDefaultState()),
                    BlockStateProvider.of(Blocks.CRIMSON_ROOTS.getDefaultState())));

    //Placed Features
    public static final RegistryEntry<PlacedFeature> SEARED_DUNE_PLACED =
            //Placement modifiers copied from the huge crimson fungus, see TreePlacedFeatures
            registerPFeature("seared_dune", SEARED_DUNE, CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> FLOATING_SEARED_SAND_FIXER_PLACED =
            //This feature does not need placement configs; the default generating once per chunk is fine
            registerPFeature("floating_seared_sand_fixer", FLOATING_SEARED_SAND_FIXER);
    public static final RegistryEntry<PlacedFeature> BIG_CRIMSON_ROOT_PLACED =
            //Placement modifiers copied from the huge crimson fungus, see TreePlacedFeatures
            registerPFeature("big_crimson_root", BIG_CRIMSON_ROOT, CountMultilayerPlacementModifier.of(1), BiomePlacementModifier.of());

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