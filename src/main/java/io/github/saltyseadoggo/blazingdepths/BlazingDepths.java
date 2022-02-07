package io.github.saltyseadoggo.blazingdepths;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsSoundEvents;

public class BlazingDepths implements ModInitializer {
	public static final String MOD_ID = "blazing_depths";
 
  	@Override
 	public void onInitialize() {
			//run separate init classes found in the init folder
		BlazingDepthsSoundEvents.init();
		BlazingDepthsBiomes.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}