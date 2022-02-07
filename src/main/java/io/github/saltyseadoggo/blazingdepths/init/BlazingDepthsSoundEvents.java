package io.github.saltyseadoggo.blazingdepths.init;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.mixin.SoundEventAccessor;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlazingDepthsSoundEvents {

    // Acts as a kind of local registry for sound events added by Blazing Depths
	private static final Map<Identifier, SoundEvent> SOUND_EVENTS = new LinkedHashMap<>();
    
    public static final SoundEvent AMBIENT_SEARED_DUNES_LOOP = add(new SoundEvent(BlazingDepths.id("ambient.seared_dunes.loop")));

    private static <S extends SoundEvent> S add(S sound_event) {
		SOUND_EVENTS.put(((SoundEventAccessor) sound_event).getId(), sound_event);
		return sound_event;
	}

        //Initialization event called by BlazingDepths.java
    public static void init() {
        for (Identifier id : SOUND_EVENTS.keySet()) {
			Registry.register(Registry.SOUND_EVENT, id, SOUND_EVENTS.get(id));
		}
    }
}