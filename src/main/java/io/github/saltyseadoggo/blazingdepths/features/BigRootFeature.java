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

        @Override
        public boolean generate(FeatureContext<BigRootFeatureConfig> context) {
                //Get the dimension this feature is generating in (in regular gameplay, this will always be the Nether)
                StructureWorldAccess world = context.getWorld();
                //Grab the feature config, which we need to get the blocks from
                BigRootFeatureConfig config = context.getConfig();
                //Initialize the BlockPos we'll be using.
                //We need to initialize this before we get the blocks from the config because, for some reason, getting said blocks requires a pos.
                BlockPos.Mutable pos = new BlockPos.Mutable();
                BlockPos.Mutable pos2 = new BlockPos.Mutable();
                //Get the wart and roots blocks from the config
                BlockState wartBlock = config.wartBlock().getBlockState(context.getRandom(), pos);
                BlockState rootsBlock = config.rootsBlock().getBlockState(context.getRandom(), pos);

                //Move pos to the feature origin, then move it two spaces away so the actual origin is centered on the 5x5 area the root generates in.
                pos.set(context.getOrigin()).move(-2, 0, -2);

                //Choose whether to swap x and z when building the root.
                //Doing so rotates the finished product 90 degrees and flips it, creating additional variety.
                boolean shouldMirrorAndRotate = new Random().nextBoolean();

                //Choose a variant of the root structure to build
                boolean[][][] rootMap = chooseRootMap();

                //Place blocks where the array says to
                for (int y = 0; y < 5; y++) {
                        for (int x = 0; x < 5; x++) {
                                for (int z = 0; z < 5; z++) {
                                        pos2.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                                        if (shouldMirrorAndRotate) {
                                                if (rootMap[y][z][x] && world.getBlockState(pos2).isAir()) {
                                                        world.setBlockState(pos2, wartBlock, 3);
                                                }
                                        }
                                        else {
                                                if (rootMap[y][x][z] && world.getBlockState(pos2).isAir()) {
                                                        world.setBlockState(pos2, wartBlock, 3);
                                                }
                                        }

                                }
                        }
                }

                return true;
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
                switch (new Random().nextInt(6)) {
                        case 0 -> {
                                rootMap[1][1][2] = true;        //Left branch
                                rootMap[1][3][1] = true;        //Right branch
                                rootMap[2][3][1] = true;
                        }
                        case 1 -> {
                                rootMap[1][2][2] = true;        //Extended center
                                rootMap[2][1][2] = true;        //Left branch
                                rootMap[1][3][3] = true;        //Right branch
                                rootMap[2][3][3] = true;
                                rootMap[3][3][3] = true;
                        }
                        case 2 -> {
                                rootMap[1][1][1] = true;        //Left branch
                                rootMap[2][1][0] = true;                //Northern branch
                                rootMap[2][0][2] = true;                //Southern branch
                                rootMap[3][0][2] = true;
                                rootMap[1][3][2] = true;        //Right branch
                                rootMap[2][3][2] = true;
                                rootMap[3][4][1] = true;                //Northern branch
                                rootMap[4][4][1] = true;
                                rootMap[3][3][3] = true;                //Southern branch
                        }
                        case 3 -> {
                                rootMap[1][2][1] = true;        //Northern branch
                                rootMap[2][2][1] = true;
                                rootMap[2][1][2] = true;                //Left branch
                                rootMap[3][1][2] = true;
                                rootMap[4][1][2] = true;
                                rootMap[3][3][1] = true;                //Right branch
                                rootMap[1][3][2] = true;        //Right branch
                                rootMap[2][3][3] = true;
                                rootMap[3][3][3] = true;
                        }
                        case 4 -> {
                                rootMap[1][2][2] = true;        //Extended center
                                rootMap[1][1][1] = true;        //Left branch
                                rootMap[2][1][1] = true;
                                rootMap[2][0][2] = true;                //Left branch
                                rootMap[3][0][2] = true;
                                rootMap[4][0][2] = true;
                                rootMap[3][2][1] = true;                //Right branch
                                rootMap[1][3][3] = true;        //Right branch
                                rootMap[2][3][3] = true;
                        }
                        case 5 -> {
                                rootMap[1][1][1] = true;        //Left branch
                                rootMap[2][1][1] = true;
                                rootMap[2][0][2] = true;                //Left branch
                                rootMap[3][0][2] = true;
                                rootMap[3][2][1] = true;                //Right branch
                                rootMap[4][2][1] = true;
                                rootMap[1][3][2] = true;        //Right branch
                                rootMap[2][3][3] = true;
                                rootMap[3][3][3] = true;
                        }
                }
                return rootMap;
        }
}
