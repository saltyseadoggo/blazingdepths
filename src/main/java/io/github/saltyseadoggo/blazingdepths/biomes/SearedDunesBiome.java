package io.github.saltyseadoggo.blazingdepths.biomes;

import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
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
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.NoiseHypercube;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.NetherPlacedFeatures;
import net.minecraft.world.gen.feature.OrePlacedFeatures;

public class SearedDunesBiome {
    public static Biome SEARED_DUNES = create();

        //These values relate to the new 1.18 overworld generation, and how it places biomes and creates mountains and such.
        //I have no clue why I need this for a Nether biome, but I can't register it without it.
        //Values used by the vanilla Nether biomes can be referenced in MultiNoiseBiomeSource$Preset.
    public static final NoiseHypercube NOISE_POINT = MultiNoiseUtil.createNoiseHypercube(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

    public static Biome create() {
		return (new Biome.Builder())
            .precipitation(Biome.Precipitation.NONE)
            .category(Biome.Category.NETHER)
            .temperature(2.0F)
            .downfall(0.0F)
            .effects(createBiomeEffects())
            .spawnSettings(createSpawnSettings())
            .generationSettings(createGenerationSettings())
			.build();
    }

    public static BiomeEffects createBiomeEffects() {
        BiomeEffects.Builder builder = new BiomeEffects.Builder()
                //Brightest shade from the seared sandstone top texture converted from hexadecimal to decimal with an online converter
            .fogColor(13784875)
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

    public static GenerationSettings createGenerationSettings() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder()
                //Nether cave carver
            .carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
                //The order of these features is apparently sensitive. Reordering them might cause the game to crash on the Create New World screen.
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherPlacedFeatures.PATCH_FIRE)
                //GLOWSTONE_EXTRA absolutely must come before GLOWSTONE, or the game crashes on the Create New World screen
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherPlacedFeatures.GLOWSTONE_EXTRA)
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherPlacedFeatures.GLOWSTONE)
                //TODO: replace with one with shorter, age 15 vines
            .feature(GenerationStep.Feature.VEGETAL_DECORATION, NetherPlacedFeatures.WEEPING_VINES)
            .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_MAGMA);
                //TODO: basalt collumn reskin
                //TODO: crimson roots
                //TODO: rare warped roots
                //TODO: big crimson roots
                //TODO: rare big warped roots
                //Mojank made these private, so I guess no structures in the dunes for now...
            //.structureFeature(ConfiguredStructureFeatures.FORTRESS)
            //.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)
            //.structureFeature(ConfiguredStructureFeatures.BASTION_REMNANT)

        DefaultBiomeFeatures.addNetherMineables(builder);
        return builder.build();
    }

    public static SpawnSettings createSpawnSettings() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder()
            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 40, 1, 1))
            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5))
            .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2));

        return builder.build();
    }
}
