package com.andysmods.crazyores.datagen;

import com.andysmods.crazyores.CrazyOres;
import com.andysmods.crazyores.registry.BlockRegistry;
import com.andysmods.crazyores.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ModelProvider extends FabricModelProvider {

	private static final Identifier ITEM_BUCKET_TEXTURE_LOCATION = new Identifier(CrazyOres.MOD_INFO.modId(), "item/bucket/");
	private static final String ITEM_BUCKET_OVERLAY = "custom_bucket_overlay_";

	public ModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
		blockStateModelGenerator.registerNorthDefaultHorizontalRotation(BlockRegistry.COPPER_BUCKET_EMPTY_PLACEABLE);
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {
		final Identifier baseCopperBucketTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + "copper_bucket_empty");
		final Identifier baseCopperBucketEntityTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + "copper_bucket_empty_entityoffset");
		final Identifier waterOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "water");
		final Identifier lavaOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "lava");
		final Identifier milkOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "milk");
		final Identifier powderSnowOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "powder_snow");
		final Identifier axolotlOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "axolotl");
		final Identifier codOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "cod");
		final Identifier pufferfishOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "pufferfish");
		final Identifier salmonOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "salmon");
		final Identifier tadpoleOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "tadpole");
		final Identifier tropicalFishOverlayTexture = new Identifier(CrazyOres.MOD_INFO.modId(), ITEM_BUCKET_TEXTURE_LOCATION.getPath() + ITEM_BUCKET_OVERLAY + "tropical_fish");

		generateItemModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_EMPTY, baseCopperBucketTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_WATER, baseCopperBucketTexture, waterOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_LAVA, baseCopperBucketTexture, lavaOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_MILK, baseCopperBucketTexture, milkOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_POWDER_SNOW, baseCopperBucketTexture, powderSnowOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_AXOLOTL, baseCopperBucketEntityTexture, axolotlOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_COD, baseCopperBucketEntityTexture, codOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_PUFFERFISH, baseCopperBucketEntityTexture, pufferfishOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_SALMON, baseCopperBucketEntityTexture, salmonOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_TADPOLE, baseCopperBucketEntityTexture, tadpoleOverlayTexture);
		generateBucketModel(itemModelGenerator, ItemRegistry.COPPER_BUCKET_TROPICAL_FISH, baseCopperBucketEntityTexture, tropicalFishOverlayTexture);
	}

	private void generateItemModel(ItemModelGenerator itemModelGenerator, Item item, Identifier textureLocation) {
		Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(textureLocation), itemModelGenerator.writer);
	}

	private void generateBucketModel(ItemModelGenerator itemModelGenerator, Item bucketItem, Identifier emptyBucketTextureLocation, Identifier overlayBucketTextureLocation) {
		Models.GENERATED_TWO_LAYERS.upload(ModelIds.getItemModelId(bucketItem), TextureMap.layered(emptyBucketTextureLocation, overlayBucketTextureLocation), itemModelGenerator.writer);
	}
}
