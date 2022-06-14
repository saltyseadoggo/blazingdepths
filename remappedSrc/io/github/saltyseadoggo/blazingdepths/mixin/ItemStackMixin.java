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

    //This class mixins ItemStack's method that handles durability loss to subtract from "bonus durability" applied by seared sealant first.
    //It also adds some methods to ItemStack that are needed for our ItemRendererMixin to work.

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    //The @Shadow lines are "shadowing" the fields and methods from the ItemStack class that we need to reference.
    //For fields, we remove the `= whatever`. If the field is final, we also remove the final, then add @Final above the @Shadow.
    //For methods, we remove the {} and everything within, and also make the method abstract. If the method is private, we make it protected instead.
    //See: https://gist.github.com/TelepathicGrunt/3784f8a8b317bac11039474012de5fb4

    @Shadow
    private NbtCompound nbt;

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract void setDamage(int damage);

    @Shadow
    public abstract int getMaxDamage();

    @Shadow public abstract void removeSubNbt(String key);

    //Returns the value of our own "BonusDurability" NBT tag.
    public int blazingdepths_getBonusDurability() {
        return this.nbt == null ? 0 : this.nbt.getInt("BonusDurability");
    }

    //Sets the value of our "BonusDurability" NBT tag.
    //This value should never equal zero or less, so the lower bound for setting its value is one.
    public void blazingdepths_setBonusDurability(int value) {
        this.nbt.putInt("BonusDurability", Math.max(1, value));
    }

    //Mixin the method that calculates durability loss to subtract from bonus durability before vanilla durability.
    //For the rather complicated looking paths, just type in the desired method and IntelliJ will autocomplete.~
    //CTRL-click CallbackInfoReturnable<Boolean> to find out why we need the <Boolean> part. Without it, we get warnings.
    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), cancellable = true)
    public void blazingdepths_damageSealantFirst(int amount, Random random, @Nullable ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (blazingdepths_getBonusDurability() != 0) {
            //Get the remaining bonus durability points.
            int bonusDurability = this.blazingdepths_getBonusDurability();

            //If there's more than enough bonus durability to absorb the damage, subtract the damage from bonus durability and return false.
            //By returning false, the vanilla code doesn't get a chance to add the damage to the vanilla `damage` nbt tag.
            if (bonusDurability > amount) {
                blazingdepths_setBonusDurability(bonusDurability - amount);
                cir.setReturnValue(false);
            }
            else {
                //Otherwise, there is no more bonus durability left, so we remove the "BonusDurability" tag.
                removeSubNbt("BonusDurability");
                //If all the damage was absorbed, we return false.
                if (bonusDurability == amount) {
                    cir.setReturnValue(false);
                }
                //If some damage was not absorbed, we deal that damage to the tool.
                else {
                    int remainder = this.getDamage() + (amount - bonusDurability);
                    this.setDamage(remainder);
                    cir.setReturnValue(remainder >= this.getMaxDamage());
                }
            }
        }
    }
}
