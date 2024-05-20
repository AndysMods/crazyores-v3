package com.andysmods.crazyores.mixin.client;

import com.andysmods.crazyores.CrazyOres;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModels.class)
public class ItemModelsMixin {

	@Inject(method = "getModel(Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
	public void injectGetModels(Item item, CallbackInfoReturnable<BakedModel> callbackInfoReturnable) {
		final ItemModelsAccessorMixin itemModelsThis = (ItemModelsAccessorMixin) (Object) this;
		final BakedModel bakedModel = itemModelsThis.getModels().get(ItemModelsAccessorMixin.invokeGetModelId(item));

		if (bakedModel.equals(Models.GENERATED_TWO_LAYERS)) {
			CrazyOres.CRAZYORES_LOGGER.warn("This item is using two layer generation! Fix z-fighting issue!");
			callbackInfoReturnable.setReturnValue(bakedModel);
			callbackInfoReturnable.cancel();
		}
	}
}
