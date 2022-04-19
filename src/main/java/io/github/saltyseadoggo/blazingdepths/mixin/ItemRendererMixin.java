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

    @Shadow
    protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

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
            int j = stack2.blazingdepths_getBonusDurabilityBarColor();
            //If the item has bonus durability but no lost regular durability, we need to draw the black background rectangle ourselves.
            if (!stack.isItemBarVisible()) {
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 3, 0, 0, 0, 255);
            }
            else {
                this.renderGuiQuad(bufferBuilder, x + 2, y + 15, 13, 1, 0, 0, 0, 255);
            }
            //Draw the bonus durability bar
            this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 2, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }
}
