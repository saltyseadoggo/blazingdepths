package io.github.saltyseadoggo.blazingdepths.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.saltyseadoggo.blazingdepths.access.ItemStackAccess;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    //This mixin handles the drawing of the bonus durability bar on items in the inventory.

    //Store the colors of the upper and lower halves of the bonus durability bar
    final int bonusDurabilityBarUpperColor = 2930387;
    final int bonusDurabilityBarLowerColor = 886689;

    @Shadow
    protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    //This code is largely adapted from the method it mixins.
    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    public void renderBonusDurabilityBar(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        //If we don't cast to (Object) here, a compile error crops up.
        ItemStackAccess stack2 = (ItemStackAccess) (Object) stack;
        if (stack2.blazingdepths_hasBonusDurability()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            int i = stack2.blazingdepths_getBonusDurabilityBarStep();
            //Draw our addition to the black rectangle background drawn by the vanilla durability bar, or draw the whole thing if it's missing.
            if (!stack.isItemBarVisible()) {
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, Math.min(13, i +1), 3, 0, 0, 0, 255);
            }
            else {
                this.renderGuiQuad(bufferBuilder, x + 2, y + 15, Math.min(13, i +1), 1, 0, 0, 0, 255);
            }
            //Draw the bonus durability bar. It is drawn in two halves, both with a different color.
            this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1,
                    bonusDurabilityBarUpperColor >> 16 & 0xFF,
                    bonusDurabilityBarUpperColor >> 8 & 0xFF,
                    bonusDurabilityBarUpperColor & 0xFF, 255);
            this.renderGuiQuad(bufferBuilder, x + 2, y + 14, i, 1,
                    bonusDurabilityBarLowerColor >> 16 & 0xFF,
                    bonusDurabilityBarLowerColor >> 8 & 0xFF,
                    bonusDurabilityBarLowerColor & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }
}
