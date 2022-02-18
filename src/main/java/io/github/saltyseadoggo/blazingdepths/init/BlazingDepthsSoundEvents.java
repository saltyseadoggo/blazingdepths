package io.github.saltyseadoggo.blazingdepths.init;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlazingDepthsSoundEvents {
    public static SoundEvent AMBIENT_SEARED_DUNES_LOOP = register(BlazingDepths.makeIdentifier("ambient.seared_dunes.loop"));

	//Init event called by BlazingDepths.java. Just like in BlazingDepthsBiomes, calling an empty init event seems to run the code in the class.
    public static void init() {}

	private static SoundEvent register(Identifier identifier) {
		return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
	}

}