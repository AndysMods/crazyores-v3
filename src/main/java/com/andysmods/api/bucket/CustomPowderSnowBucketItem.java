package com.andysmods.api.bucket;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CustomPowderSnowBucketItem extends Item implements Bucket {

	private final Item emptyBucket;
	private final Block block;
	private final SoundEvent placeSound;

	public CustomPowderSnowBucketItem(Item emptyBucket, Block block, SoundEvent placeSound, Settings settings) {
		super(settings);
		this.emptyBucket = emptyBucket;
		this.block = block;
		this.placeSound = placeSound;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		final PlayerEntity player = context.getPlayer();

		if (player == null) {
			return ActionResult.FAIL;
		}

		final Hand hand = context.getHand();
		final ItemStack stackInHand = player.getStackInHand(hand);
		final ActionResult actionResult = this.place(new ItemPlacementContext(context));

		if (!actionResult.isAccepted() || player.isCreative()) {
			return actionResult;
		}

		final ItemStack emptyBucket = this.getEmptyBucket(stackInHand, player);
		player.setStackInHand(hand, emptyBucket);
		return actionResult;
	}

	private ActionResult place(ItemPlacementContext context) {
		if (!context.canPlace()) {
			return ActionResult.FAIL;
		}

		final BlockState placementState = this.getBlock().getPlacementState(context);
		if (placementState == null || !this.canPlace(context, placementState)) {
			return ActionResult.FAIL;
		}

		// TODO: Move this to some common util class.
		final int BLOCK_UPDATE = 1;
		final int SEND_TO_CLIENT = 2;
		final int REDRAW_ON_MAIN_THREAD = 8;
		final boolean bSuccessfullySetBlockState = context.getWorld().setBlockState(context.getBlockPos(), placementState, BLOCK_UPDATE | SEND_TO_CLIENT | REDRAW_ON_MAIN_THREAD);

		if (!bSuccessfullySetBlockState) {
			return ActionResult.FAIL;
		}

		final BlockPos blockPos = context.getBlockPos();
		final World world = context.getWorld();
		final PlayerEntity player = context.getPlayer();
		final ItemStack stack = context.getStack();
		final BlockState blockState = world.getBlockState(blockPos);

		if (blockState.isOf(placementState.getBlock())) {
			blockState.getBlock().onPlaced(world, blockPos, blockState, player, stack);

			if (player instanceof final ServerPlayerEntity serverPlayer) {
				Criteria.PLACED_BLOCK.trigger(serverPlayer, blockPos, stack);
			}
		}

		final BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
		world.playSound(player, blockPos, this.placeSound, SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
		world.emitGameEvent(player, GameEvent.BLOCK_PLACE, blockPos);

		if (player == null || !player.getAbilities().creativeMode) {
			stack.decrement(1);
		}

		return ActionResult.success(world.isClient);
	}

	private boolean canPlace(ItemPlacementContext context, BlockState state) {
		final PlayerEntity playerEntity = context.getPlayer();
		final ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
		final boolean bCanPlaceAtPos = state.canPlaceAt(context.getWorld(), context.getBlockPos());
		final boolean bDoesNotCollide = context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
		return bCanPlaceAtPos && bDoesNotCollide;
	}

	public Block getBlock() {
		return this.block;
	}

	@Override
	public ItemStack getEmptyBucket(ItemStack filledBucket, PlayerEntity player) {
		if (player.getAbilities().creativeMode) {
			return filledBucket;
		}

		return new ItemStack(this.emptyBucket);
	}

	@Override
	public ItemStack getFilledBucket(ItemStack emptyBucket, Fluid fluid) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getFilledBucket(ItemStack emptyBucket, Block block) {
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
		return Fluids.EMPTY;
	}
}
