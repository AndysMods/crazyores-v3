package com.andysmods.crazyores.mixin.entity;

import com.andysmods.crazyores.registry.ItemRegistry;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AxolotlEntity.class)
public class AxolotlEntityMixin {

	@Inject(method = "eat", at = @At("HEAD"), cancellable = true)
	public void injectEat(final PlayerEntity player, final Hand hand, final ItemStack stack, CallbackInfo callbackInfo) {
		if (stack.isOf(ItemRegistry.COPPER_BUCKET_TROPICAL_FISH)) {
			final ItemStack returnedBucket = new ItemStack(ItemRegistry.COPPER_BUCKET_WATER);
			player.setStackInHand(hand, returnedBucket);
			callbackInfo.cancel();
		}
	}
}
