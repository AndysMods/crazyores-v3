package com.andysmods.api.bucket;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

/**
 * Heavily inspired by Isabel Maskrey's <a href="https://github.com/satyrnidae/early-game-buckets/">Early Game Buckets</a> mod. <3
 */
public interface Bucket {

	/**
	 * Gets an item stack which will be exchanged when the player empties a bucket.
	 */
	ItemStack getEmptyBucket(final ItemStack filledBucket, final PlayerEntity player);

	/**
	 * Attempts to fill the bucket with the given fluid.
	 */
	ItemStack getFilledBucket(final ItemStack emptyBucket, final Fluid fluid);

	/**
	 * Attempts to fill the bucket with the given block.
	 */
	ItemStack getFilledBucket(final ItemStack emptyBucket, final Block block);

	/**
	 * Attempts to fill the bucket with the given entity.
	 */
	ItemStack getFilledBucket(final ItemStack emptyBucket, final LivingEntity entity);

	/**
	 * Attempts to fill the bucket with milk.
	 */
	ItemStack getMilkBucket(final ItemStack emptyBucket);

	/**
	 * Gets the fluid in the bucket.
	 */
	Fluid getFluid();

	/**
	 * Plays a sound based on the sound that was returned after fill.
	 */
	default void playSoundOnFill(PlayerEntity player, ItemStack filledBucket) {
	}
}
