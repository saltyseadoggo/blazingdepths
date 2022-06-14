package io.github.saltyseadoggo.blazingdepths.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

//Relevant tutorial: https://fabricmc.net/wiki/tutorial:features

public record FloatingSandFixerConfig(BlockStateProvider surfaceBlock, BlockStateProvider underBlock) implements FeatureConfig {
    public static final Codec<FloatingSandFixerConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("surface_block").forGetter(FloatingSandFixerConfig::surfaceBlock),
            BlockStateProvider.TYPE_CODEC.fieldOf("under_block").forGetter(FloatingSandFixerConfig::underBlock)
    ).apply(instance, instance.stable(FloatingSandFixerConfig::new)));
}
