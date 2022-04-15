package io.github.saltyseadoggo.blazingdepths.features.config;

//Relevant tutorial: https://fabricmc.net/wiki/tutorial:features

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BigRootFeatureConfig(BlockStateProvider wartBlock, BlockStateProvider rootsBlock,
                                   IntProvider spreadWidth, IntProvider spreadHeight) implements FeatureConfig {
    public static final Codec<BigRootFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("wart_block").forGetter(BigRootFeatureConfig::wartBlock),
            BlockStateProvider.TYPE_CODEC.fieldOf("roots_block").forGetter(BigRootFeatureConfig::rootsBlock),
            IntProvider.VALUE_CODEC.fieldOf("spread_width").forGetter(BigRootFeatureConfig::spreadWidth),
            IntProvider.VALUE_CODEC.fieldOf("spread_height").forGetter(BigRootFeatureConfig::spreadHeight)
    ).apply(instance, instance.stable(BigRootFeatureConfig::new)));
}
