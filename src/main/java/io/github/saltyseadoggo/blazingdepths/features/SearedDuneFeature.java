package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.noise.OpenSimplexNoise;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
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
	int duneMaxHeight = 7;
	//Set the x and z scale applied to the noise map, which affects the horizontal size of the dunes. Smaller = wider
	//1.16 Terrestria had these values set to 0.01 and 0.015, respectively.
	double noiseMapXScale = 0.05;
	double noiseMapZScale = 0.075;

	public SearedDuneFeature(Codec<DuneFeatureConfig> configCodec) {
        super(configCodec);
    }

	@Override
	public boolean generate(FeatureContext<DuneFeatureConfig> featureContext) {

		//Grab the feature config, which we need to get the blocks from
		DuneFeatureConfig config = featureContext.getConfig();
		//Get the dimension this feature is generating in (in regular gameplay, this will always be the Nether)
		StructureWorldAccess world = featureContext.getWorld();
		//Get the sea level, one block below which we stop generating dunes.
		//TODO: Find a replacement for world.getSeaLevel that returns the Nether's sea level
		int noDunesBelowY = 30; //world.getSeaLevel();
		//Get the coordinate at which Nether caves stop generating, above which we do not generate dunes.
		//TODO: Get this via a method for datapack/Amplified Nether compat
		int noDunesAboveY = 120;
		//Initialize the two position variables we'll be using to build dunes.
		BlockPos.Mutable checkingPos = new BlockPos.Mutable();
		BlockPos.Mutable duneBuilderPos;

		//We loop through the x and z coordinates, as we need to check all 64 columns in the chunk.
		for (int loopX = 0; loopX < 16; loopX++) {
			for (int loopZ = 0; loopZ < 16; loopZ++) {
				//For each column, we loop down through all the blocks within the range we can generate dunes in.
				for(int loopY = noDunesAboveY; loopY > noDunesBelowY; --loopY) {

					//Store the position we're checking for seared sand as checkingPos
					checkingPos.set(featureContext.getOrigin()).move(loopX, loopY, loopZ);
					//Store the surfaceBlock and underBlock from the config
					BlockState surfaceBlock = config.surfaceBlock().getBlockState(featureContext.getRandom(), checkingPos);
					BlockState underBlock = config.underBlock().getBlockState(featureContext.getRandom(), checkingPos);

					//We only run further actions when we find a seared sand block placed by the surface rule.
					//Surface rules run before features, so, by checking for the block our rule places on the surface only, we find the surface.~
					//This is why we iterate downwards instead of upwards: to avoid finding seared sand blocks that are part of dune columns.
					if (world.getBlockState(checkingPos) == surfaceBlock) {

						//Store the x and z coordinates of checkingPos.
						int checkingX = checkingPos.getX();
						int checkingZ = checkingPos.getZ();

						//If there's air under the surface block we found, replace it with the under block. This stops floating sand from appearing.
						if (world.getBlockState(new BlockPos(checkingX, loopY-1, checkingZ)).isAir()) {
							//Block placing line using the feature config. From this Fabric tutorial: https://fabricmc.net/wiki/tutorial:features
							world.setBlockState(checkingPos, underBlock, 3);
						}

						//Check for adjacent cliff edges or biome borders, and stop the code here if one is found, as we don't build dunes next to those things.
						if (!areAllNeighborsGood(checkingPos, surfaceBlock, underBlock, world)) {
							continue;
						}

						//Copy checkingPos to duneBuilderPos, as we need to remember where we were checking after building the dune column.
						duneBuilderPos = checkingPos;
						//Sample the noise from the noise map at the x and z coordinates we're in, making the result the height of the column.
						double height = (NOISE.sample(checkingX * noiseMapXScale , checkingZ * noiseMapZScale) * duneMaxHeight);
						height = Math.abs(height);

						//Yet another loop forms the dunes.
						for (int h = 0; h < height; h++) {
							//Skip the first layer deliberately. Otherwise, the sand is two layers deep everywhere unnecessarily.
							if (h == 0) continue;
							//Move duneBuilderPos up a block
							duneBuilderPos.move(Direction.UP);
							//Make sure we aren't replacing other terrain blocks before placing a sand block!
							if (world.getBlockState(duneBuilderPos).isAir()) {
								world.setBlockState(duneBuilderPos, surfaceBlock, 3);
							}
							//If we find another terrain block where we're trying to build, stop building the column
							else break;
						}
					}
				}
			}
		}
		//Some BYG features had this set to true, but their dune feature had it set to false. I have no clue what it means.
		return false;
	}

	//This method is run whenever we find a seared sand block to build a dune column on top of.
	//It analyzes the adjacent blocks to determine if the sand block is on the edge of the biome or a cliff, and returns false if so.
	public boolean areAllNeighborsGood (BlockPos.Mutable pos, BlockState surfaceBlock, BlockState underBlock, StructureWorldAccess world) {
		return (isColumnGoodNeighbor(pos, Direction.NORTH, surfaceBlock, underBlock, world)
			&& isColumnGoodNeighbor(pos, Direction.EAST, surfaceBlock, underBlock, world)
			&& isColumnGoodNeighbor(pos, Direction.SOUTH, surfaceBlock, underBlock, world)
			&& isColumnGoodNeighbor(pos, Direction.WEST, surfaceBlock, underBlock, world));
	}

	//This method determines if there's a cliff edge or a non-Seared Dunes biome in a given space, which makes it a 'bad neighbor' for dune columns.
	public boolean isColumnGoodNeighbor (BlockPos.Mutable pos, Direction direction, BlockState surfaceBlock, BlockState underBlock, StructureWorldAccess world) {
		//Move the provided BlockPos in the provided Direction, and store the BlockState found there
		BlockState state = world.getBlockState(pos.move(direction));
		//If the adjacent block is seared sand, the terrain is flat, which is fine.
		//Likewise, if the adjacent block is seared sandstone, there's a step-up in the terrain that's still in the seared dunes biome, which is also fine.
		if (state == surfaceBlock || state == underBlock) return true;
		//If the adjacent block is air, there's either a step-down in terrain, or a cliff. We check the block below to find out.
		if (state.isAir()) {
			state = world.getBlockState(pos.move(Direction.DOWN));
			//If the block under the air block is seared sand or sandstone, there is a step-down in terrain, which is fine.
			//(Seared sandstone occurs on the surface where floating sand 'sheets' are replaced.)
			//If else, the block is either air (cliff) or something else (terrain in an adjacent biome), which means we need to shorten the column.
			return (state == surfaceBlock || state == underBlock);
		}
		//If the adjacent block is neither seared sand/stone nor air, it's either a cliff/cave wall or a step-up into another biome.
		//We check the block above to find out.
		state = world.getBlockState(pos.move(Direction.UP));
		//If the block above isn't air, the adjacent column is a cliff/cave wall, with is fine.
		//Otherwise, it's a step-up into another biome, which means we need to shorten the column.
		return (!state.isAir());
	}
}