package io.github.saltyseadoggo.blazingdepths.items;

import net.minecraft.item.Item;

    //Items of this class are identical to vanilla basic items, like iron ingots, but with our expanding tooltips.

public class HoldKeyTooltipItem
extends Item
implements HoldKeyTooltip {

    public HoldKeyTooltipItem(Settings settings) {
        super(settings);
    }
}
