package io.github.saltyseadoggo.blazingdepths.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    private NbtCompound nbt;

    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract void setDamage(int damage);

    @Shadow
    public abstract int getMaxDamage();

    //Returns the value of our own "BonusDurability" NBT tag.
    public int getBonusDurability() {
        return this.nbt == null ? 0 : this.nbt.getInt("BonusDurability");
    }

    //Sets the value of our "BonusDurability" NBT tag.
    public void setBonusDurability(int value) {
        this.getOrCreateNbt().putInt("BonusDurability", Math.max(0, value));
    }

    //Mixin the method that calculates durability loss to subtract from bonus durability before vanilla durability.
    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), cancellable = true)
    public void blazingdepths_damageSealantFirst(int amount, Random random, @Nullable ServerPlayerEntity player, CallbackInfoReturnable cir) {
        if (getBonusDurability() != 0) {
            //Get the remaining bonus durability points.
            int bonusDurability = this.getBonusDurability();
            int k;
            //If there's enough bonus durability to absorb the damage, subtract the damage from bonus durability and return false.
            //By returning false, the vanilla code doesn't get a chance to add the damage to the vanilla `damage` value.
            if (bonusDurability >= amount) {
                setBonusDurability(bonusDurability - amount);
                cir.setReturnValue(false);
            }
            //If there isn't enough bonus durability to absorb the damage, get the amount of damage that can't be absorbed and add it to vanilla `damage`.
            //Once again, we return to prevent the vanilla code from dealing the full damage.
            else {
                setBonusDurability(0);
                k = this.getDamage() + (amount - bonusDurability);
                this.setDamage(k);
                cir.setReturnValue(k >= this.getMaxDamage());
            }
        }
    }
}
