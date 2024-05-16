package com.andysmods.crazyores.registry;

import com.andysmods.api.IRegistryInitializer;
import com.andysmods.crazyores.CrazyOres;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemRegistry implements IRegistryInitializer {

	private static final Item COPPER_BUCKET = registerItem("copper_bucket", new Item(new FabricItemSettings()));

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(CrazyOres.MOD_INFO.modId(), name), item);
	}

	private static void itemGroupIngredients(FabricItemGroupEntries entries) {
		entries.add(COPPER_BUCKET);
	}

	@Override
	public void onInitializeRegistry() {
		CrazyOres.CRAZYORES_LOGGER.info("Registering items!");

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ItemRegistry::itemGroupIngredients);
	}
}
