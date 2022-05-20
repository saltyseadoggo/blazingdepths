package io.github.saltyseadoggo.blazingdepths.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SearedSealantItem
extends Item {
    public SearedSealantItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
