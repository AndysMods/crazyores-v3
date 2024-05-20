package com.andysmods.crazyores.mixin.client;

import com.andysmods.crazyores.CrazyOres;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayVertexConsumer.class)
public class OverlayVertexConsumerMixin {

	@Inject(method = "next", at = @At("HEAD"), cancellable = true)
	public void injectNext(CallbackInfo callbackInfo) {

//		CrazyOres.CRAZYORES_LOGGER.info("TEST");
		OverlayVertexConsumerAccessorMixin thisAccessor = (OverlayVertexConsumerAccessorMixin) (Object) this;
		Vector3f vector3f = thisAccessor.getInverseNormalMatrix().transform(new Vector3f(thisAccessor.getNormalX(), thisAccessor.getNormalY(), thisAccessor.getNormalZ()));
		Direction direction = Direction.getFacing(vector3f.x(), vector3f.y(), vector3f.z());
		Vector4f vector4f = thisAccessor.getInverseTextureMatrix().transform(new Vector4f(thisAccessor.getX(), thisAccessor.getY(), thisAccessor.getZ(), 1.0f));
		vector4f.rotateY((float)Math.PI);
		vector4f.rotateX(-1.5707964f);
		vector4f.rotate(direction.getRotationQuaternion());
		float f = -vector4f.x() * thisAccessor.getTextureScale();
		float g = -vector4f.y() * thisAccessor.getTextureScale();
		thisAccessor.getDelegate().vertex(thisAccessor.getX(), thisAccessor.getY(), thisAccessor.getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).texture(f, g).overlay(thisAccessor.getU(), thisAccessor.getV()).light(thisAccessor.getLight()).normal(thisAccessor.getNormalX(), thisAccessor.getNormalY(), thisAccessor.getNormalZ()).next();
		thisAccessor.invokeInit();

		callbackInfo.cancel();
	}
}
