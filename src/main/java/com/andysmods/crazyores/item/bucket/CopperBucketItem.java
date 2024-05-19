package com.andysmods.crazyores.item.bucket;

import com.andysmods.api.bucket.CustomBucketItem;
import com.andysmods.crazyores.registry.ItemRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;

public class CopperBucketItem extends CustomBucketItem {
	public CopperBucketItem(Fluid fluid, Settings settings) {
		super(fluid, settings);
	}

	@Override
	public ItemStack getFilledBucket(final ItemStack emptyBucket, final Fluid forFluid) {
		if (forFluid.isIn(FluidTags.WATER)) {
			return new ItemStack(ItemRegistry.COPPER_BUCKET_WATER);
		}
		else if (forFluid.isIn(FluidTags.LAVA)) {
			return new ItemStack(ItemRegistry.COPPER_BUCKET_LAVA);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getFilledBucket(final ItemStack emptyBucket, final LivingEntity entity) {
		if (!this.fluid.isIn(FluidTags.WATER)) {
			return ItemStack.EMPTY;
		}

		final EntityType<?> entityType = entity.getType();
		final Item entityBucketItem = ItemRegistry.COPPER_ENTITY_BUCKETS.getOrDefault(entityType, null);
		return entityBucketItem != null ? new ItemStack(entityBucketItem) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getMilkBucket(final ItemStack emptyBucket) {
		if (!Fluids.EMPTY.equals(this.fluid)) {
			return ItemStack.EMPTY;
		}

		return new ItemStack(ItemRegistry.COPPER_BUCKET_MILK);
	}

	@Override
	public Item getPowderSnowFilledItem() {
		return ItemRegistry.COPPER_BUCKET_POWDER_SNOW;
	}

	@Override
	public Item getEmptyItem() {
		return ItemRegistry.COPPER_BUCKET_EMPTY;
	}
}
