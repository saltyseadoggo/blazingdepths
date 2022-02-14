package io.github.saltyseadoggo.blazingdepths.init;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.blocks.NonProtectedStairsBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlazingDepthsBlocks {
        //Here, we're defining what class each of our blocks is an instance of, and configuring them with the .options() you see.

        //Seared Sandstone
    public static final Block SEARED_SANDSTONE = new Block(AbstractBlock.Settings.copy(Blocks.RED_SANDSTONE).sounds(BlockSoundGroup.TUFF));
    public static final Block SEARED_SANDSTONE_SLAB = new SlabBlock(AbstractBlock.Settings.copy(Blocks.RED_SANDSTONE_SLAB).sounds(BlockSoundGroup.TUFF));
    public static final Block SEARED_SANDSTONE_STAIRS = new NonProtectedStairsBlock(SEARED_SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(SEARED_SANDSTONE));
    public static final Block SEARED_SANDSTONE_WALL =  new WallBlock(AbstractBlock.Settings.copy(SEARED_SANDSTONE));
        //Smooth Seared Sandstone
    public static final Block SMOOTH_SEARED_SANDSTONE = new Block(AbstractBlock.Settings.copy(Blocks.SMOOTH_RED_SANDSTONE).sounds(BlockSoundGroup.TUFF));
    public static final Block SMOOTH_SEARED_SANDSTONE_SLAB = new SlabBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_RED_SANDSTONE_SLAB).sounds(BlockSoundGroup.TUFF));
    public static final Block SMOOTH_SEARED_SANDSTONE_STAIRS = new NonProtectedStairsBlock(SMOOTH_SEARED_SANDSTONE.getDefaultState(), AbstractBlock.Settings.copy(SMOOTH_SEARED_SANDSTONE));
    
    public static void init() {
            //Define default item settings for our BlockItems, notably needed to set their creative tab to ours.
        Item.Settings defaultItemSettings = new Item.Settings().group(BlazingDepths.CREATIVE_TAB);

            //Register all of the blocks with BlockItems!

            //Seared Sandstone
        registerWithItem("seared_sandstone", SEARED_SANDSTONE, defaultItemSettings);
        registerWithItem("seared_sandstone_slab", SEARED_SANDSTONE_SLAB, defaultItemSettings);
        registerWithItem("seared_sandstone_stairs", SEARED_SANDSTONE_STAIRS, defaultItemSettings);
        registerWithItem("seared_sandstone_wall", SEARED_SANDSTONE_WALL, defaultItemSettings);
            //Smooth Seared Sandstone
        registerWithItem("smooth_seared_sandstone", SMOOTH_SEARED_SANDSTONE, defaultItemSettings);
        registerWithItem("smooth_seared_sandstone_slab", SMOOTH_SEARED_SANDSTONE_SLAB, defaultItemSettings);
        registerWithItem("smooth_seared_sandstone_stairs", SMOOTH_SEARED_SANDSTONE_STAIRS, defaultItemSettings);
    }

        //This method helps us register all of our Blocks with BlockItems, with less bulk in the above code.
    public static void registerWithItem(String namespaceid, Block block, Item.Settings settings) {
        var id = new Identifier(BlazingDepths.MOD_ID, namespaceid);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, settings));
    }
}
