package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.features.config.FloatingSandFixerConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import com.mojang.serialization.Codec;

public class FloatingSandFixerFeature extends Feature<FloatingSandFixerConfig> {

	//This class finds floating sand blocks in the biome and removes them.

	public FloatingSandFixerFeature(Codec<FloatingSandFixerConfig> configCodec) {
        super(configCodec);
    }

	@Override
	public boolean generate(FeatureContext<FloatingSandFixerConfig> featureContext) {

		//Grab the feature config, which we need to get the blocks from
		FloatingSandFixerConfig config = featureContext.getConfig();
		//Get the dimension this feature is generating in (in regular gameplay, this will always be the Nether)
		StructureWorldAccess world = featureContext.getWorld();
		//Get the sea level, one block below which we stop generating dunes.
		//TODO: Find a replacement for world.getSeaLevel that returns the Nether's sea level
		int netherSeaLevel = 30; //world.getSeaLevel();
		//Get the coordinate at which Nether caves stop generating, above which we do not generate dunes.
		//TODO: Get this via a method for datapack/Amplified Nether compat
		int netherCaveCutoffPoint = 120;
		//Initialize the BlockPos we'll be using
		BlockPos.Mutable pos = new BlockPos.Mutable();
		//Store the surfaceBlock and underBlock from the config
		BlockState surfaceBlock = config.surfaceBlock().getBlockState(featureContext.getRandom(), pos);
		BlockState underBlock = config.underBlock().getBlockState(featureContext.getRandom(), pos);

		//We loop through the x and z coordinates, as we need to check all 64 columns in the chunk.
		//We then loop through the relevant range of y coordinates in each column.
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for(int y = netherSeaLevel; y < netherCaveCutoffPoint; y++) {
					//Store the position we're checking
					pos.set(featureContext.getOrigin()).move(x, y, z);

					//If the block at the position we're checking is the surfaceBlock, we proceed.
					//Surface rules run before features, so, by checking for the block our rule places on the surface only, we find the surface.~
					if (world.getBlockState(pos) == surfaceBlock) {

						//If there's air under the surface block we found, replace it with the under block. This stops floating sand from appearing.
						if (world.getBlockState(new BlockPos(pos.getX(), y-1, pos.getZ())).isAir()) {
							world.setBlockState(pos, underBlock, 3);
						}
					}
				}
			}
		}
		//Returning false is used to fail feature generation. This feature doesn't have any invalid generation places, however,
		//so we just have this here to prevent an error from appearing.
		return false;
	}
}