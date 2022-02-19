package io.github.saltyseadoggo.blazingdepths.features;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class NonProtectedSimpleBlockStateProvider extends SimpleBlockStateProvider {

    //We need SimpleBlockStateProvider in BlazingDepthsFeatures.java to configure our features, as per the tutorial:
    //https://fabricmc.net/wiki/tutorial:features
    //However, Mojank decided to make SimpleBlockStateProvider protected.
    //This class extends it, and is public, so we can use SimpleBlockStateProvider 'through' this class.

    protected NonProtectedSimpleBlockStateProvider(BlockState state) {
        super(state);
    }
}
