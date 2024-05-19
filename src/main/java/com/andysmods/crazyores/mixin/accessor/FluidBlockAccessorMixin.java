package com.andysmods.crazyores.mixin.accessor;

import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidBlock.class)
public interface FluidBlockAccessorMixin {

	@Accessor("fluid")
	FlowableFluid getFluid();
}
