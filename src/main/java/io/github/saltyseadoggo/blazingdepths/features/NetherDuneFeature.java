package io.github.saltyseadoggo.blazingdepths.features;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import com.mojang.serialization.Codec;

import java.util.Random;

public class NetherDuneFeature extends Feature<DuneFeatureConfig> {

    //This class is what builds the Seared Dunes biome's namesake dunes.

    //Set the minimum and maximum heights of the dunes, then choose a random height for this dune between them.
    final int duneMaxHeight = 7;
    final int duneMinHeight = 4;
    final int duneHeight = new Random().nextInt((duneMaxHeight - duneMinHeight) + duneMinHeight);
    //Do the same for length (the longer side)...
    final int duneMaxLength = 20;
    final int duneMinLength = 14;
    final int duneLength = new Random().nextInt((duneMaxLength - duneMinLength) + duneMinLength);
    //... and the width (the shorter side).
    final int duneMaxWidth = 10;
    final int duneMinWidth = 7;
    final int duneWidth = new Random().nextInt((duneMaxWidth - duneMinWidth) + duneMinWidth);

    public NetherDuneFeature(Codec<DuneFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DuneFeatureConfig> featureContext) {

        //Grab the feature config, which we need to get the blocks from
        DuneFeatureConfig config = featureContext.getConfig();
        //Get the dimension this feature is generating in (in regular gameplay, this will always be the Nether)
        StructureWorldAccess world = featureContext.getWorld();
        //Initialize the BlockPos we'll be using later for terrain analyzing and dune building.
        BlockPos.Mutable pos = new BlockPos.Mutable();

        //"Array Maker"
        //Creates a 2D array containing the height of each column in the dune.

        //Get the height of each 'slice' of the dune
        int[] heightOfEachSliceArray = new int[duneLength];
        for(int i = 0; i < duneLength; i++) {
            heightOfEachSliceArray[i] = getYOnBellCurve(duneLength, duneHeight, i);
        }
        //Get the height of each column of the dune
        int[][] heightOfEachColumnArray = new int[duneLength][duneWidth];
        for(int x = 0; x < duneLength; x++) {
            for(int z = 0; z < duneWidth; z++) {
                heightOfEachColumnArray[x][z] = getYOnBellCurve(duneWidth, heightOfEachSliceArray[x], z);
            }
        }

        //"Terrain Analyzer"
        //Iterates through the dune's building location, looking for the lowest terrain height within, while ignoring cliffs.

        int featureX = featureContext.getOrigin().getX() - duneLength /2;
        int featureZ = featureContext.getOrigin().getZ() - duneWidth /2;
        int featureY = featureContext.getOrigin().getY();

        for(int x = 0; x < duneLength; x++) {
            for(int z = 0; z < duneWidth; z++) {
                pos.set(featureX + x, featureY -1, featureZ + z);
                //If the block isn't air, it's a terrain block, which means we're on the ground.
                //Otherwise...
                if (world.getBlockState(pos).isAir()) {
                    //... we check one block down to see if we find terrain there.
                    //If we don't, there's a cliff edge here, which we will not be building dune columns on top of, so we ignore it.
                    //If we do...
                    pos.move(Direction.DOWN);
                    if (!world.getBlockState(pos).isAir()) {
                        //...we move the feature's location down a y value, so it's on the lowest terrain level.
                        featureY--;
                    }
                }
            }
        }

        //"Dune Shaper"
        //Builds the dune in the world based on the one we mapped out in the array.

        BlockState surfaceBlock = config.surfaceBlock().getBlockState(featureContext.getRandom(), pos);
        BlockState underBlock = config.underBlock().getBlockState(featureContext.getRandom(), pos);

        //We loop through the x and z coordinates, as we need to check all 64 columns in the chunk.
        for (int x = 0; x < duneLength; x++) {
            for (int z = 0; z < duneWidth; z++) {
                //Skip this column if height is 0
                if (heightOfEachColumnArray[x][z] == 0) continue;
                //If there's air at the bottom of the column, skip it, as there's a cliff edge in this column
                pos.set(featureX + x, featureY -1, featureZ + z);
                if (world.getBlockState(pos).isAir()) continue;

                for (int h = 0; h < heightOfEachColumnArray[x][z]; h++) {
                    pos.set(featureX + x, featureY + h, featureZ + z);
                    //Place sandstone below half the height of the column, and sand above
                    if (!world.getBlockState(pos).isAir()) {
                        if (h < heightOfEachColumnArray[x][z] / 2) {
                            world.setBlockState(pos, underBlock, 3);
                        }
                        else {
                            world.setBlockState(pos, surfaceBlock, 3);
                        }
                    }
                }
            }
        }
        //Some BYG features had this set to true, but their dune feature had it set to false. I have no clue what it means.
        return false;
    }

    //This method gets the y value at a given x on a bell curve shaped graph of the given dimensions.
    //Equation for the bell curve shaped graph provided by Penrose.~
    public int getYOnBellCurve (int width, int height, int x) {
        x -= (width /2);
        double pi = Math.PI;
        //Yes, we're supposed to cast the result to int. See: https://www.geeksforgeeks.org/convert-double-to-integer-in-java/
        return (int) Math.round(
                (Math.cos (Math.min (Math.max (pi *x /width, -pi), pi)) +1) *0.5 *height
        );
    }
}