package com.andysmods.crazyores;

import com.andysmods.crazyores.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CrazyOresDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(BlockLootTableProvider::new);
		pack.addProvider(BlockTagProvider::new);
		pack.addProvider(ItemTagProvider::new);
		pack.addProvider(ModelProvider::new);
		pack.addProvider(RecipeProvider::new);
	}
}
