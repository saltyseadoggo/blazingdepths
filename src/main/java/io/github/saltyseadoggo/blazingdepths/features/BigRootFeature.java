package io.github.saltyseadoggo.blazingdepths.features;

import com.mojang.serialization.Codec;
import io.github.saltyseadoggo.blazingdepths.features.config.BigRootFeatureConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class BigRootFeature extends Feature<BigRootFeatureConfig> {

        public BigRootFeature (Codec<BigRootFeatureConfig> configCodec) {
                super(configCodec);
        }

        //To tell this feature how to generate the roots, this method "maps" out their arrangements of blocks in a 3D array.
        //In each of the array positions is a boolean. True means place a wart block, while false means do nothing.
        public boolean[][][] chooseRootMap() {
                //Initialize the 3D array which will contain our "map."
                //The []'s are, in order, height, length (east to west) and width (north to south).
                //All the indexes are set to false by default.
                boolean[][][] rootMap = new boolean[5][5][5];
                //Set the bottom, central block to true, as all roots have this base block in common.
                rootMap[0][2][2] = true;

                //Each `case` here is a variant of the big root feature that I built in a Minecraft world and then manually mapped out.
                //This code chooses a random variant, then "maps" out the occupied blocks in our array.
                switch(new Random().nextInt(6)) {
                        case 0: rootMap[1][1][2] = true;        //Left branch
                                rootMap[1][3][1] = true;        //Right branch
                                rootMap[2][3][1] = true;

                        case 1: rootMap[1][2][2] = true;        //Extended center
                                rootMap[2][1][2] = true;        //Left branch
                                rootMap[1][3][3] = true;        //Right branch
                                rootMap[2][3][3] = true;
                                rootMap[3][3][3] = true;

                        case 2: rootMap[1][1][1] = true;        //Left branch
                                rootMap[2][1][0] = true;                //Northern branch
                                rootMap[2][0][2] = true;                //Southern branch
                                rootMap[3][0][2] = true;
                                rootMap[1][3][2] = true;        //Right branch
                                rootMap[2][3][2] = true;
                                rootMap[3][4][1] = true;                //Northern branch
                                rootMap[4][4][1] = true;
                                rootMap[3][3][3] = true;                //Southern branch

                        case 3: rootMap[1][2][1] = true;        //Northern branch
                                rootMap[2][2][1] = true;
                                rootMap[2][1][2] = true;                //Left branch
                                rootMap[3][1][2] = true;
                                rootMap[4][1][2] = true;
                                rootMap[3][3][1] = true;                //Right branch
                                rootMap[1][3][2] = true;        //Right branch
                                rootMap[2][3][3] = true;
                                rootMap[3][3][3] = true;

                        case 4: rootMap[1][2][2] = true;        //Extended center
                                rootMap[1][1][1] = true;        //Left branch
                                rootMap[2][1][1] = true;
                                rootMap[2][0][2] = true;                //Left branch
                                rootMap[3][0][2] = true;
                                rootMap[4][0][2] = true;
                                rootMap[3][2][1] = true;                //Right branch
                                rootMap[1][3][3] = true;        //Right branch
                                rootMap[2][3][3] = true;

                        case 5: rootMap[1][1][1] = true;        //Left branch
                                rootMap[2][1][1] = true;
                                rootMap[2][0][2] = true;                //Left branch
                                rootMap[3][0][2] = true;
                                rootMap[3][2][1] = true;                //Right branch
                                rootMap[4][2][1] = true;
                                rootMap[1][3][2] = true;        //Right branch
                                rootMap[2][3][3] = true;
                                rootMap[3][3][3] = true;
                }
                return rootMap;
        }

        @Override
        public boolean generate(FeatureContext<BigRootFeatureConfig> context) {
                //Get the dimension this feature is generating in (in regular gameplay, this will always be the Nether)
                StructureWorldAccess world = context.getWorld();
                //Grab the feature config, which we need to get the blocks from
                BigRootFeatureConfig config = context.getConfig();
                //Initialize the BlockPos we'll be using.
                //We need to initialize this before we get the blocks from the config because, for some reason, getting said blocks requires a pos.
                BlockPos.Mutable pos = new BlockPos.Mutable();
                //Get the wart and roots blocks from the config
                BlockState wartBlock = config.wartBlock().getBlockState(context.getRandom(), pos);
                BlockState rootsBlock = config.rootsBlock().getBlockState(context.getRandom(), pos);

                //Choose a number of arms for this big root to have, either two or three.
                //nextInt chooses a random integer between 0 and one less than the bound, so bound must be two to let it return one.
                int armCount = new Random().nextInt(2) +2;

                //Move pos to the feature origin
                pos.set(context.getOrigin());

                return true;
        }

        //This method is used whenever a branch forks into two to make the second arm go in a direction opposite to the first.
        public void moveInVariedOppositeDirection (BlockPos.Mutable pos, int xDir, int zDir) {

                //If neither xDir nor zDir is equal to 0, pos is in a corner, so we store this information.
                boolean isPosInCorner = xDir != 0 && zDir !=0;
                //We invert the direction to start. If pos is in a corner, the new one will default to the opposite corner.
                //If pos is adjacent to the center in a cardinal direction, the new one will default to the opposite cardinal direction.
                //(Negative 0 is still 0, so the 0 direction doesn't change.)
                int xOpposite = -xDir;
                int zOpposite = -zDir;

                //If pos is centered on the x-axis (and thus not in a corner), we can vary left or right, or not move at all.
                //The nextInt returns 0, 1 or 2, and we subtract one to have it return -1, 0 or 1, valid direction numbers, with equal chance.~
                if (xOpposite == 0) {
                        xOpposite = (new Random().nextInt(3)) -1;
                }
                //If pos isn't centered on the x-axis and is in a corner, we have a 1/3 chance to center the new one on the x-axis (one of three valid positions).
                else if (isPosInCorner && new Random().nextInt(3) == 0) {
                        xOpposite = 0;
                        //If this chance passes, our new pos is no longer in a corner, so we change the boolean to reflect that.
                        //This is done to prevent the z direction from varying if the x direction did, which would put pos in the center (bad).
                        isPosInCorner = false;
                }

                //Repeat the above for the z-axis.
                if (zOpposite == 0) {
                        zOpposite = (new Random().nextInt(3)) -1;
                }
                //There are only two valid positions left, so the random chance is 1/2 instead of 1/3 like above.
                else if (isPosInCorner && new Random().nextInt(2) == 0) {
                        zOpposite = 0;
                }

                //Either way, we now have the direction to move pos to place the first block of our next branch.
                pos.move(xOpposite, pos.getY(), zOpposite);
        }
}
