package com.andysmods.crazyores.mixin.entity;

import com.andysmods.api.bucket.Bucket;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AxolotlEntity.class, FishEntity.class})
public class BucketableEntityMixin {

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	public void injectInteractMob(final PlayerEntity player, final Hand hand, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
		final LivingEntity livingEntityThis = (LivingEntity) (Object) this;
		final Bucketable bucketableThis = (Bucketable) livingEntityThis;
		final ItemStack stackInHand = player.getStackInHand(hand);
		final Item itemInHand = stackInHand.getItem();

		if (livingEntityThis.isAlive() && itemInHand instanceof final Bucket bucket) {
			final ItemStack filledBucket = bucket.getFilledBucket(stackInHand, livingEntityThis);

			if (!filledBucket.isEmpty()) {
				livingEntityThis.playSound(bucketableThis.getBucketFillSound(), 1.0f, 1.0f);
				bucketableThis.copyDataToStack(filledBucket);

				final ItemStack exchangedStack = ItemUsage.exchangeStack(stackInHand, player, filledBucket, false);
				player.setStackInHand(hand, exchangedStack);

				final World world = livingEntityThis.getWorld();
				if (!world.isClient) {
					Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity) player, filledBucket);
				}

				livingEntityThis.discard();
				callbackInfoReturnable.setReturnValue(ActionResult.success(world.isClient));
				callbackInfoReturnable.cancel();
			}
		}
	}
}
