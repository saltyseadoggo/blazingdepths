package io.github.saltyseadoggo.blazingdepths.mixin;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    protected abstract void decrementStack(int slot);

    public int bonusDurabilityPerSealant = 512;
    public int usedSealants;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    //canTakeOutput checks if the smithing table contains a valid recipe and lets the player take the output item only if it does.
    //We inject into this method to let them take the output item if the inputs match the seared sealant application "recipe."
    @Inject(method = "canTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    public void blazingdepths_letPlayerTakeSealantApplicationOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        if (canApplySealant()) {
            cir.setReturnValue(true);
        }
    }

    //updateResult comes up with the item in the result slot by checking smithing recipes.
    //We inject into this method to come up with our own output item for the seared sealant application "recipe."
    @Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
    public void blazingdepths_applySearedSealant(CallbackInfo ci) {
        //We only proceed if the first slot contains seared sealant, and the second slot contains an item that can lose durability.
        if (canApplySealant()) {
            //Make a copy of the ItemStack in the first input slot.
            ItemStack itemStack = this.input.getStack(0).copy();
            //Store some information about this copy: its nbtCompound, maximum durability and current bonus durability.
            NbtCompound nbtCompound = itemStack.getOrCreateNbt();
            int maxDamage = itemStack.getMaxDamage();
            int oldBonusDurability = nbtCompound.getInt("BonusDurability");
            //Store the number of seared sealants in the second input slot.
            int sealantCount = this.input.getStack(1).getCount();

            //Determine how many seared sealants the player can apply to the input item.
            usedSealants = (int) Math.min(sealantCount, Math.ceil((double) (maxDamage - oldBonusDurability) / bonusDurabilityPerSealant));
            //Add bonus durability to the copied NbtCompound, capped by the item's maximum durability.
            nbtCompound.putInt("BonusDurability", Math.min(maxDamage, oldBonusDurability + (usedSealants * bonusDurabilityPerSealant)));
            //Set the copied input ItemStack's nbt compound to the one we just modified
            itemStack.setNbt(nbtCompound.copy());
            //Set the smithing table's result item to the modified ItemStack
            this.output.setStack(0, itemStack);
            //Stop the vanilla code from checking for a recipe
            ci.cancel();
        }
    }

    //onTakeOutput handles removing the input items used in a smithing recipe when it is completed.
    //We inject this method to remove more than one item from the second input slot based on the number of sealants consumed in one craft.
    @Inject(method = "onTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    protected void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (canApplySealant()) {
            //A line from the vanilla method. I don't know what it does.
            stack.onCraft(player.world, player, stack.getCount());
            //Decrement the first input slot as the vanilla method does.
            this.decrementStack(0);
            //Subtract all the consumed seared sealants at once
            ItemStack itemStack = this.input.getStack(1);
            itemStack.decrement(usedSealants);
            this.input.setStack(1, itemStack);
            //A line from the vanilla method. I don't know what it does.
            this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.SMITHING_TABLE_USED, pos, 0));
            ci.cancel();
        }

    }

    //Returns true if seared sealant can be applied, which means:
    //The second input item must be seared sealant.
    //The first input item must be damageable.
    //The first input item must not have maximum bonus durability.
    public boolean canApplySealant() {
        ItemStack stack = this.input.getStack(0);
        return (this.input.getStack(1).getItem() == BlazingDepthsItems.SEARED_SEALANT
                && stack.isDamageable()
                && stack.getMaxDamage() != stack.getOrCreateNbt().getInt("BonusDurability"));
    }
}
