package io.github.saltyseadoggo.blazingdepths.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SearedSealantItem
extends Item
implements HoldKeyTooltip {
    public SearedSealantItem(Item.Settings settings) {
        super(settings);
    }

    //Seared sealant gets an enchantment glint because it's special~
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
