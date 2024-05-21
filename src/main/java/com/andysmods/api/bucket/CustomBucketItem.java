package com.andysmods.api.bucket;

import com.andysmods.crazyores.mixin.accessor.FluidBlockAccessorMixin;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * Heavily inspired by Isabel Maskrey's <a href="https://github.com/satyrnidae/early-game-buckets/">Early Game Buckets</a> mod. <3
 */
public abstract class CustomBucketItem extends BucketItem implements Bucket {

	protected final Fluid fluid;

	protected CustomBucketItem(Fluid fluid, Settings settings) {
		super(fluid, settings);
		this.fluid = fluid;
	}

	@Override
	public ItemStack getEmptyBucket(ItemStack filledBucket, PlayerEntity player) {
		if (player != null && player.getAbilities().creativeMode) {
			return filledBucket;
		}

		return new ItemStack(getEmptyItem());
	}

	@Override
	public ItemStack getFilledBucket(ItemStack emptyBucket, Fluid fluid) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getFilledBucket(ItemStack emptyBucket, Block block) {
		if (!Fluids.EMPTY.equals(this.fluid)) {
			return ItemStack.EMPTY;
		}

		if (Blocks.POWDER_SNOW.equals(block)) {
			final Item powderSnowItem = this.getPowderSnowFilledItem();
			return powderSnowItem != null ? new ItemStack(powderSnowItem) : ItemStack.EMPTY;
		}

		if (block instanceof Waterloggable || block instanceof BubbleColumnBlock) {
			return this.getFilledBucket(emptyBucket, Fluids.WATER);
		}

		if (block instanceof final FluidBlock fluidBlock) {
			FlowableFluid flowableFluid = ((FluidBlockAccessorMixin) fluidBlock).getFluid();
			return this.getFilledBucket(emptyBucket, flowableFluid);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getFilledBucket(ItemStack emptyBucket, LivingEntity entity) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getMilkBucket(ItemStack emptyBucket) {
		return ItemStack.EMPTY;
	}

	@Override
	public Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		final ItemStack stackInHand = user.getStackInHand(hand);
		final BlockHitResult blockHitResult = raycast(world, user, Fluids.EMPTY.equals(this.fluid) ? FluidHandling.SOURCE_ONLY : FluidHandling.NONE);

		if (!Type.BLOCK.equals(blockHitResult.getType())) {
			return TypedActionResult.pass(stackInHand);
		}

		final BlockPos hitBlockPos = blockHitResult.getBlockPos();
		final Direction hitSide = blockHitResult.getSide();
		final BlockPos offsetByHitSideBlockPos = hitBlockPos.offset(hitSide);
		final BlockState hitBlockState = world.getBlockState(hitBlockPos);
		final boolean bCanPlayerModifyAtPosition = world.canPlayerModifyAt(user, hitBlockPos);
		final boolean bCanPlayerPlaceOn = user.canPlaceOn(offsetByHitSideBlockPos, hitSide, stackInHand);

		if (!bCanPlayerModifyAtPosition || !bCanPlayerPlaceOn) {
			return TypedActionResult.fail(stackInHand);
		}

		if (Fluids.EMPTY.equals(this.fluid)) {
			return this.tryToFillBucket(world, user, stackInHand, hitBlockPos, hitBlockState);
		}
		else {
			return this.tryToEmptyBucket(world, user, stackInHand, hitBlockPos, hitBlockState, offsetByHitSideBlockPos, blockHitResult);
		}
	}

	private TypedActionResult<ItemStack> tryToFillBucket(World world, PlayerEntity user, ItemStack stackInHand, BlockPos hitBlockPos, BlockState hitBlockState) {
		final Block hitBlock = hitBlockState.getBlock();
		if (!(hitBlock instanceof final FluidDrainable fluidDrainable)) {
			return TypedActionResult.fail(stackInHand);
		}

		final ItemStack filledBucket = getFilledBucket(stackInHand, hitBlock);
		if (filledBucket.isEmpty()) {
			return TypedActionResult.fail(stackInHand);
		}

		final ItemStack drainedFluid = fluidDrainable.tryDrainFluid(world, hitBlockPos, hitBlockState);
		if (drainedFluid.isEmpty()) {
			return TypedActionResult.fail(stackInHand);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		fluidDrainable.getBucketFillSound().ifPresent((sound) -> user.playSound(sound, 1.0f, 1.0f));
		this.playSoundOnFill(user, filledBucket);
		world.emitGameEvent(user, GameEvent.FLUID_PICKUP, hitBlockPos);

		if (!world.isClient) {
			Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity) user, filledBucket);
		}

		final ItemStack exchangedItemStack = ItemUsage.exchangeStack(stackInHand, user, filledBucket);
		return TypedActionResult.success(exchangedItemStack, world.isClient);
	}

	private TypedActionResult<ItemStack> tryToEmptyBucket(World world, PlayerEntity user, ItemStack stackInHand, BlockPos hitBlockPos, BlockState hitBlockState, BlockPos offsetByHitSideBlockPos, BlockHitResult blockHitResult) {
		final boolean bFluidIsWater = hitBlockState.getBlock() instanceof FluidFillable && Fluids.WATER.equals(this.fluid);
		final BlockPos placeFluidAtBlockPos = bFluidIsWater ? hitBlockPos : offsetByHitSideBlockPos;

		if (!this.placeFluid(user, world, placeFluidAtBlockPos, blockHitResult)) {
			return TypedActionResult.fail(stackInHand);
		}

		this.onEmptied(user, world, stackInHand, placeFluidAtBlockPos);
		user.incrementStat(Stats.USED.getOrCreateStat(this));

		final ItemStack emptyBucket = this.getEmptyBucket(stackInHand, user);
		if (user instanceof final ServerPlayerEntity serverPlayer) {
			Criteria.PLACED_BLOCK.trigger(serverPlayer, placeFluidAtBlockPos, stackInHand);
		}

		return TypedActionResult.success(emptyBucket, world.isClient);
	}

	public abstract Item getEmptyItem();

	public Item getPowderSnowFilledItem() {
		return null;
	}
}
