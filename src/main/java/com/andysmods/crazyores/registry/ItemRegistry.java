package com.andysmods.crazyores.registry;

import com.andysmods.api.RegistryInitializer;
import com.andysmods.api.bucket.Bucket;
import com.andysmods.api.bucket.CustomEntityBucketItem;
import com.andysmods.api.bucket.CustomMilkBucketItem;
import com.andysmods.api.bucket.CustomPowderSnowBucketItem;
import com.andysmods.crazyores.CrazyOres;
import com.andysmods.crazyores.item.bucket.CopperBucketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry implements RegistryInitializer {

	public static Map<EntityType<?>, Item> COPPER_ENTITY_BUCKETS = new HashMap<>();

	public static final Item COPPER_BUCKET_EMPTY = registerItem("copper_bucket_empty", new CopperBucketItem(Fluids.EMPTY, new FabricItemSettings().maxCount(16)));
	public static final Item COPPER_BUCKET_WATER = registerItem("copper_bucket_water", new CopperBucketItem(Fluids.WATER, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_LAVA = registerItem("copper_bucket_lava", new CopperBucketItem(Fluids.LAVA, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_MILK = registerItem("copper_bucket_milk", new CustomMilkBucketItem(COPPER_BUCKET_EMPTY, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_POWDER_SNOW = registerItem("copper_bucket_powder_snow", new CustomPowderSnowBucketItem(COPPER_BUCKET_EMPTY, Blocks.POWDER_SNOW, SoundEvents.BLOCK_POWDER_SNOW_PLACE, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_AXOLOTL = registerItem("copper_bucket_axolotl", new CustomEntityBucketItem(COPPER_BUCKET_EMPTY, EntityType.AXOLOTL, SoundEvents.ITEM_BUCKET_FILL_AXOLOTL, Fluids.WATER, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_COD = registerItem("copper_bucket_cod", new CustomEntityBucketItem(COPPER_BUCKET_EMPTY, EntityType.COD, SoundEvents.ITEM_BUCKET_FILL_FISH, Fluids.WATER, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_PUFFERFISH = registerItem("copper_bucket_pufferfish", new CustomEntityBucketItem(COPPER_BUCKET_EMPTY, EntityType.PUFFERFISH, SoundEvents.ITEM_BUCKET_FILL_FISH, Fluids.WATER, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_SALMON = registerItem("copper_bucket_salmon", new CustomEntityBucketItem(COPPER_BUCKET_EMPTY, EntityType.SALMON, SoundEvents.ITEM_BUCKET_FILL_FISH, Fluids.WATER, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_TADPOLE = registerItem("copper_bucket_tadpole", new CustomEntityBucketItem(COPPER_BUCKET_EMPTY, EntityType.TADPOLE, SoundEvents.ITEM_BUCKET_FILL_TADPOLE, Fluids.WATER, new FabricItemSettings().maxCount(1)));
	public static final Item COPPER_BUCKET_TROPICAL_FISH = registerItem("copper_bucket_tropical_fish", new CustomEntityBucketItem(COPPER_BUCKET_EMPTY, EntityType.TROPICAL_FISH, SoundEvents.ITEM_BUCKET_FILL_FISH, Fluids.WATER, new FabricItemSettings().maxCount(1)));

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(CrazyOres.MOD_INFO.modId(), name), item);
	}

	private static void itemGroupIngredients(FabricItemGroupEntries entries) {
		entries.add(COPPER_BUCKET_EMPTY);
		entries.add(COPPER_BUCKET_WATER);
		entries.add(COPPER_BUCKET_LAVA);
		entries.add(COPPER_BUCKET_MILK);
		entries.add(COPPER_BUCKET_POWDER_SNOW);
		entries.add(COPPER_BUCKET_AXOLOTL);
		entries.add(COPPER_BUCKET_COD);
		entries.add(COPPER_BUCKET_PUFFERFISH);
		entries.add(COPPER_BUCKET_SALMON);
		entries.add(COPPER_BUCKET_TROPICAL_FISH);
		entries.add(COPPER_BUCKET_TADPOLE);
	}

	@Override
	public void onInitializeRegistry() {
		CrazyOres.CRAZYORES_LOGGER.info("Registering items!");

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ItemRegistry::itemGroupIngredients);

		registerValidCopperBucketEntities();
	}

	private void registerValidCopperBucketEntities() {
		COPPER_ENTITY_BUCKETS.put(EntityType.AXOLOTL, COPPER_BUCKET_AXOLOTL);
		COPPER_ENTITY_BUCKETS.put(EntityType.COD, COPPER_BUCKET_COD);
		COPPER_ENTITY_BUCKETS.put(EntityType.PUFFERFISH, COPPER_BUCKET_PUFFERFISH);
		COPPER_ENTITY_BUCKETS.put(EntityType.SALMON, COPPER_BUCKET_SALMON);
		COPPER_ENTITY_BUCKETS.put(EntityType.TROPICAL_FISH, COPPER_BUCKET_TROPICAL_FISH);
		COPPER_ENTITY_BUCKETS.put(EntityType.TADPOLE, COPPER_BUCKET_TADPOLE);
	}
}
