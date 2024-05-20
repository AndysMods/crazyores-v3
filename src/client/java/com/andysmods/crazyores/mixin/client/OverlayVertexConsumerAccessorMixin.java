package com.andysmods.crazyores.mixin.client;

import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OverlayVertexConsumer.class)
public interface OverlayVertexConsumerAccessorMixin {

	@Invoker("init")
	void invokeInit();

	@Accessor("light")
	int getLight();

	@Accessor("v1")
	int getV();

	@Accessor("u1")
	int getU();

	@Accessor("delegate")
	VertexConsumer getDelegate();

	@Accessor("textureScale")
	float getTextureScale();

	@Accessor("x")
	float getX();

	@Accessor("y")
	float getY();

	@Accessor("z")
	float getZ();

	@Accessor("normalX")
	float getNormalX();

	@Accessor("normalY")
	float getNormalY();

	@Accessor("normalZ")
	float getNormalZ();

	@Accessor("inverseNormalMatrix")
	Matrix3f getInverseNormalMatrix();

	@Accessor("inverseTextureMatrix")
	Matrix4f getInverseTextureMatrix();
}
