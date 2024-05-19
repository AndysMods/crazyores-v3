package com.andysmods.api.bucket;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class CustomMilkBucketItem extends MilkBucketItem {

	private final Item emptyBucket;

	public CustomMilkBucketItem(Item emptyBucket, Settings settings) {
		super(settings);

		this.emptyBucket = emptyBucket;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof final ServerPlayerEntity serverPlayer) {
			Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
			serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
		}

		if (user instanceof final PlayerEntity player && !player.getAbilities().creativeMode) {
			stack.decrement(1);
		}

		if (!world.isClient) {
			user.clearStatusEffects();
		}

		final ItemStack emptyBucket = stack.isEmpty() ? new ItemStack(this.getEmptyItem()) : stack;
		return emptyBucket.isEmpty() ? ItemStack.EMPTY : emptyBucket;
	}

	private Item getEmptyItem() {
		return this.emptyBucket;
	}
}
