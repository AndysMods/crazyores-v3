package com.andysmods.crazyores.registry;

import com.andysmods.crazyores.CrazyOres;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class TagRegistry {

	private static final String COMMON_ID = "c";

	public static class Blocks {

		private static TagKey<Block> createBlockTag(String name) {
			return TagKey.of(RegistryKeys.BLOCK, new Identifier(CrazyOres.MOD_INFO.modId(), name));
		}

		private static TagKey<Block> createCommonBlockTag(String name) {
			return TagKey.of(RegistryKeys.BLOCK, new Identifier(TagRegistry.COMMON_ID, name));
		}
	}

	public static class Items {

		private static TagKey<Item> createItemTag(String name) {
			return TagKey.of(RegistryKeys.ITEM, new Identifier(CrazyOres.MOD_INFO.modId(), name));
		}

		private static TagKey<Item> createCommonItemTag(String name) {
			return TagKey.of(RegistryKeys.ITEM, new Identifier(TagRegistry.COMMON_ID, name));
		}
	}
}
