package com.andysmods.crazyores.registry;

import com.andysmods.api.RegistryInitializer;
import com.andysmods.crazyores.CrazyOres;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class GroupRegistry implements RegistryInitializer {

	@Override
	public void initializeRegistry() {
		registerCrazyOresTab();
	}

	private void registerCrazyOresTab() {
		Registry.register(Registries.ITEM_GROUP, new Identifier(CrazyOres.MOD_INFO.modId(), "crazyores_group"),
			FabricItemGroup.builder()
				.displayName(Text.translatable("itemgroup.crazyores"))
				.icon(() -> new ItemStack(ItemRegistry.COPPER_BUCKET_LAVA)).entries((displayContext, entries) -> {
					entries.add(ItemRegistry.COPPER_BUCKET_EMPTY);
					entries.add(ItemRegistry.COPPER_BUCKET_WATER);
					entries.add(ItemRegistry.COPPER_BUCKET_COD);
					entries.add(ItemRegistry.COPPER_BUCKET_SALMON);
					entries.add(ItemRegistry.COPPER_BUCKET_TROPICAL_FISH);
					entries.add(ItemRegistry.COPPER_BUCKET_PUFFERFISH);
					entries.add(ItemRegistry.COPPER_BUCKET_AXOLOTL);
					entries.add(ItemRegistry.COPPER_BUCKET_TADPOLE);
					entries.add(ItemRegistry.COPPER_BUCKET_LAVA);
					entries.add(ItemRegistry.COPPER_BUCKET_POWDER_SNOW);
					entries.add(ItemRegistry.COPPER_BUCKET_MILK);
				}).build());
	}
}
