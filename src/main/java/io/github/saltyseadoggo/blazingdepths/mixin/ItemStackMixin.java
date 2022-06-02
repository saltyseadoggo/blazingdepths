package io.github.saltyseadoggo.blazingdepths.mixin;

import io.github.saltyseadoggo.blazingdepths.access.ItemStackAccess;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

    //This class mixins ItemStack's method that handles durability loss to subtract from "bonus durability" applied by seared sealant first.
    //It also adds some methods to ItemStack that are needed for our ItemRendererMixin to work.

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAccess {

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

    @Shadow public abstract NbtCompound getOrCreateNbt();

    //Returns the value of our own "BonusDurability" NBT tag.
    public int blazingdepths_getBonusDurability() {
        return this.nbt == null ? 0 : this.nbt.getInt("BonusDurability");
    }

    //Sets the value of our "BonusDurability" NBT tag.
    //This value should never equal zero or less, so the lower bound for setting its value is one.
    public void blazingdepths_setBonusDurability(int value) {
        this.nbt.putInt("BonusDurability", Math.max(1, value));
    }

    //The next three methods define some values needed by our ItemRendererMixin to draw our custom durability bar.

    //Returns true if the item has the "BonusDurability" tag.
    public boolean blazingdepths_hasBonusDurability() {
        return blazingdepths_getBonusDurability() != 0;
    }

    //Determines the width of the bonus durability bar based on how much bonus durability exists.
    public int blazingdepths_getBonusDurabilityBarStep() {
        return Math.round((13.0f * (float) blazingdepths_getBonusDurability()) / (float) getMaxDamage());
    }

    //Mixin the method that calculates durability loss to subtract from bonus durability before vanilla durability.
    //For the rather complicated looking paths, just type in the desired method and IntelliJ will autocomplete.~
    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), cancellable = true)
    public void blazingdepths_damageSealantFirst(int amount, Random random, @Nullable ServerPlayerEntity player, CallbackInfoReturnable cir) {
        if (blazingdepths_hasBonusDurability()) {
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
                //If all of the damage was absorbed, we return false.
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
