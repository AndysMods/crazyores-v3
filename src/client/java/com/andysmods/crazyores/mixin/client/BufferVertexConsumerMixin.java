package com.andysmods.crazyores.mixin.client;

import com.andysmods.crazyores.CrazyOres;
import net.minecraft.client.render.BufferVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BufferVertexConsumer.class)
public interface BufferVertexConsumerMixin {

	@Inject(method = "vertex", at = @At("HEAD"), cancellable = true)
	default void vertex(double x, double y, double z, CallbackInfoReturnable<VertexConsumer> callbackInfoReturnable) {
		x += 200;
		y += 200;
		z += 200;
	}
}
