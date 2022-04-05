package io.github.saltyseadoggo.blazingdepths.mixin;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.RootsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Mixin the RootsBlock, the base class of crimson and warped roots, to let them be placed on top of seared sand.

@Mixin(RootsBlock.class)
public abstract class RootsBlockMixin {

    //cancellable = true is important to prevent a crash.
    @Inject(method = "canPlantOnTop", at = @At("RETURN"), cancellable = true)
    public void AddBDRootGroundBlocks(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.isOf(BlazingDepthsBlocks.SEARED_SAND))
            cir.setReturnValue(true);
    }
}
