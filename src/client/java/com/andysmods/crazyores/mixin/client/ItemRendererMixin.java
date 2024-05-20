package com.andysmods.crazyores.mixin.client;

import com.andysmods.crazyores.CrazyOres;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(method = "renderBakedItemQuads", at = @At("HEAD"), cancellable = true)
	private void injectRenderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay, CallbackInfo callbackInfo) {

		final ItemRendererAccessorMixin accessorMixin = (ItemRendererAccessorMixin) (Object) this;
		final boolean bIsStackEmpty = stack.isEmpty();
		MatrixStack.Entry entry = matrices.peek();

		for (BakedQuad bakedQuad : quads) {

			int i = -1;
			if (!bIsStackEmpty && bakedQuad.hasColor()) {
				i = accessorMixin.getColors().getColor(stack, bakedQuad.getColorIndex());
			}

			float red = (float)(i >> 16 & 0xFF) / 255.0f;
			float green = (float)(i >> 8 & 0xFF) / 255.0f;
			float blue = (float)(i & 0xFF) / 255.0f;

//			entry.getPositionMatrix().scale(1.01f, 1.0f, 1.0f);

			vertices.quad(entry, bakedQuad, red, green, blue, light, overlay);
		}

		callbackInfo.cancel();
	}
}
