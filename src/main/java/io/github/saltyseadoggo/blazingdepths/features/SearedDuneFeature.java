package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import io.github.saltyseadoggo.blazingdepths.noise.OpenSimplexNoise;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import com.mojang.serialization.Codec;

public class SearedDuneFeature extends Feature<DuneFeatureConfig> {

		//Fetch a noise map from TerraformAPI
	private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(3445);
		//Set the approximate maximum height above y=70 of the dunes, to be used for setting `height` when building collumns based on the noise
		//Terrestria dunes biome's value is 30
	int duneMaxHeight = 10;
		//Set the x and z scale applied to the noise map, which affects the horizontal size of the dunes.
		//Terrestria dunes biome's values are 0.01 and 0.015, respectively
	double noiseMapXScale = 0.1;
	double noiseMapZScale = 0.15;
		//Set the y value below which dunes are not formed. Currently set to one less than the Nether's sea level of 31
	int noDunesBelowY = 30;

	public SearedDuneFeature(Codec<DuneFeatureConfig> configCodec) {
        super(configCodec);
    };

	@Override
	public boolean generate(FeatureContext<DuneFeatureConfig> featureContext) {

		BlockPos.Mutable checkingPos = new BlockPos.Mutable();
		BlockPos.Mutable duneBuilderPos = new BlockPos.Mutable();

		for (int xMove = 0; xMove < 16; xMove++) {
			for (int zMove = 0; zMove < 16; zMove++) {
				//Loop through all y coordinates and build dune collumns on top of each seared sand placed by the DefaultSurfaceBuilder
				for(int y = 120; y > noDunesBelowY; --y) {
					checkingPos.set(featureContext.getOrigin()).move(xMove, 0, zMove);
					StructureWorldAccess world = featureContext.getWorld();
					Chunk chunk = world.getChunk(checkingPos);
					BlockState stateAtCheckingPos = chunk.getBlockState(checkingPos);
						//If it detects seared sand placed by DefaultSurfaceBuilder at checkingPos, we build a dune collumn on top
					if (stateAtCheckingPos == BlazingDepthsBlocks.SEARED_SAND.getDefaultState()) {
							//If there's air under the current sand block, replace the current sand block with sandstone to prevent floating sand
						if (chunk.getBlockState(new BlockPos(xMove, y-1, zMove)).isAir()) {
							chunk.setBlockState(checkingPos, BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState(), false);
						}

						duneBuilderPos = checkingPos;
							//Gets the height for the given x/z collumn based on the noise map
						double height = (NOISE.sample(xMove * noiseMapXScale , zMove * noiseMapZScale) * duneMaxHeight);
							height = Math.abs(height);
							height = Math.min(height, (NOISE.sample(xMove * 0.03 + 5 , zMove * 0.05 + 5) * duneMaxHeight + 6));

							//The height loop forms the dunes.
							//With only this loop, the resulting biome is a stony plain with shorter sand dunes coming out in places.
						for (int h = 0; h < height; h++) {
							duneBuilderPos.move(Direction.UP);
							chunk.setBlockState(duneBuilderPos, BlazingDepthsBlocks.SEARED_SAND.getDefaultState(), false);
						}
					}
				}
			}
		}
		return false;
	}
}