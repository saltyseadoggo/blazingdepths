package io.github.saltyseadoggo.blazingdepths.blocks;

import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SearedSandBlock extends SandBlock {
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public SearedSandBlock (int color, Settings settings) {
        super (color, settings);
    }

    //This method handles the generation of sand particles when entities trudge through the seared sand, and the frost walker reaction.
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        //entity.slowMovement(state, new Vec3d(0.8999999761581421D, 3.5D, 0.8999999761581421D));
        if (!world.isClient) {

            //If an entity with Frost Walker steps onto seared sand, convert it into red sand
            //if (entity instanceof LivingEntity && EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
                //world.setBlockState(pos, Blocks.RED_SAND.getDefaultState());

                //When the block is changed, the entity is clipped into it. These lines let the player keep moving smoothly.
                //Vec3d v = entity.getVelocity();
                //entity.setVelocity(v);
                //entity.setPosition(entity.getX(), Math.ceil(entity.getY()) +10, entity.getZ());
                //entity.refreshPositionAndAngles(entity.getX(), Math.ceil(entity.getY()), entity.getZ(), entity.getYaw(), entity.getPitch());


                //world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH,
                        //SoundCategory.BLOCKS, 1.0F, 1.0F, true);
                //return;
            //}

            //If an entity moves while inside seared sand, we make particles.
            if (entity.lastRenderX != entity.getX() || entity.lastRenderZ != entity.getZ()) {
                Random random = world.getRandom();
                //Spawn the particles.
                //While particles are rendered on the client, this method sends packets to the client, so it works.
                //Particle spawning code adapted from MagmaBlock$randomTick
                //deltaX, Y and Z values adapted from PowderSnowBlock&onEntityCollision
                ((ServerWorld)world).spawnParticles
                        (new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state),
                        entity.getX() + MathHelper.nextBetween(random, -0.5F, 0.5F),
                        pos.getY() + 1.1,
                        entity.getZ() + MathHelper.nextBetween(random, -0.5F, 0.5F),
                        1,
                        MathHelper.nextBetween(random, -0.5F, 0.5F) * 0.08D,
                        0.05D,
                        MathHelper.nextBetween(random, -0.5F, 0.5F) * 0.08D,
                        0.0D);
            }
        }
    }

    //The below methods were taken from SoulSandBlock, and relate to making the block have a less than full block collision shape.

    //Set seared sand's collision shape to be slightly shorter than a full block, so entities sink into it slightly.
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
