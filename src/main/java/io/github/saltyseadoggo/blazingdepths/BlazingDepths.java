package io.github.saltyseadoggo.blazingdepths;

import io.github.saltyseadoggo.blazingdepths.init.*;
import io.github.saltyseadoggo.blazingdepths.items.HoldKeyTooltip;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BlazingDepths implements ModInitializer {
	//Store the mod namespace here for use everywhere else
	public static final String MOD_ID = "blazing_depths";
	//Set us up with a creative tab
	public static final ItemGroup CREATIVE_TAB = FabricItemGroupBuilder.create(
            new Identifier(MOD_ID, "blazing_depths")).icon(() -> new ItemStack(BlazingDepthsBlocks.SEARED_SANDSTONE)).build();

  	@Override
 	public void onInitialize() {
		//Run separate init classes found in the init folder
		//BlazingDepthsBlocks *must* be initialized before BlazingDepthsBiomes, or the game crashes during initialization.
		//This is because BlazingDepthsBlocks sets the variable containing the fog color for the Seared Dunes biome.
		BlazingDepthsSoundEvents.init();
		BlazingDepthsBlocks.init();
		BlazingDepthsItems.init();
		BlazingDepthsFeatures.init();
		BlazingDepthsBiomes.init();

		//This is called a callback. Documentation for them is found here: https://fabricmc.net/wiki/tutorial:callbacks
		//The above documentation does not contain a complete list of callbacks; I was told about the one used here on the Fabricord.
		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {

			//Make items that implement the HoldKeyTooltip interface show their expanding tooltips.
			//I could have instead used a parent class with the tooltip code for all tooltip having items to extend,
			//but that would mean I couldn't have ArmorItems or whatever with tooltips, only basic Items.
			if (stack.getItem() instanceof HoldKeyTooltip) {
				//If ALT is being held, show the expanded tooltip; if it isn't being held, show a line telling the player to do so.
				String key = Screen.hasAltDown() ?
						stack.getTranslationKey() + ".tooltip" :
						"tooltip.blazing_depths.more_info";
				lines.add(new TranslatableText(key).formatted(Formatting.GRAY));
			}

			//Make vanilla items with BonusDurability from seared sealant show their exact BonusDurability when advanced tooltips are on
			int bonusDurability = stack.getOrCreateNbt().getInt("BonusDurability");
			if (context.isAdvanced() && bonusDurability != 0) {
				String label = new TranslatableText("item.bonus_durability").getString();
				lines.add(new LiteralText(label + ": " + bonusDurability + " / " + stack.getMaxDamage()).formatted(Formatting.AQUA));
			}
		});
	}

	public static Identifier makeIdentifier(String path) {
		return new Identifier(MOD_ID, path);
	}
}