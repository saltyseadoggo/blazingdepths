package io.github.saltyseadoggo.blazingdepths.biomes;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsFeatures;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsSoundEvents;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.NetherPlacedFeatures;
import net.minecraft.world.gen.feature.OrePlacedFeatures;

public class SearedDunesBiome {

    //"Build" the biome. The effects, mob spawn settings and terrain generation settings are configured in methods below.
    public static Biome create() {
		return (new Biome.Builder())
            .precipitation(Biome.Precipitation.NONE)
            .category(Biome.Category.NETHER)
            //TODO: Compare this with vanilla deserts and increase the temperature accordingly
            .temperature(2.0F)
            .downfall(0.0F)
            .effects(createBiomeEffects())
            .spawnSettings(createSpawnSettings())
            .generationSettings(createGenerationSettings())
			.build();
    }

    //Configure our biome's "effects," which are music & sounds; particles; and water, fog & sky colours.
    public static BiomeEffects createBiomeEffects() {
        BiomeEffects.Builder builder = new BiomeEffects.Builder()
			//Use seared sand's dust particle color as the fog color
            .fogColor(BlazingDepthsBlocks.SearedSandDustColor)
			//The water & sky colours zone
            .waterColor(4159204)
			.waterFogColor(329011)
            .skyColor(BlazingDepthsBiomes.getSkyColor(2.0F))
			//TODO: replace temporary crimson forest particles with abundant, sight obstructing sand particles
            .particleConfig(new BiomeParticleConfig(ParticleTypes.CRIMSON_SPORE, 0.025F))
			//The music & sounds zone
            .loopSound(BlazingDepthsSoundEvents.AMBIENT_SEARED_DUNES_LOOP)
            .moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0D))
			.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111D))
			.music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_NETHER_WASTES));
        
            return builder.build();
    }

    //Add the desired features to our biome to populate it with dunes, vegetation, ores, etc.
    //While all other Nether biomes include the Nether cave carver, I deliberately excluded it from the Seared Dunes because caves ruined the look of the biome.
    public static GenerationSettings createGenerationSettings() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder()
                //Dune feature
            .feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, BlazingDepthsFeatures.SEARED_DUNE_PLACED)
                //Floating sand fixer
            .feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, BlazingDepthsFeatures.FLOATING_SEARED_SAND_FIXER_PLACED)
                //The order of these features is apparently sensitive. Reordering them might cause the game to crash on the Create New World screen.
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherPlacedFeatures.PATCH_FIRE)
                //GLOWSTONE_EXTRA absolutely must come before GLOWSTONE, or the game crashes on the Create New World screen
                //TODO: Mixin these two features so they can generate under seared sandstone
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherPlacedFeatures.GLOWSTONE_EXTRA)
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherPlacedFeatures.GLOWSTONE)
                //TODO: replace with one that can generate under seared sandstone. Also consider making it have shorter, age 15 vines, and be more common
            .feature(GenerationStep.Feature.VEGETAL_DECORATION, NetherPlacedFeatures.WEEPING_VINES)
                //TODO: replace with one that generates more often
            .feature(GenerationStep.Feature.VEGETAL_DECORATION, NetherPlacedFeatures.PATCH_CRIMSON_ROOTS)
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_MAGMA);
                //TODO: basalt collumn reskin
                //TODO: crimson roots
                //TODO: rare warped roots
                //TODO: big crimson roots
                //TODO: rare big warped roots
                //TODO: use new 1.18.2 tags to make fortress, bastion remnant & ruined portal (custom with sand?) generate in our biome

        DefaultBiomeFeatures.addNetherMineables(builder);
        return builder.build();
    }

    //Configure what mobs can spawn in our biome.
    public static SpawnSettings createSpawnSettings() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder()
            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 40, 1, 1))
            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5))
            .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2));

        return builder.build();
    }
}
