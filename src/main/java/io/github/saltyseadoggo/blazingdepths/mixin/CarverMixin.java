package io.github.saltyseadoggo.blazingdepths.mixin;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Mixin the "can I carve this block?" method in Carver.class so that Nether caves can cut through Blazing Depths blocks.

//I looked at Cinderscapes' solution to its ground blocks not being carvable and tried to adapt that fix at first.
//(https://github.com/TerraformersMC/Cinderscapes/commit/e439bf400c64c94a7ff5dff52f69d4aca858b0ce)
//However, this resulted in a crash on startup, because we can't .add entries to the relevant ImmutableSet anymore, apparently.
//I could have mixin'ed to overwrite the set with one that contains the vanilla entries and our own, but that would have been incompatible with other mods.
//So I looked at NetherCaveCarver.carveAtPoint, and noticed that it uses the canAlwaysCarveBlock method in Carver.class.
//I then came up with this solution: canAlwaysCarveBlock will check if the block is one of our carvable blocks, and preemptively return true if it is.

@Mixin(Carver.class)
public abstract class CarverMixin {

    //cancellable = true is important to prevent a crash.
    @Inject(method = "canAlwaysCarveBlock", at = @At("RETURN"), cancellable = true)
    public void addBDCarvables(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        //When we get more terrain blocks, perhaps we can make a carvable list of our own, and have this if statement check for any of its contents.
        if (state.isOf(BlazingDepthsBlocks.SEARED_SAND) || state.isOf(BlazingDepthsBlocks.SEARED_SANDSTONE)) {
            cir.setReturnValue(true);
        }
    }
}