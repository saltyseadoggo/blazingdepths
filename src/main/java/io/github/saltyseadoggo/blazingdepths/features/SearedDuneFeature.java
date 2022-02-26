package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.noise.OpenSimplexNoise;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import com.mojang.serialization.Codec;

public class SearedDuneFeature extends Feature<DuneFeatureConfig> {

	//This class is what builds the Seared Dunes biome's namesake dunes.

	//Fetch a noise map from OpenSimplexNoise with the given seed and store it.
	//(Seed comes from 1.16 Terrestria's dune surface builder, like much of this code)
	private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(3445);
	//Set the approximate maximum height of the dunes, to be used for setting `height` when building collumns based on the noise
	//1.16 Terrestria had its dunes' height set to 30.
	int duneMaxHeight = 4;
	//Set the x and z scale applied to the noise map, which affects the horizontal size of the dunes.
	//1.16 Terrestria had these values set to 0.01 and 0.015, respectively.
	double noiseMapXScale = 0.1;
	double noiseMapZScale = 0.15;
	//Set the y value below which dunes are not formed. Currently set to one less than the Nether's sea level of 31.
	//Because the surface rule doesn't place sand under lava, no dunes would generate there even if we didn't set this. This is just for performance.
	int noDunesBelowY = 30;

	public SearedDuneFeature(Codec<DuneFeatureConfig> configCodec) {
        super(configCodec);
    }

	@Override
	public boolean generate(FeatureContext<DuneFeatureConfig> featureContext) {

		//Grab the feature config
		DuneFeatureConfig config = featureContext.getConfig();
		//Get the dimension this feature is generating in (in regular gameplay, this will always be the Nether)
		StructureWorldAccess world = featureContext.getWorld();
		//Initialize the two position variables we'll be using to build dunes.
		BlockPos.Mutable checkingPos = new BlockPos.Mutable();
		BlockPos.Mutable duneBuilderPos;

		//For each of the 16 'rows' in a chunk on the x axis, we check each of the 16 columns along the z axis.
		for (int loopX = 0; loopX < 16; loopX++) {
			for (int loopZ = 0; loopZ < 16; loopZ++) {
				//For each column, we loop through all the blocks within the range we can generate dunes in.
				for(int loopY = 120; loopY > noDunesBelowY; --loopY) {
					//Store the position we're checking for seared sand as checkingPos
					checkingPos.set(featureContext.getOrigin()).move(loopX, loopY, loopZ);
					//If we detect the surface block from the config, we build a dune column on top.~
					//The surface rules place their blocks before this. By checking for the block it places on the surface only, we find the ground.~
					if (world.getBlockState(checkingPos) == config.surfaceBlock().getBlockState(featureContext.getRandom(), checkingPos)) {

						//Store the x and z coordinates of checkingPos.
						int checkingX = checkingPos.getX();
						int checkingZ = checkingPos.getZ();

						//If there's air under the surface block we found, replace it with the under block. This stops floating sand from appearing.
						if (world.getBlockState(new BlockPos(checkingX, loopY-1, checkingZ)).isAir()) {
							//Block placing line using the feature config. From this Fabric tutorial: https://fabricmc.net/wiki/tutorial:features
							world.setBlockState(checkingPos, config.underBlock().getBlockState(featureContext.getRandom(), checkingPos), 3);
						}

						//Copy checkingPos to duneBuilderPos, as we need to remember where we were checking after building the dune column.
						duneBuilderPos = checkingPos;
						//Sample the noise from the noise map at the x and z coordinates we're in, making the result the height of the column.
						double height = (NOISE.sample(checkingX * noiseMapXScale , checkingZ * noiseMapZScale) * duneMaxHeight);
							height = Math.abs(height);
							height = Math.min(height, (NOISE.sample(checkingX * 0.03 + 5 , checkingZ * 0.05 + 5) * duneMaxHeight + 6));

						//Yet another loop forms the dunes.
						for (int h = 0; h < height; h++) {
							duneBuilderPos.move(Direction.UP);
							//Make sure we aren't replacing other terrain blocks!
							if (world.getBlockState(duneBuilderPos).isAir()) {
								//Block placing line using the feature config. From this Fabric tutorial: https://fabricmc.net/wiki/tutorial:features
								world.setBlockState(duneBuilderPos, config.surfaceBlock().getBlockState(featureContext.getRandom(), duneBuilderPos), 3);
							}
							//If we find another terrain block where we're trying to build, stop building the column.
							else {
								break;
							}
						}
					}
				}
			}
		}
		//Some BYG features had this set to true, but their dune feature had it set to false. I have no clue what it means.
		return false;
	}
}