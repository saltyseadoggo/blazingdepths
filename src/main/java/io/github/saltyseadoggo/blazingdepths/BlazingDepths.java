package io.github.saltyseadoggo.blazingdepths;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsSoundEvents;

public class BlazingDepths implements ModInitializer {
		//Store the mod namespace here for use everywhere else
	public static final String MOD_ID = "blazing_depths";
		//Set us up with a creative tab
	public static final ItemGroup CREATIVE_TAB = FabricItemGroupBuilder.create(
            new Identifier(MOD_ID, "blazing_depths")).icon(() -> new ItemStack(BlazingDepthsBlocks.SEARED_SANDSTONE)).build();

  	@Override
 	public void onInitialize() {
			//Run separate init classes found in the init folder
		BlazingDepthsSoundEvents.init();
		BlazingDepthsBiomes.init();
		BlazingDepthsBlocks.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}