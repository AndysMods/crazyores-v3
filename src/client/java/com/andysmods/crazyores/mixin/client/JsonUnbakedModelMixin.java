package com.andysmods.crazyores.mixin.client;

import com.andysmods.crazyores.CrazyOres;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.render.model.json.JsonUnbakedModel.PARTICLE_KEY;

@Mixin(JsonUnbakedModel.class)
public class JsonUnbakedModelMixin {

	@Inject(method = "bake(Lnet/minecraft/client/render/model/Baker;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
	public void injectBake(Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth, CallbackInfoReturnable<BakedModel> callbackInfoReturnable) {

		JsonUnbakedModel accessorThis = (JsonUnbakedModel) (Object) this;

		JsonUnbakedModelAccessorMixin accessorMixin = (JsonUnbakedModelAccessorMixin) accessorThis;

		final Sprite sprite = textureGetter.apply(accessorThis.resolveSprite(PARTICLE_KEY));
		if (accessorThis.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
			callbackInfoReturnable.setReturnValue(new BuiltinBakedModel(accessorThis.getTransformations(), accessorMixin.invokeCompileOverrides(baker, parent), sprite, accessorThis.getGuiLight().isSide()));
			callbackInfoReturnable.cancel();
		}
		BasicBakedModel.Builder builder = new BasicBakedModel.Builder(accessorThis, accessorMixin.invokeCompileOverrides(baker, parent), hasDepth).setParticle(sprite);
		for (ModelElement modelElement : accessorThis.getElements()) {
			for (Direction direction : modelElement.faces.keySet()) {
				ModelElementFace modelElementFace = modelElement.faces.get(direction);
				Sprite sprite2 = textureGetter.apply(accessorThis.resolveSprite(modelElementFace.textureId));

				// Determine the layer and apply an offset if necessary
				int layerIndex = getLayerIndexFromTextureId(modelElementFace.textureId);
				ModelElement elementToBake = modelElement;

				CrazyOres.CRAZYORES_LOGGER.info("modelElementFace.textureId: " + modelElementFace.textureId);

				if (layerIndex != 0) {
					elementToBake = createOffsetElement(modelElement, layerIndex * 0.001f);
				}

				if (modelElementFace.cullFace == null) {
					builder.addQuad(JsonUnbakedModelAccessorMixin.createQuad(elementToBake, modelElementFace, sprite2, direction, settings, id));
					continue;
				}
				builder.addQuad(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace), JsonUnbakedModelAccessorMixin.createQuad(elementToBake, modelElementFace, sprite2, direction, settings, id));
			}
		}

		callbackInfoReturnable.setReturnValue(builder.build());
		callbackInfoReturnable.cancel();
	}

	private int getLayerIndexFromTextureId(String textureId) {
		// Implement logic to determine the layer based on textureId
		// For example, you might use naming conventions like "layer0" or "layer1"

		switch (textureId) {
			case "layer1" -> {
				return 1;
			}
			case "layer2" -> {
				return 2;
			}
			case "layer3" -> {
				return 3;
			}
			default -> {
				return 0;
			}
		}
	}

	private ModelElement createOffsetElement(ModelElement element, float scale) {

		// Calculate the center of the element
//		Vector3f center = new Vector3f(
//			(element.from.x + element.to.x) / 2,
//			(element.from.y + element.to.y) / 2,
//			(element.from.z + element.to.z) / 2
//		);

		// Scale the from and to coordinates relative to the center
//		Vector3f from = scaleVec(element.from, center, scale);
//		Vector3f to = scaleVec(element.to, center, scale);

		final Vector3f from = new Vector3f(element.from.x + scale, element.from.y + scale, element.from.z + scale);
		final Vector3f to = new Vector3f(element.to.x + scale, element.to.y + scale, element.to.z + scale);

		Map<Direction, ModelElementFace> faces = element.faces;
		Map<Direction, ModelElementFace> offsetFaces = new HashMap<>();

		// Create new ModelElementFaces with the same data, as they are immutable
		for (Map.Entry<Direction, ModelElementFace> entry : faces.entrySet()) {
			Direction direction = entry.getKey();
			ModelElementFace face = entry.getValue();
			offsetFaces.put(direction, new ModelElementFace(face.cullFace, face.tintIndex, face.textureId, face.textureData));
		}

		return new ModelElement(from, to, offsetFaces, element.rotation, element.shade);
	}

	private Vector3f scaleVec(Vector3f vec, Vector3f center, float scale) {
		return new Vector3f(
			center.x + (vec.x - center.x) * scale,
			center.y + (vec.y - center.y) * scale,
			center.z + (vec.z - center.z) * scale
		);
	}
}
