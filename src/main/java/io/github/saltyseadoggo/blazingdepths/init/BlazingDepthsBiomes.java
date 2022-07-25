package io.github.saltyseadoggo.blazingdepths.init;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.biomes.SearedDunesBiome;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class BlazingDepthsBiomes {
	//These lines create registry keys for our biomes. We need the keys to register our biomes, and to reference them in code elsewhere.
    public static final RegistryKey<Biome> SEARED_DUNES_KEY = makeBiomeRegKey("seared_dunes");
	public static final RegistryKey<Biome> THUNDERING_BOWELS_KEY = makeBiomeRegKey("thundering_bowels");

	//This method is found in OverworldBiomeCreator, and is used in the creation of vanilla Nether biomes in TheNetherBiomeCreator.
	//It has protected access, so here's a copy of it for our own use.
	public static int getSkyColor(float temperature) {
		float f = temperature / 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	//Register our biomes and add them to worldgen.~
    public static void init() {
		// **Explanation of the Noise Hypercube values**
		//Temperature, humidity, continentalness, erosion, depth and weirdness vary all throughout the Nether's terrain.
		//Each biome has defined numerical values for all six of those properties. See MultiNoiseBiomeSource$Preset for the vanilla Nether biomes' values.

		//At each point in the Nether, the biome that generates is the one with the closest numbers to the ones at that position.
		//For example, if one point has 0.6 temperature and -0.2 humidity,
		//the closest biome would be the Crimson Forest, which has 0.4 temperature and 0 humidity.

		//Vanilla Nether biomes only define special values for Temperature and Humidity, but the other values still vary throughout the Nether.
		//For example, Promenade replaces some Nether Wastes with its different Gallery biomes by generating the galleries at the same temperature and humidity,
		//but with different Erosion and Weirdness values, which vanilla Nether biomes all ignore.

		//If two biomes have all of the same values, only the first will generate.

		//Offset is unique, in that setting it to values larger than 0.0 makes the biome rarer, *if* another biome has all the same values except for Offset.
		//This could be used to make rare variants of biomes.~

		//This temperature value is between the Crimson Forest's 0.4 and the Nether Wastes' 0.0,
		//and this humidity value is between the Soul Sand Valley's -0.5 and the Nether Wastes' 0.0,
		//so the Seared Dunes always generate between Nether Wastes, Soul Sand Valleys and Crimson Forests.~
		registerAndAddToGen(SEARED_DUNES_KEY, SearedDunesBiome.create(), 0.2f, -0.3f);
	}

	//This method creates a biome registry key from a string containing the biome's name.
	private static RegistryKey<Biome> makeBiomeRegKey(String name) {
		return RegistryKey.of(Registry.BIOME_KEY, BlazingDepths.makeIdentifier(name));
	}

	//This method registers a biome and adds it to the Nether's worldgen using the Fabric API.
	private static void registerAndAddToGen(RegistryKey<Biome> key, Biome biome, Float temperature, Float humidity) {
		Registry.register(BuiltinRegistries.BIOME, key.getValue(), biome);
		NetherBiomes.addNetherBiome(key,
				MultiNoiseUtil.createNoiseHypercube(temperature, humidity, 0.0f, 0.0F, 0.0f, 0.0F, 0.0F));
	}
} 
