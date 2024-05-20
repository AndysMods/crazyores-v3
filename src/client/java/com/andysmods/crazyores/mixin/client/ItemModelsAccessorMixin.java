package com.andysmods.crazyores.mixin.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemModels.class)
public interface ItemModelsAccessorMixin {

	@Accessor("models")
	Int2ObjectMap<BakedModel> getModels();

	@Invoker("getModelId")
	static int invokeGetModelId(Item item) {
		throw new NotImplementedException("Failed to invoke ItemModels.getModelId()");
	}
}
