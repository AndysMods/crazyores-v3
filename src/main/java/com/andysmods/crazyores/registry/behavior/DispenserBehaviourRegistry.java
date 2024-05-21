package com.andysmods.crazyores.registry.behavior;

import com.andysmods.api.RegistryInitializer;
import com.andysmods.api.bucket.Bucket;
import com.andysmods.crazyores.registry.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

public final class DispenserBehaviourRegistry implements RegistryInitializer {

	private static final ItemDispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

		@Override
		public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			FluidModificationItem fluidModificationItem = (FluidModificationItem)((Object)stack.getItem());
			BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
			ServerWorld world = pointer.getWorld();
			if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
				fluidModificationItem.onEmptied(null, world, stack, blockPos);

				if (stack.getItem() instanceof final Bucket bucketItem) {
					return bucketItem.getEmptyBucket(stack, null);
				}

				return new ItemStack(Items.BUCKET);
			}

			return this.fallbackBehavior.dispense(pointer, stack);
		}
	};

	@Override
	public void onInitializeRegistry() {
		registerDispenserBehaviorsForCopperBuckets();
	}

	private void registerDispenserBehaviorsForCopperBuckets() {
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_LAVA, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_WATER, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_POWDER_SNOW, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_SALMON, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_COD, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_PUFFERFISH, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_TROPICAL_FISH, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_AXOLOTL, DISPENSER_BEHAVIOR);
		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_TADPOLE, DISPENSER_BEHAVIOR);

		DispenserBlock.registerBehavior(ItemRegistry.COPPER_BUCKET_EMPTY, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				ItemStack itemStack;
				BlockPos blockPos;
				ServerWorld worldAccess = pointer.getWorld();
				BlockState blockState = worldAccess.getBlockState(blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING)));
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable) {
					itemStack = ((FluidDrainable)((Object)block)).tryDrainFluid(worldAccess, blockPos, blockState);

					if (itemStack.isEmpty()) {
						return super.dispenseSilently(pointer, stack);
					}
				} else {
					return super.dispenseSilently(pointer, stack);
				}
				worldAccess.emitGameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

				final Fluid theFluid = blockState.getFluidState().getFluid();
				if (stack.getItem() instanceof final Bucket bucketItem) {
					itemStack = bucketItem.getFilledBucket(stack, theFluid);
				}

				Item item = itemStack.getItem();
				stack.decrement(1);
				if (stack.isEmpty()) {
					return new ItemStack(item);
				}
				if (((DispenserBlockEntity)pointer.getBlockEntity()).addToFirstFreeSlot(new ItemStack(item)) < 0) {
					this.fallbackBehavior.dispense(pointer, new ItemStack(item));
				}
				return stack;
			}
		});
	}
}
