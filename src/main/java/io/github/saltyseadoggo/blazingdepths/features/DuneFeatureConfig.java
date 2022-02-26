package io.github.saltyseadoggo.blazingdepths.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

    //Relevant tutorial: https://fabricmc.net/wiki/tutorial:features

public record DuneFeatureConfig(BlockStateProvider surfaceBlock, BlockStateProvider underBlock) implements FeatureConfig {
    public static final Codec<DuneFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockStateProvider.TYPE_CODEC.fieldOf("surface_block").forGetter(DuneFeatureConfig::surfaceBlock),
        BlockStateProvider.TYPE_CODEC.fieldOf("under_block").forGetter(DuneFeatureConfig::underBlock)
    ).apply(instance, instance.stable(DuneFeatureConfig::new)));
}
