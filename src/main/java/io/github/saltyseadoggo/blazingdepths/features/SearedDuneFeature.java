package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.noise.OpenSimplexNoise;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import com.mojang.serialization.Codec;

public class SearedDuneFeature extends Feature<DuneFeatureConfig> {

	//Fetch a noise map from OpenSimplexNoise with the given seed and store it
	//(Seed comes from 1.16 Terrestria's dune surface builder, like much of this code)
	private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(3445);
	//Set the approximate maximum height of the dunes, to be used for setting `height` when building collumns based on the noise
	//1.16 Terrestria dunes biome's value is 30
	int duneMaxHeight = 10;
	//Set the x and z scale applied to the noise map, which affects the horizontal size of the dunes.
	//1.16 Terrestria dunes biome's values are 0.01 and 0.015, respectively

	double noiseMapXScale = 0.1;
	double noiseMapZScale = 0.15;
	//Set the y value below which dunes are not formed. Currently set to one less than the Nether's sea level of 31
	int noDunesBelowY = 30;

	public SearedDuneFeature(Codec<DuneFeatureConfig> configCodec) {
        super(configCodec);
    }

	@Override
	public boolean generate(FeatureContext<DuneFeatureConfig> featureContext) {

		DuneFeatureConfig config = featureContext.getConfig();
		StructureWorldAccess world = featureContext.getWorld();
		BlockPos.Mutable checkingPos = new BlockPos.Mutable();
		BlockPos.Mutable duneBuilderPos;

		for (int xMove = 0; xMove < 16; xMove++) {
			for (int zMove = 0; zMove < 16; zMove++) {
				//Loop through all y coordinates and build dune columns on top of each seared sand placed by the surface rule
				for(int y = 120; y > noDunesBelowY; --y) {
					checkingPos.set(featureContext.getOrigin()).move(xMove, y, zMove);
					//If it detects seared sand placed by surface rule at checkingPos, we build a dune column on top
					if (world.getBlockState(checkingPos) == config.underBlock().getBlockState(featureContext.getRandom(), checkingPos)) {
						//If there's air under the current sand block, replace the current sand block with sandstone to prevent floating sand
						if (world.getBlockState(new BlockPos(xMove, y-1, zMove)).isAir()) {
							//This block placing line is from the Fabric feature tutorial, and it is extremely stupid.
							//I don't understand why I need a fucking random to place a block, or what 'flags' means.
							world.setBlockState(checkingPos, config.underBlock().getBlockState(featureContext.getRandom(), checkingPos), 3);
						}

						duneBuilderPos = checkingPos;
						//Gets the height for the given x/z column based on the OpenSimplexNoise map
						double height = (NOISE.sample(xMove * noiseMapXScale , zMove * noiseMapZScale) * duneMaxHeight);
							height = Math.abs(height);
							height = Math.min(height, (NOISE.sample(xMove * 0.03 + 5 , zMove * 0.05 + 5) * duneMaxHeight + 6));

						//The height loop forms the dunes.
						for (int h = 0; h < height; h++) {
							duneBuilderPos.move(Direction.UP);
							//This block placing line is from the Fabric feature tutorial, and it is extremely stupid.
							//I don't understand why I need a fucking random to place a block, or what 'flags' means.
							world.setBlockState(duneBuilderPos, config.surfaceBlock().getBlockState(featureContext.getRandom(), duneBuilderPos), 3);
						}
					}
				}
			}
		}
		//Some BYG features had this set to true, but their dune feature had it set to false. I need to figure out what it means.
		return false;
	}

}