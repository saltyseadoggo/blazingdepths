package io.github.saltyseadoggo.blazingdepths.mixin;

import io.github.saltyseadoggo.blazingdepths.access.ItemStackAccess;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

    //Mixins ItemStack's method that handles durability loss to subtract from 'bonus durability' applied by seared sealant first.

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAccess {

    //The @Shadow lines are "shadowing" the `nbt` field and some methods from the ItemStack class, so we can use them here.
    //For fields, we remove the `= whatever`.
    //For methods, we remove the {} and everything within, and also make the method abstract.
    //See: https://gist.github.com/TelepathicGrunt/3784f8a8b317bac11039474012de5fb4

    @Shadow
    private NbtCompound nbt;

    //When we shadow a final field, we remove `final` when we copy-paste, then add @Final above @Shadow.
    @Final
    @Shadow
    private Item item;

    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract void setDamage(int damage);

    @Shadow
    public abstract int getMaxDamage();

    //Returns the value of our own "BonusDurability" NBT tag.
    public int blazingdepths_getBonusDurability() {
        return this.nbt == null ? 0 : this.nbt.getInt("BonusDurability");
    }

    //Sets the value of our "BonusDurability" NBT tag.
    public void blazingdepths_setBonusDurability(int value) {
        this.getOrCreateNbt().putInt("BonusDurability", Math.max(0, value));
    }

    //"The durability bar zone"
    //The next three methods are related to drawing the durability bar that shows how much bonus durability an item has.

    //Returns true if the tool has more than 0 bonus durability.
    //Functions as the bonus durability bar's equivalent of `isItemBarVisible`. Is also used in the `damage` method inject.
    public boolean blazingdepths_hasBonusDurability() {
        return blazingdepths_getBonusDurability() != 0;
    }

    //Returns the bonus durability bar's color. Unlike the vanilla durability bar, ours is always a solid color.
    public int blazingdepths_getBonusDurabilityBarColor() {
        return BlazingDepthsBlocks.SearedSandDustColor;
    }

    //Determines the width of the bonus durability bar based on how much bonus durability exists.
    public int blazingdepths_getBonusDurabilityBarStep() {
        return Math.round((13.0f * (float) blazingdepths_getBonusDurability()) / (float) item.getMaxDamage());
    }

    //Mixin the method that calculates durability loss to subtract from bonus durability before vanilla durability.
    //For the rather complicated looking paths, just type in the desired method and IntelliJ will autocomplete.~
    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), cancellable = true)
    public void blazingdepths_damageSealantFirst(int amount, Random random, @Nullable ServerPlayerEntity player, CallbackInfoReturnable cir) {
        if (blazingdepths_hasBonusDurability()) {
            //Get the remaining bonus durability points.
            int bonusDurability = this.blazingdepths_getBonusDurability();
            int k;
            //If there's enough bonus durability to absorb the damage, subtract the damage from bonus durability and return false.
            //By returning false, the vanilla code doesn't get a chance to add the damage to the vanilla `damage` nbt tag.
            if (bonusDurability >= amount) {
                blazingdepths_setBonusDurability(bonusDurability - amount);
                cir.setReturnValue(false);
            }
            //If there isn't enough bonus durability to absorb the damage, get the amount of damage that can't be absorbed and add it to vanilla `damage`.
            //Once again, we return to prevent the vanilla code from dealing the full damage.
            else {
                blazingdepths_setBonusDurability(0);
                k = this.getDamage() + (amount - bonusDurability);
                this.setDamage(k);
                cir.setReturnValue(k >= this.getMaxDamage());
            }
        }
    }
}
