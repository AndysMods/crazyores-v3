package com.andysmods.crazyores.registry;

import com.andysmods.api.RegistryInitializer;
import com.andysmods.api.bucket.Bucket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Predicate;

public final class CauldronBehaviorRegistry implements RegistryInitializer {

	private static final CauldronBehavior FILL_WITH_LAVA = (state, world, pos, player, hand, stack) ->
		fillCauldron(world, pos, player, hand, stack, Blocks.LAVA_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY_LAVA);

	private static final CauldronBehavior FILL_WITH_WATER = (state, world, pos, player, hand, stack) ->
		fillCauldron(world, pos, player, hand, stack, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);

	private static final CauldronBehavior FILL_WITH_POWDER_SNOW = (state, world, pos, player, hand, stack) ->
		fillCauldron(world, pos, player, hand, stack, Blocks.POWDER_SNOW_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW);

	@Override
	public void onInitializeRegistry() {
		registerCauldronBehaviorsForCopperBuckets();
	}

	private void registerCauldronBehaviorsForCopperBuckets() {
		CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_LAVA, FILL_WITH_LAVA);
		CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_WATER, FILL_WITH_WATER);
		CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_POWDER_SNOW, FILL_WITH_POWDER_SNOW);

		CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_LAVA, FILL_WITH_LAVA);
		CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_WATER, FILL_WITH_WATER);
		CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_POWDER_SNOW, FILL_WITH_POWDER_SNOW);

		CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_LAVA, FILL_WITH_LAVA);
		CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_WATER, FILL_WITH_WATER);
		CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_POWDER_SNOW, FILL_WITH_POWDER_SNOW);

		CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_LAVA, FILL_WITH_LAVA);
		CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_WATER, FILL_WITH_WATER);
		CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_POWDER_SNOW, FILL_WITH_POWDER_SNOW);

		CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_EMPTY, (state, world, pos, player, hand, stack) -> emptyCauldron(state, world, pos, player, hand, stack, blockState -> true, SoundEvents.ITEM_BUCKET_FILL_LAVA));
		CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_EMPTY, (state, world, pos, player, hand, stack) -> emptyCauldron(state, world, pos, player, hand, stack, blockState -> blockState.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
		CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR.put(ItemRegistry.COPPER_BUCKET_EMPTY, (state, world, pos, player, hand, stack) -> emptyCauldron(state, world, pos, player, hand, stack, blockState -> blockState.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW));
	}

	private static ActionResult fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
		final boolean bIsClient = world.isClient;
		if (bIsClient) {
			return ActionResult.success(bIsClient);
		}

		final Item item = stack.getItem();
		final ItemStack returningStack = (item instanceof final Bucket bucket) ? bucket.getEmptyBucket(stack, player) : new ItemStack(Items.BUCKET);

		player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, returningStack));
		player.incrementStat(Stats.FILL_CAULDRON);
		player.incrementStat(Stats.USED.getOrCreateStat(item));
		world.setBlockState(pos, state);
		world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
		world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

		return ActionResult.success(bIsClient);
	}

	private static ActionResult emptyCauldron(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, Predicate<BlockState> predicate, SoundEvent soundEvent) {
		final boolean bIsClient = world.isClient;
		if (bIsClient) {
			return ActionResult.success(bIsClient);
		}

		final ItemStack initialStack = stack.copy();
		final Item item = initialStack.getItem();

		ItemStack returnedStack = getReturnedStack(state.getBlock(), initialStack);
		if (returnedStack.isEmpty()) {
			returnedStack = stack;
		}

		final ItemStack returnedStackCopy = returnedStack.copy();
		ActionResult result = CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, returnedStack, predicate, soundEvent);
		if (result.isAccepted()) {
			if (item instanceof final Bucket bucket) {
				bucket.playSoundOnFill(player, returnedStackCopy);
			}
		}

		return result;
	}

	private static ItemStack getReturnedStack(Block block, ItemStack originalStack) {
		// TODO: Change this to use tags maybe? Would be nice to know if any type of block is a fillable (for example when our buckets become placeable).
		final boolean bIsWaterCauldron = Blocks.WATER_CAULDRON.equals(block);
		final boolean bIsLavaCauldron = Blocks.LAVA_CAULDRON.equals(block);
		final boolean bIsPowderSnowCauldron = Blocks.POWDER_SNOW_CAULDRON.equals(block);

		final Item originalItem = originalStack.getItem();

		if (!(originalItem instanceof final Bucket bucket)) {
			final Item returnedItem = bIsWaterCauldron ? Items.WATER_BUCKET : bIsLavaCauldron ? Items.LAVA_BUCKET : bIsPowderSnowCauldron ? Items.POWDER_SNOW_BUCKET : originalItem;
			return new ItemStack(returnedItem);
		}

		ItemStack returnedStack;
		if (bIsWaterCauldron) {
			returnedStack = bucket.getFilledBucket(originalStack, Fluids.WATER);
		}
		else if (bIsLavaCauldron) {
			returnedStack = bucket.getFilledBucket(originalStack, Fluids.LAVA);
		}
		else if (bIsPowderSnowCauldron) {
			returnedStack = bucket.getFilledBucket(originalStack, Blocks.POWDER_SNOW);
		}
		else {
			returnedStack = originalStack;
		}

		return returnedStack;
	}
}
