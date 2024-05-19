package com.andysmods.crazyores.mixin.entity;

import com.andysmods.api.bucket.Bucket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntity.class)
public class CowEntityMixin {

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void injectInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
		final LivingEntity livingEntityThis = (LivingEntity) (Object) this;
		final ItemStack stackInHand = player.getStackInHand(hand);
		final Item itemInHand = stackInHand.getItem();

		if (itemInHand instanceof final Bucket bucket && !livingEntityThis.isBaby()) {
			final ItemStack milkBucket = bucket.getMilkBucket(stackInHand);
			if (!milkBucket.isEmpty()) {
				player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0f, 1.0f);

				final ItemStack exchangedStack = ItemUsage.exchangeStack(stackInHand, player, milkBucket);
				player.setStackInHand(hand, exchangedStack);
				callbackInfoReturnable.setReturnValue(ActionResult.success(livingEntityThis.getWorld().isClient));
				callbackInfoReturnable.cancel();
			}
		}
	}
}
