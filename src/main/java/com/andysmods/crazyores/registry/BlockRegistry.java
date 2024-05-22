package com.andysmods.crazyores.registry;

import com.andysmods.api.RegistryInitializer;
import com.andysmods.crazyores.CrazyOres;
import com.andysmods.crazyores.block.bucket.CustomBucketBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockRegistry implements RegistryInitializer {

	public static final Block COPPER_BUCKET_EMPTY_PLACEABLE = registerBlock("copper_bucket_empty_placeable", new CustomBucketBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK).nonOpaque()));

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(CrazyOres.MOD_INFO.modId(), name), block);
	}

	private static Block registerBlockWithoutBlockItem(String name, Block block) {
		return Registry.register(Registries.BLOCK, new Identifier(CrazyOres.MOD_INFO.modId(), name), block);
	}

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(CrazyOres.MOD_INFO.modId(), name), new BlockItem(block, new FabricItemSettings()));
	}

	@Override
	public void initializeRegistry() {
		CrazyOres.CRAZYORES_LOGGER.info("Registering blocks!");
	}
}
