package io.github.saltyseadoggo.blazingdepths;

import io.github.saltyseadoggo.blazingdepths.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
	}

	public static Identifier makeIdentifier(String path) {
		return new Identifier(MOD_ID, path);
	}
}