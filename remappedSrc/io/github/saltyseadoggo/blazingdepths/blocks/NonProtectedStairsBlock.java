package io.github.saltyseadoggo.blazingdepths.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

    //We can't register StairsBlocks because the needed method is protected, so this extended class lets us access said method.

public class NonProtectedStairsBlock extends StairsBlock {
    public NonProtectedStairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
        super (baseBlockState, settings);
    }
    
}
