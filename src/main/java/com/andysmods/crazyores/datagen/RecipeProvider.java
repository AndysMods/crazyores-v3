package com.andysmods.crazyores.datagen;

import com.andysmods.crazyores.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class RecipeProvider extends FabricRecipeProvider {
	public RecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generate(Consumer<RecipeJsonProvider> exporter) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemRegistry.COPPER_BUCKET_EMPTY)
			.input('#', Items.COPPER_INGOT).pattern("# #").pattern(" # ")
			.criterion(FabricRecipeProvider.hasItem(Items.COPPER_INGOT), FabricRecipeProvider.conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(exporter);
	}
}
