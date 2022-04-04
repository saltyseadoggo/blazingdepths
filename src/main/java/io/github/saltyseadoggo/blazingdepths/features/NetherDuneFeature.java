package io.github.saltyseadoggo.blazingdepths.features;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import com.mojang.serialization.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class NetherDuneFeature extends Feature<DuneFeatureConfig> {

    //This class is what builds the Seared Dunes biome's namesake dunes.

    //Set the minimum and maximum heights of the dunes.
    final int duneMinHeight = 4;
    final int duneMaxHeight = 7;
    //Do the same for length (the longer side)...
    final int duneMinLength = 18;
    final int duneMaxLength = 24;
    //... and the width (the shorter side).
    final int duneMinWidth = 10;
    final int duneMaxWidth = 14;

    public NetherDuneFeature(Codec<DuneFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DuneFeatureConfig> featureContext) {

        //"Setter-Upper"
        //Decides the size of the dune, initializes some important variables, and cancels generation if the feature origin is in a bad place.

        //If the feature origin is below the Nether's sea level or above the cut-off point for its cave carvers, cancel generation.
        int featureY = featureContext.getOrigin().getY();
        if (featureY <= 31 || featureY >= 120) return false;

        //Initialize the BlockPos we'll be using later for terrain analyzing and dune building.
        //We need to initialize this before we get the blocks from the config because, for some reason, getting said blocks requires a pos.
        BlockPos.Mutable pos = new BlockPos.Mutable();

        //Get the dimension this feature is generating in (in regular gameplay, this will always be the Nether)
        StructureWorldAccess world = featureContext.getWorld();
        //Grab the feature config, which we need to get the blocks from
        DuneFeatureConfig config = featureContext.getConfig();
        //Get the sand and sandstone blocks from the config
        BlockState surfaceBlock = config.surfaceBlock().getBlockState(featureContext.getRandom(), pos);
        BlockState underBlock = config.underBlock().getBlockState(featureContext.getRandom(), pos);

        //Choose a random height, length (the longer side) and width (the shorter side) for this dune.
        int duneHeight = new Random().nextInt((duneMaxHeight - duneMinHeight)) + duneMinHeight;
        int duneLength = new Random().nextInt((duneMaxLength - duneMinLength)) + duneMinLength;
        int duneWidth = new Random().nextInt((duneMaxWidth - duneMinWidth)) + duneMinWidth;


        //"Array Maker"
        //Creates a 2D array containing the height of each column in the dune.

        //Get the height and width of each 'slice' of the dune
        //[number] is the number of entries in the array, not the index of the final entry.
        //We need the final entry's index to equal duneLength, so we have to add one to duneLength.
        int[] heightOfEachSliceArray = new int[duneLength +1];
        for(int i = 0; i <= duneLength; i++) {
            heightOfEachSliceArray[i] = getYOnBellCurve(duneLength, duneHeight, i);
        }
        //Get the height of each column of the dune
        int[][] heightOfEachColumnArray = new int[duneLength +1][duneWidth +1];
        for(int x = 0; x <= duneLength; x++) {
            for(int z = 0; z <= duneWidth; z++) {
                heightOfEachColumnArray[x][z] = getYOnBellCurve(duneWidth, heightOfEachSliceArray[x], z);
            }
        }



        //"Terrain Analyzer"
        //Iterates through the dune's building location, looking for the lowest terrain height within, while ignoring cliffs.

        int featureX = featureContext.getOrigin().getX() - duneLength /2;
        int featureZ = featureContext.getOrigin().getZ() - duneWidth /2;

        for(int x = 0; x <= duneLength; x++) {
            for(int z = 0; z <= duneWidth; z++) {
                pos.set(featureX + x, featureY, featureZ + z);
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

        featureY++;

        //We loop through the x and z coordinates, as we need to check all 64 columns in the chunk.
        for (int x = 0; x <= duneLength; x++) {
            for (int z = 0; z <= duneWidth; z++) {
                //Skip this column if height is 0
                if (heightOfEachColumnArray[x][z] == 0) continue;
                //If there's air at the bottom of the column, skip it, as there's a cliff edge in this column.
                //Likewise, if there's lava at the bottom of the column, we're above the lava sea, so skip in that case as well.
                pos.set(featureX + x, featureY -1, featureZ + z);
                if (world.getBlockState(pos).isAir() || world.getBlockState(pos).isOf(Blocks.LAVA)) continue;

                for (int h = 0; h < heightOfEachColumnArray[x][z]; h++) {
                    pos.set(featureX + x, featureY + h, featureZ + z);
                    //Place sandstone below half the height of the column, and sand above
                    if (world.getBlockState(pos).isAir()) {
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
        return true;
    }

    //This method gets the y value at a given x on a bell curve shaped graph of the given dimensions.
    //The 'bell curve' is actually the middle-most wave of a cosine wave.
    //Equation for the bell curve shaped graph provided by Penrose.~
    public int getYOnBellCurve (int width, int height, int x) {
        //The equation we use to make the bell curve shape creates a graph of double the size of the input width and height.
        //To compensate, we multiply x by two to skip every other x coordinate, creating the same result.
        //We also need to move x to the left by half of the graph's width to compensate for the graph being centered on the y axis.
        double dx = (x *2) -width;
        double dh = (double) height /2;
        //Store pi
        double pi = Math.PI;
        //Yes, we're supposed to cast the result to int. See: https://www.geeksforgeeks.org/convert-double-to-integer-in-java/
        return (int) Math.round(
                Math.cos (pi *(dx /width)) *dh +dh
        );
    }
}