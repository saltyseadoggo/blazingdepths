package io.github.saltyseadoggo.blazingdepths.features.config;

//Relevant tutorial: https://fabricmc.net/wiki/tutorial:features

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BigRootConfig(BlockStateProvider surfaceBlock, BlockStateProvider underBlock) implements FeatureConfig {
    public static final Codec<BigRootConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("wart_block").forGetter(BigRootConfig::surfaceBlock),
            BlockStateProvider.TYPE_CODEC.fieldOf("roots_block").forGetter(BigRootConfig::underBlock)
    ).apply(instance, instance.stable(BigRootConfig::new)));
}
