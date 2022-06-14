package io.github.saltyseadoggo.blazingdepths.datagen;

import io.github.saltyseadoggo.blazingdepths.BlazingDepths;
import io.github.saltyseadoggo.blazingdepths.datagen.api.LanguageDataProvider;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBiomes;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsBlocks;
import io.github.saltyseadoggo.blazingdepths.init.BlazingDepthsItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class BlazingDepthsDataGen implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		fabricDataGenerator.addProvider(BlockStateDefinitionProvider::new);
		fabricDataGenerator.addProvider(BlazingLootTableDataProvider::new);
		fabricDataGenerator.addProvider(BlazingLanguageDataProvider::new);
		fabricDataGenerator.addProvider(BlazingRecipesDataProvider::new);

		BlazingBlockTagsDataProvider blockTagsProvider = fabricDataGenerator.addProvider(BlazingBlockTagsDataProvider::new);
		fabricDataGenerator.addProvider(new BlazingItemTagsDataProvider(fabricDataGenerator, blockTagsProvider));
	}

	private static class BlockStateDefinitionProvider extends FabricModelProvider {

		public BlockStateDefinitionProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
			//Seared Sand
			//now uses its own custom model, but I left its old datagen code here for future reference.~
//			TexturedModel.Factory searedSandFactory = TexturedModel.CUBE_ALL.andThen(textures -> textures.put(TextureKey.ALL,
//					new Identifier(BlazingDepths.MOD_ID, "block/seared_sand")));
//			blockStateModelGenerator.registerSingleton(BlazingDepthsBlocks.SEARED_SAND, searedSandFactory);
			blockStateModelGenerator.registerParentedItemModel(BlazingDepthsBlocks.SEARED_SAND,
					ModelIds.getBlockModelId(BlazingDepthsBlocks.SEARED_SAND));

			//Seared Sandstone
			TextureMap searedSandstoneTexture = new TextureMap()
					.put(TextureKey.SIDE, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone"))
					.put(TextureKey.END, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone_top"))
					.put(TextureKey.WALL, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone"));
			TexturedModel.Factory searedSandstoneFactory = TexturedModel.CUBE_COLUMN.andThen(texture -> texture
					.put(TextureKey.SIDE, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone"))
					.put(TextureKey.END, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone_top")));
			blockStateModelGenerator.registerSingleton(BlazingDepthsBlocks.SEARED_SANDSTONE, searedSandstoneFactory);
			blockStateModelGenerator.registerParentedItemModel(BlazingDepthsBlocks.SEARED_SANDSTONE,
					ModelIds.getBlockModelId(BlazingDepthsBlocks.SEARED_SANDSTONE));

			wall(BlazingDepthsBlocks.SEARED_SANDSTONE_WALL, searedSandstoneTexture, blockStateModelGenerator);
			stairs(BlazingDepthsBlocks.SEARED_SANDSTONE_STAIRS, searedSandstoneTexture, blockStateModelGenerator);
			slab(BlazingDepthsBlocks.SEARED_SANDSTONE_SLAB, searedSandstoneTexture, blockStateModelGenerator);

			//Smooth Seared Sandstone
			TextureMap smoothSearedSandstoneTexture = new TextureMap()
					.put(TextureKey.SIDE, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone_top"))
					.put(TextureKey.END, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone_top"))
					.put(TextureKey.WALL, new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone_top"));
			TexturedModel.Factory smoothSearedSandstoneFactory = TexturedModel.CUBE_ALL.andThen(textures -> textures.put(TextureKey.ALL,
					new Identifier(BlazingDepths.MOD_ID, "block/seared_sandstone_top")));
			blockStateModelGenerator.registerSingleton(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE, smoothSearedSandstoneFactory);
			blockStateModelGenerator.registerParentedItemModel(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE,
					ModelIds.getBlockModelId(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE));

			wall(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_WALL, smoothSearedSandstoneTexture, blockStateModelGenerator);
			stairs(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_STAIRS, smoothSearedSandstoneTexture, blockStateModelGenerator);
			slab(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_SLAB, smoothSearedSandstoneTexture, blockStateModelGenerator);
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {

		}

		private void stairs(Block block, TextureMap texture, BlockStateModelGenerator blockStateModelGenerator) {
			Identifier searedSandstoneStairsInner = Models.INNER_STAIRS.upload(block,
					texture, blockStateModelGenerator.modelCollector);
			Identifier searedSandstoneStairsStraight = Models.STAIRS.upload(block,
					texture, blockStateModelGenerator.modelCollector);
			Identifier searedSandstoneStairsOuter = Models.OUTER_STAIRS.upload(block,
					texture, blockStateModelGenerator.modelCollector);
			blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(block,
					searedSandstoneStairsInner, searedSandstoneStairsStraight, searedSandstoneStairsOuter));
			blockStateModelGenerator.registerParentedItemModel(block, searedSandstoneStairsStraight);
		}

		private void slab(Block block, TextureMap texture, BlockStateModelGenerator blockStateModelGenerator) {
			Identifier resourceLocation3 = Models.SLAB_TOP.upload(block, texture,
					blockStateModelGenerator.modelCollector);
			Identifier resourceLocation4 = Models.SLAB.upload(block, texture,
					blockStateModelGenerator.modelCollector);
			blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(block,
					resourceLocation4, resourceLocation3, ModelIds.getBlockModelId(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE)));
			blockStateModelGenerator.registerParentedItemModel(block, resourceLocation4);
		}

		public void wall(Block wallBlock, TextureMap texture, BlockStateModelGenerator blockStateModelGenerator) {
			Identifier identifier = Models.TEMPLATE_WALL_POST.upload(wallBlock, texture, blockStateModelGenerator.modelCollector);
			Identifier identifier2 = Models.TEMPLATE_WALL_SIDE.upload(wallBlock, texture, blockStateModelGenerator.modelCollector);
			Identifier identifier3 = Models.TEMPLATE_WALL_SIDE_TALL.upload(wallBlock, texture, blockStateModelGenerator.modelCollector);
			blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(wallBlock, identifier, identifier2, identifier3));
			Identifier identifier4 = Models.WALL_INVENTORY.upload(wallBlock, texture, blockStateModelGenerator.modelCollector);
			blockStateModelGenerator.registerParentedItemModel(wallBlock, identifier4);
		}
	}

	private static class BlazingLanguageDataProvider extends LanguageDataProvider {

		protected BlazingLanguageDataProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, "en_us");
		}

		@Override
		protected void registerTranslations() {
			//Creative Tab
			this.add("itemGroup.blazing_depths.blazing_depths", "Blazing Depths");

			//Biomes
			this.addBiome(BlazingDepthsBiomes.SEARED_DUNES_KEY, "Seared Dunes");

			//Seared Sand
			this.addBlock(BlazingDepthsBlocks.SEARED_SAND, "Seared Sand");

			//Seared Sandstone
			this.addBlock(BlazingDepthsBlocks.SEARED_SANDSTONE, "Seared Sandstone");
			this.addBlock(BlazingDepthsBlocks.SEARED_SANDSTONE_SLAB, "Seared Sandstone Slab");
			this.addBlock(BlazingDepthsBlocks.SEARED_SANDSTONE_STAIRS, "Seared Sandstone Stairs");
			this.addBlock(BlazingDepthsBlocks.SEARED_SANDSTONE_WALL, "Seared Sandstone Wall");

			//Smooth Seared Sandstone
			this.addBlock(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE, "Smooth Seared Sandstone");
			this.addBlock(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_SLAB, "Smooth Seared Sandstone Slab");
			this.addBlock(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_STAIRS, "Smooth Seared Sandstone Stairs");
			this.addBlock(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_WALL, "Smooth Seared Sandstone Wall");

			//Ancient Concoction and its ingredients
			this.addItem(BlazingDepthsItems.SEARED_SEALANT, "Seared Sealant");
			this.addItem(BlazingDepthsItems.ANCIENT_CONCOCTION, "Ancient Concoction");
			this.addItem(BlazingDepthsItems.WARPED_ROOT_EXTRACT, "Warped Root Extract");

			//Tooltips
			this.add("tooltip.blazing_depths.more_info", "Hold [ALT] to view info");
			this.add("tooltip.blazing_depths.bonus_durability", "Bonus Durability");
			this.add("item.blazing_depths.seared_sealant.tooltip", """
					A surface coating brewed according to ancient recipes that protects equipment from wear and tear.
					Apply this in a smithing table to any item with durability to give it "bonus durability" that is consumed before vanilla durability.
					For more information, view uses in REI.""");
			this.add("item.blazing_depths.ancient_concoction.tooltip", """
					A cracked vial containing an unknown chemical substance. The fluid inside seems to have anti-abrasive properties.
					Brew Warped Root Extract into this in a brewing stand to create Seared Sealant.""");
			this.add("item.blazing_depths.warped_root_extract.tooltip", """
					A dew-like fluid found inside of warped roots. If left exposed to air, it hardens and becomes clear.
					Brew this into Ancient Concoctions in a brewing stand to create Seared Sealant.""");

			//REI info
			this.add("info.blazing_depths.apply_sealant", """
					Place seared sealant and any item with durability in a Smithing Table to apply the sealant, giving the item bonus durability that is consumed before vanilla durability.

					Applying seared sealant to armor also makes it immune to damage from sandstorms. This effect lasts as long as the armor has bonus durability.
					
					You may add more sealants to an item to add more bonus durability until its blue bar is full.
					 
					Bonus durability from seared sealant is affected by Unbreaking, but cannot be repaired by Mending.""");

			//Subtitles
			this.add("subtitles.blazing_depths.block.seared_sand.cool", "Seared sand cools");
		}
	}

	private static class BlazingLootTableDataProvider extends FabricBlockLootTableProvider {

		protected BlazingLootTableDataProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateBlockLootTables() {
			//Seared Sand
			this.addDrop(BlazingDepthsBlocks.SEARED_SAND);

			//Seared Sandstone
			this.addDrop(BlazingDepthsBlocks.SEARED_SANDSTONE);
			this.addDrop(BlazingDepthsBlocks.SEARED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
			this.addDrop(BlazingDepthsBlocks.SEARED_SANDSTONE_STAIRS);
			this.addDrop(BlazingDepthsBlocks.SEARED_SANDSTONE_WALL);

			//Smooth Seared Sandstone
			this.addDrop(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE);
			this.addDrop(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
			this.addDrop(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_STAIRS);
			this.addDrop(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_WALL);
		}

	}

	private static class BlazingBlockTagsDataProvider extends FabricTagProvider.BlockTagProvider {

		protected BlazingBlockTagsDataProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateTags() {
			this.getOrCreateTagBuilder(BlockTags.STAIRS)
					.add(BlazingDepthsBlocks.SEARED_SANDSTONE_STAIRS)
					.add(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_STAIRS);
			this.getOrCreateTagBuilder(BlockTags.SLABS)
					.add(BlazingDepthsBlocks.SEARED_SANDSTONE_SLAB)
					.add(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_SLAB);
			this.getOrCreateTagBuilder(BlockTags.WALLS)
					.add(BlazingDepthsBlocks.SEARED_SANDSTONE_WALL)
					.add(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_WALL);
			this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
					.add(BlazingDepthsBlocks.SEARED_SANDSTONE)
					.add(BlazingDepthsBlocks.SEARED_SANDSTONE_SLAB)
					.add(BlazingDepthsBlocks.SEARED_SANDSTONE_STAIRS)
					.add(BlazingDepthsBlocks.SEARED_SANDSTONE_WALL)
					.add(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE)
					.add(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_SLAB)
					.add(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_STAIRS)
					.add(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_WALL);
			this.getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
					.add(BlazingDepthsBlocks.SEARED_SAND);
		}

	}

	private static class BlazingItemTagsDataProvider extends FabricTagProvider.ItemTagProvider {

		protected BlazingItemTagsDataProvider(FabricDataGenerator dataGenerator, BlockTagProvider blockTagProvider) {
			super(dataGenerator, blockTagProvider);
		}

		@Override
		protected void generateTags() {
			this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
			this.copy(BlockTags.SLABS, ItemTags.SLABS);
			this.copy(BlockTags.WALLS, ItemTags.WALLS);
		}

	}

	private static class BlazingRecipesDataProvider extends FabricRecipeProvider {

		protected BlazingRecipesDataProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
			//Seared Sandstone (slab, stairs, wall)
			ShapedRecipeJsonBuilder.create(BlazingDepthsBlocks.SEARED_SANDSTONE).input('#', BlazingDepthsBlocks.SEARED_SAND).pattern("##").pattern("##")
					.criterion("has_seared_sand", conditionsFromItem(BlazingDepthsBlocks.SEARED_SAND)).offerTo(exporter);
			createSlabRecipe(BlazingDepthsBlocks.SEARED_SANDSTONE_SLAB, Ingredient.ofItems(BlazingDepthsBlocks.SEARED_SANDSTONE))
					.criterion("has_seared_sandstone", conditionsFromItem(BlazingDepthsBlocks.SEARED_SANDSTONE)).offerTo(exporter);
			createStairsRecipe(BlazingDepthsBlocks.SEARED_SANDSTONE_STAIRS, Ingredient.ofItems(BlazingDepthsBlocks.SEARED_SANDSTONE))
					.criterion("has_seared_sandstone", conditionsFromItem(BlazingDepthsBlocks.SEARED_SANDSTONE)).offerTo(exporter);
			offerWallRecipe(exporter, BlazingDepthsBlocks.SEARED_SANDSTONE_WALL, BlazingDepthsBlocks.SEARED_SANDSTONE);
			offerStonecuttingRecipe(exporter, BlazingDepthsBlocks.SEARED_SANDSTONE_SLAB, BlazingDepthsBlocks.SEARED_SANDSTONE, 2);
			offerStonecuttingRecipe(exporter, BlazingDepthsBlocks.SEARED_SANDSTONE_STAIRS, BlazingDepthsBlocks.SEARED_SANDSTONE);
			offerStonecuttingRecipe(exporter, BlazingDepthsBlocks.SEARED_SANDSTONE_WALL, BlazingDepthsBlocks.SEARED_SANDSTONE);

			//Smooth Seared Sandstone (slab, stairs, wall)
			CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(BlazingDepthsBlocks.SEARED_SANDSTONE), BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE.asItem(), 0.1F, 200).criterion("has_seared_sandstone", conditionsFromItem(BlazingDepthsBlocks.SEARED_SANDSTONE)).offerTo(exporter);
			createSlabRecipe(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_SLAB, Ingredient.ofItems(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE)).criterion("has_smooth_seared_sandstone", conditionsFromItem(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE)).offerTo(exporter);
			createStairsRecipe(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_STAIRS, Ingredient.ofItems(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE)).criterion("has_smooth_seared_sandstone", conditionsFromItem(BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE)).offerTo(exporter);
			offerWallRecipe(exporter, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_WALL, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE);
			offerStonecuttingRecipe(exporter, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_SLAB, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE, 2);
			offerStonecuttingRecipe(exporter, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_STAIRS, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE);
			offerStonecuttingRecipe(exporter, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE_WALL, BlazingDepthsBlocks.SMOOTH_SEARED_SANDSTONE);

			//Warped Root Extract
			ShapelessRecipeJsonBuilder.create(BlazingDepthsItems.WARPED_ROOT_EXTRACT)
					.input(Items.GLASS_BOTTLE).input(Items.WARPED_ROOTS).input(Items.WARPED_ROOTS).input(Items.WARPED_ROOTS).input(Items.WARPED_ROOTS)
					.criterion("has_warped_roots", conditionsFromItem(Items.WARPED_ROOTS)).offerTo(exporter);
		}

	}

}