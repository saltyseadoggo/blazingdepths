package io.github.saltyseadoggo.blazingdepths.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PowderSnowBlock.class)
public class LivingEntityMixin {

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    public void allowJumpIfInSearedSand (BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity) || entity.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW)) {
            entity.setOnGround(true);
        }
    }
}
