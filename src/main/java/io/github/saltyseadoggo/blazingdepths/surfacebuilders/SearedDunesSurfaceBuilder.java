package io.github.saltyseadoggo.blazingdepths.surfacebuilders;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import io.github.saltyseadoggo.blazingdepths.noise.OpenSimplexNoise;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class SearedDunesSurfaceBuilder {

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

	public void placeDunes(Random rand, Chunk chunk, Biome biome, int x, int z, int vHeight, double noise,
		BlockState stone, BlockState water, int seaLevel, int i, long l) {

		BlockPos.Mutable checkingPos = new BlockPos.Mutable();
		BlockPos.Mutable duneBuilderPos = new BlockPos.Mutable();
        int y;

			//Loop through all y coordinates and build dune collumns on top of each seared sand placed by the DefaultSurfaceBuilder
        for(y = 120; y > noDunesBelowY; --y) {
            checkingPos.set(x, y, z);
            BlockState stateAtCheckingPos = chunk.getBlockState(checkingPos);
				//If it detects seared sand placed by DefaultSurfaceBuilder at checkingPos, we build a dune collumn on top
            if (stateAtCheckingPos == BlazingDepthsBlocks.SEARED_SAND.getDefaultState()) {
					//If there's air under the current sand block, replace the current sand block with sandstone to prevent floating sand
				if (chunk.getBlockState(new BlockPos(x, y-1, z)).isAir()) {
					chunk.setBlockState(checkingPos, BlazingDepthsBlocks.SEARED_SANDSTONE.getDefaultState(), false);
				}

                duneBuilderPos = checkingPos;
                    //Gets the height for the given x/z collumn based on the noise map
		    	double height = (NOISE.sample(x * noiseMapXScale , z * noiseMapZScale) * duneMaxHeight);
                   	height = Math.abs(height);
                    height = Math.min(height, (NOISE.sample(x * 0.03 + 5 , z * 0.05 + 5) * duneMaxHeight + 6));

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