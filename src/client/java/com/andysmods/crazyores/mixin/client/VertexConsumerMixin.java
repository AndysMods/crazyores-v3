package com.andysmods.crazyores.mixin.client;

import com.andysmods.crazyores.CrazyOres;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin {

//	@Invoker("overlay")
//	VertexConsumer invokeOverlay(int var1, int var2);

//	@Inject(method = "overlay(I)Lnet/minecraft/client/render/VertexConsumer;", at = @At("HEAD"), cancellable = true)
//	default void injectOverlay(int uv, CallbackInfoReturnable<VertexConsumer> callbackInfoReturnable) {
//		CrazyOres.CRAZYORES_LOGGER.info("IS THIS GETTING CALLED");
//		callbackInfoReturnable.setReturnValue(this.invokeOverlay(0, 0));
//		callbackInfoReturnable.cancel();
//	}
}
