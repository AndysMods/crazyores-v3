package com.andysmods.api.bucket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomEntityBucketItem extends CustomBucketItem {

	private final Item emptyBucket;
	private final EntityType<?> entityType;
	private final SoundEvent emptyingSound;

	public CustomEntityBucketItem(Item emptyBucket, EntityType<?> entityType, SoundEvent emptyingSound, Fluid fluid, Settings settings) {
		super(fluid, settings);
		this.emptyBucket = emptyBucket;
		this.entityType = entityType;
		this.emptyingSound = emptyingSound;
	}

	@Override
	public void onEmptied(PlayerEntity player, World world, ItemStack stack, BlockPos pos) {
		if (!(world instanceof ServerWorld)) {
			return;
		}

		this.spawnEntity((ServerWorld) world, stack, pos);
		world.emitGameEvent(player, GameEvent.ENTITY_PLACE, pos);
	}

	@Override
	protected void playEmptyingSound(PlayerEntity player, WorldAccess world, BlockPos pos) {
		world.playSound(player, pos, this.emptyingSound, SoundCategory.NEUTRAL, 1.0f, 1.0f);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (this.entityType != EntityType.TROPICAL_FISH) {
			return;
		}

		final NbtCompound binaryTag = stack.getNbt();
		if (binaryTag == null || !binaryTag.contains("BucketVariantTag", 3)) {
			return;
		}

		final int variantTagIndex = binaryTag.getInt("BucketVariantTag");
		final Formatting[] formattings = new Formatting[] { Formatting.ITALIC, Formatting.GRAY };
		final String tropicalFishDyeColor = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(variantTagIndex);
		final String tropicalFishPatternColor = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(variantTagIndex);

		TropicalFishEntity.COMMON_VARIANTS.stream().filter((variant) -> variantTagIndex == variant.getId()).findFirst().ifPresent((variant) -> {
			tooltip.add(Text.translatable(TropicalFishEntity.getToolTipForVariant(variant.getId())).formatted(formattings));
		});

		tooltip.add(TropicalFishEntity.getVariety(variantTagIndex).getText().copyContentOnly().formatted(formattings));

		final MutableText mutableText = Text.translatable(tropicalFishDyeColor);
		if (!tropicalFishDyeColor.equals(tropicalFishPatternColor)) {
			mutableText.append(", ").append(Text.translatable(tropicalFishPatternColor));
		}

		mutableText.formatted(formattings);
		tooltip.add(mutableText);
	}

	private void spawnEntity(ServerWorld serverWorld, ItemStack stack, BlockPos pos) {
		final Entity entity = this.entityType.spawnFromItemStack(serverWorld, stack, null, pos, SpawnReason.BUCKET, true, false);

		if (entity instanceof final Bucketable bucketable) {
			bucketable.copyDataFromNbt(stack.getOrCreateNbt());
			bucketable.setFromBucket(true);
		}
	}

	@Override
	public Item getEmptyItem() {
		return this.emptyBucket;
	}
}
