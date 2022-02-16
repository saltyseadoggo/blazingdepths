package io.github.saltyseadoggo.blazingdepths.init;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.biomes.SearedDunesBiome;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class BlazingDepthsBiomes {
	//init variable for seared dunes biome's registry key
    public static RegistryKey<Biome> SEARED_DUNES_KEY = register("seared_dunes", SearedDunesBiome.SEARED_DUNES);

	//copied from a vanilla class, both terrestria and promenade have this, it gets biome sky colour based on temperature
	public static int getSkyColor(float temperature) {
		float f = temperature / 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

    public static void init() {
        NetherBiomes.addNetherBiome(SEARED_DUNES_KEY, SearedDunesBiome.NOISE_POINT);
    }

	public static RegistryKey<Biome> register(String name, Biome biome) {
		RegistryKey<Biome> registryKey = RegistryKey.of(Registry.BIOME_KEY, BlazingDepths.makeIdentifier(name));
		Registry.register(BuiltinRegistries.BIOME, registryKey.getValue(), biome);
		return registryKey;
	}
} 
