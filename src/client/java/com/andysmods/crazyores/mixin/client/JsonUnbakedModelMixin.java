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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.render.model.json.JsonUnbakedModel.PARTICLE_KEY;

@Mixin(JsonUnbakedModel.class)
public class JsonUnbakedModelMixin {

	@Inject(method = "bake(Lnet/minecraft/client/render/model/Baker;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
	public void injectBake(Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth, CallbackInfoReturnable<BakedModel> callbackInfoReturnable) {
		final JsonUnbakedModel accessorThis = (JsonUnbakedModel) (Object) this;
		final JsonUnbakedModelAccessorMixin accessorMixin = (JsonUnbakedModelAccessorMixin) accessorThis;
		final Sprite sprite = textureGetter.apply(accessorThis.resolveSprite(PARTICLE_KEY));

		if (accessorThis.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
			callbackInfoReturnable.setReturnValue(new BuiltinBakedModel(accessorThis.getTransformations(), accessorMixin.invokeCompileOverrides(baker, parent), sprite, accessorThis.getGuiLight().isSide()));
			callbackInfoReturnable.cancel();
		}

		final BasicBakedModel.Builder builder = new BasicBakedModel.Builder(accessorThis, accessorMixin.invokeCompileOverrides(baker, parent), hasDepth).setParticle(sprite);
		final Vector3f modelCenter = calculateCenterBoundsForModel(accessorThis.getElements());

		for (ModelElement modelElement : accessorThis.getElements()) {
			for (Direction direction : modelElement.faces.keySet()) {

				ModelElementFace modelElementFace = modelElement.faces.get(direction);
				Sprite sprite2 = textureGetter.apply(accessorThis.resolveSprite(modelElementFace.textureId));

				// Determine the layer and apply an offset if necessary.
				final int layerIndex = getLayerIndexFromTextureId(modelElementFace.textureId);
				final boolean bScaleLayer = layerIndex != 0;
				final float scaleFactor = 1.0f + (layerIndex * 0.005f);
				ModelElement elementToBake = modelElement;

				if (bScaleLayer) {
					elementToBake = createOffsetElement(modelElement, modelCenter, scaleFactor);
				}

				BakedQuad theQuad = JsonUnbakedModelAccessorMixin.createQuad(elementToBake, modelElementFace, sprite2, direction, settings, id);
				if (bScaleLayer) {
					theQuad = scaleQuad(theQuad, modelCenter, scaleFactor);
				}

				if (modelElementFace.cullFace == null) {
					builder.addQuad(JsonUnbakedModelAccessorMixin.createQuad(elementToBake, modelElementFace, sprite2, direction, settings, id));
					continue;
				}

				builder.addQuad(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace), theQuad);
			}
		}

		callbackInfoReturnable.setReturnValue(builder.build());
		callbackInfoReturnable.cancel();
	}

	private int getLayerIndexFromTextureId(String textureId) {
		// TODO: Refactor so it can handle an arbitrary amount of layers.

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

	private ModelElement createOffsetElement(ModelElement element, Vector3f modelCenter, float scale) {

		// Scale the from and to coordinates relative to the center.
		final Vector3f from = scaleVector(element.from, modelCenter, scale);
		final Vector3f to = scaleVector(element.to, modelCenter, scale);

		final Map<Direction, ModelElementFace> faces = element.faces;
		final Map<Direction, ModelElementFace> offsetFaces = new HashMap<>();

		// Create new ModelElementFaces with the same data, as they are immutable.
		for (Map.Entry<Direction, ModelElementFace> entry : faces.entrySet()) {
			Direction direction = entry.getKey();
			ModelElementFace face = entry.getValue();
			offsetFaces.put(direction, new ModelElementFace(face.cullFace, face.tintIndex, face.textureId, face.textureData));
		}

		return new ModelElement(from, to, offsetFaces, element.rotation, element.shade);
	}

	private Vector3f scaleVector(Vector3f vec, Vector3f modelCenter, float scale) {
		return new Vector3f(
			modelCenter.x + (vec.x - modelCenter.x) * scale,
			modelCenter.y + (vec.y - modelCenter.y) * scale,
			modelCenter.z + (vec.z - modelCenter.z) * scale
		);
	}

	private BakedQuad scaleQuad(BakedQuad quad, Vector3f modelCenter, float scale) {
		int[] vertexData = quad.getVertexData().clone();
		for (int i = 0; i < vertexData.length; i += 8) {
			float x = Float.intBitsToFloat(vertexData[i]);
			float y = Float.intBitsToFloat(vertexData[i + 1]);
			float z = Float.intBitsToFloat(vertexData[i + 2]);

			x = modelCenter.x + (x - modelCenter.x) * scale;
			y = modelCenter.y + (y - modelCenter.y) * scale;
			z = modelCenter.z + (z - modelCenter.z) * scale;

			vertexData[i] = Float.floatToIntBits(x);
			vertexData[i + 1] = Float.floatToIntBits(y);
			vertexData[i + 2] = Float.floatToIntBits(z);
		}

		return new BakedQuad(vertexData, quad.getColorIndex(), quad.getFace(), quad.getSprite(), quad.hasShade());
	}

	private Vector3f calculateCenterBoundsForModel(List<ModelElement> modelElements) {
		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;

		for (ModelElement modelElement : modelElements) {
			minX = Math.min(minX, modelElement.from.x);
			minY = Math.min(minY, modelElement.from.y);
			minZ = Math.min(minZ, modelElement.from.z);
			maxX = Math.max(maxX, modelElement.to.x);
			maxY = Math.max(maxY, modelElement.to.y);
			maxZ = Math.max(maxZ, modelElement.to.z);
		}

		return new Vector3f((minX + maxX) * 0.5f, (minY + maxY) * 0.5f, (minZ + maxZ) * 0.5f);
	}
}
