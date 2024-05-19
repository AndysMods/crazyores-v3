package com.andysmods.crazyores.item.bucket;

import com.andysmods.crazyores.registry.ItemRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CopperBucketItemOld extends Item implements FluidModificationItem {

	private final Fluid fluid;

	public CopperBucketItemOld(Fluid fluid, Settings settings) {
		super(settings);
		this.fluid = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		final ItemStack itemInHand = user.getStackInHand(hand);
		final BlockHitResult blockHitResult = Item.raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);

		if (blockHitResult.getType() != HitResult.Type.BLOCK) {
			return TypedActionResult.pass(itemInHand);
		}

		final BlockPos hitResultPosition = blockHitResult.getBlockPos();
		final Direction hitResultDirection = blockHitResult.getSide();
		final BlockPos targetBlockPosition = hitResultPosition.offset(hitResultDirection);

		if (!world.canPlayerModifyAt(user, hitResultPosition) || !user.canPlaceOn(targetBlockPosition, hitResultDirection, itemInHand)) {
			return TypedActionResult.fail(itemInHand);
		}

		final BlockState blockStateAtHitResult = world.getBlockState(hitResultPosition);
		final Block blockAtHitResult = blockStateAtHitResult.getBlock();

		if (this.fluid == Fluids.EMPTY) {
			final boolean bIsFluidDrainable = blockAtHitResult instanceof FluidDrainable;

			if (!bIsFluidDrainable) {
				return TypedActionResult.fail(itemInHand);
			}

			final FluidDrainable drainableAtHitResult = (FluidDrainable) blockAtHitResult;
			final ItemStack bucketItem = drainableAtHitResult.tryDrainFluid(world, hitResultPosition, blockStateAtHitResult);

			if (bucketItem.isEmpty()) {
				return TypedActionResult.fail(itemInHand);
			}

			final ItemStack convertedItem;
			if (bucketItem.getItem().equals(Items.WATER_BUCKET)) {
				convertedItem = new ItemStack(ItemRegistry.COPPER_BUCKET_WATER);
			}
			else if (bucketItem.getItem().equals(Items.LAVA_BUCKET)) {
				convertedItem = new ItemStack(ItemRegistry.COPPER_BUCKET_LAVA);
			}
			else {
				convertedItem = bucketItem;
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			drainableAtHitResult.getBucketFillSound().ifPresent(sound -> user.playSound(sound, 1.0f, 1.0f));
			world.emitGameEvent(user, GameEvent.FLUID_PICKUP, hitResultPosition);

			final ItemStack newBucket = ItemUsage.exchangeStack(itemInHand, user, convertedItem);

			if (!world.isClient) {
				Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, convertedItem);
			}

			return TypedActionResult.success(newBucket, world.isClient());
		}

		final boolean bIsFluidFillable = blockAtHitResult instanceof FluidFillable;
		final BlockPos fillablePosition = (bIsFluidFillable && this.fluid.isIn(FluidTags.WATER)) ? hitResultPosition : targetBlockPosition;
		final boolean bTryPlaceFluid = this.placeFluid(user, world, fillablePosition, blockHitResult);

		if (!bTryPlaceFluid) {
			return TypedActionResult.fail(itemInHand);
		}

		this.onEmptied(user, world, itemInHand, fillablePosition);

		if (user instanceof ServerPlayerEntity) {
			Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) user, fillablePosition, itemInHand);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));

		return TypedActionResult.success(getEmptiedStack(itemInHand, user), world.isClient());
	}

	public ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
		if (!player.getAbilities().creativeMode) {
			return new ItemStack(ItemRegistry.COPPER_BUCKET_EMPTY);
		}

		return stack;
	}

	@Override
	public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
		if (!(this.fluid instanceof FlowableFluid)) {
			return false;
		}

		final BlockState targetBlockState = world.getBlockState(pos);
		final Block targetBlock = targetBlockState.getBlock();
		final boolean bIsTargetFillable = targetBlock instanceof FluidFillable;
		final boolean bCanBucketPlace = targetBlockState.canBucketPlace(this.fluid);
		final boolean bBlockCanFillWithFluid = bIsTargetFillable && ((FluidFillable) targetBlock).canFillWithFluid(world, pos, targetBlockState, this.fluid);
		final boolean bCanPlaceFluidHere = targetBlockState.isAir() || bCanBucketPlace || bBlockCanFillWithFluid;

		if (!bCanPlaceFluidHere) {
			return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
		}

		if (world.getDimension().ultrawarm() && this.fluid.isIn(FluidTags.WATER)) {
			world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);

			final double x = pos.getX();
			final double y = pos.getY();
			final double z = pos.getZ();
			final int particleCount = 8;

			for (int i = 0; i < particleCount; ++i) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, x + Math.random(), y + Math.random(), z + Math.random(), 0.0, 0.0, 0.0);
			}

			return true;
		}

		if (this.fluid.isIn(FluidTags.WATER) && bIsTargetFillable) {
			final FlowableFluid flowable = (FlowableFluid) this.fluid;
		 	((FluidFillable) targetBlock).tryFillWithFluid(world, pos, targetBlockState, flowable.getStill(false));
			this.playEmptyingSound(player, world, pos);
			return true;
		}

		// Break the block before placing the liquid. For example if you're placing water on a sapling.
		final boolean isLiquid = !targetBlockState.getFluidState().isEmpty();
		if (!world.isClient && bCanBucketPlace && !isLiquid) {
			world.breakBlock(pos, true);
		}

		// Play the emptying bucket sound if you've successfully updated the target block state, or if you're emptying the bucket into already still liquid.
		final boolean bTryUpdateBlockState = world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
		final boolean bIsFluidStateAlreadyStill = targetBlockState.getFluidState().isStill();
		if (bTryUpdateBlockState || bIsFluidStateAlreadyStill) {
			this.playEmptyingSound(player, world, pos);
			return true;
		}

		return false;
	}

	protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
		final SoundEvent soundEvent = this.fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
		world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
		world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
	}
}
