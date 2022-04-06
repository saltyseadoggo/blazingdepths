package io.github.saltyseadoggo.blazingdepths.blocks;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsSoundEvents;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class SearedSandBlock extends SandBlock {
    //Defines a collision box for seared sand that is identical to that of soul sand.
    //Entities without Frost Walker will interact with this collision box.
    //Entities with Frost Walker will interact with a full cube collision box. See getCollisionShape below
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public SearedSandBlock (int color, Settings settings) {
        super (color, settings);
    }

    //This method handles the generation of sand particles when entities trudge through the seared sand.
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient) {
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

    //This method handles the conversion of seared sand to red sand when an entity with Frost Walker steps on it.
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        //Check if the entity that steps on seared sand has Frost Walker
        if (entity instanceof LivingEntity && EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            //Handle block setting and particle generation on server side
            if (!world.isClient) {
                world.setBlockState(pos, Blocks.RED_SAND.getDefaultState());
                ((ServerWorld) world).spawnParticles
                        //CLOUD particle type is the same as mob death particles
                        //Coordinates make the particles generate centered on the top face of the block
                        (ParticleTypes.CLOUD, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                                8, 0.5D, 0.0D, 0.5D, 0.0D);
                world.playSound (null, pos.getX(), pos.getY(), pos.getZ(), BlazingDepthsSoundEvents.BLOCK_SEARED_SAND_COOL,
                        SoundCategory.BLOCKS, 0.3F, 1.0F);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    //If an entity with Frost Walker comes into contact with seared sand, they will collide with a whole block collision box.
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityShapeContext) {
            Entity entity = entityShapeContext.getEntity();
            if (entity != null) {
                //If an entity falls onto seared sand, or if a LivingEntity with Frost Walker collides with it, they will collide with the solid shape.
                if (entity instanceof LivingEntity && EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
                    //If an entity is standing on seared sand and equips Frost Walker boots, they would clip into the block because its collision box changed.
                    //To fix this, we move them up a bit when the hitbox changes.
                    if (Math.floor(entity.getX()) == pos.getX() && Math.floor(entity.getY()) == pos.getY() && Math.floor(entity.getZ()) == pos.getZ())
                        entity.setPosition(entity.getX(), Math.ceil(entity.getY()) + 0.1D, entity.getZ());
                    return VoxelShapes.fullCube();
                }
            }
        }
        //Otherwise, the entity will collide with the normal, soul sand-like hitbox.
        return COLLISION_SHAPE;
    }

    //This method and the below two were taken from SoulSandBlock, and relate to making the block behave as a full block in all except entity collision.
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
