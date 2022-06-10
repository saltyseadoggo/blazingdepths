package io.github.saltyseadoggo.blazingdepths.init;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.items.HoldKeyTooltipItem;
import io.github.saltyseadoggo.blazingdepths.items.SearedSealantItem;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class BlazingDepthsItems {
    public static final Item SEARED_SEALANT = register("seared_sealant",
            new SearedSealantItem(new Item.Settings().group(BlazingDepths.CREATIVE_TAB).rarity(Rarity.RARE)));
    public static final Item ANCIENT_CONCOCTION = register("ancient_concoction",
            new HoldKeyTooltipItem(new Item.Settings().group(BlazingDepths.CREATIVE_TAB).rarity(Rarity.UNCOMMON)));
    public static final Item WARPED_ROOT_EXTRACT = register("warped_root_extract",
            new Item(new Item.Settings().group(BlazingDepths.CREATIVE_TAB)));

    //Empty init method to make all the other code run
    public static void init() {}

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, BlazingDepths.makeIdentifier(id), item);
    }
}
