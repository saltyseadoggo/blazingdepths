package io.github.saltyseadoggo.blazingdepths.rei;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;

//Roughly Enough Items plugin to add information for some of our items.

public class BlazingDepthsREIPlugin implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        addInfo(registry, BlazingDepthsItems.SEARED_SEALANT, "apply_sealant");
    }

    private void addInfo(DisplayRegistry registry, ItemConvertible item, String infoKey){
        registry.add(DefaultInformationDisplay.createFromEntry(EntryStacks.of(item), Text.translatable(item.asItem().getTranslationKey()))
                .line(Text.translatable("info." + BlazingDepths.MOD_ID + "." + infoKey)));
    }
}
