package com.andysmods.crazyores.mixin.client;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JsonUnbakedModel.class)
public interface JsonUnbakedModelAccessorMixin {

	@Invoker("compileOverrides")
	ModelOverrideList invokeCompileOverrides(Baker baker, JsonUnbakedModel parent);

	@Invoker("createQuad")
	static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id) {
		throw new NotImplementedException("Failed to invoke JsonUnbakedModelAccessorMixin.createQuad()");
	}
}
