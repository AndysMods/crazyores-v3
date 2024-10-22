package com.andysmods.crazyores;

import com.andysmods.CrazyOresModInfo;
import com.andysmods.api.IModInitializer;

import com.andysmods.crazyores.registry.ItemRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrazyOres implements IModInitializer {

	public static final CrazyOresModInfo MOD_INFO = new CrazyOresModInfo("crazyores");
	public static final Logger CRAZYORES_LOGGER = LoggerFactory.getLogger(MOD_INFO.modId());

	private static final ItemRegistry itemRegistry = new ItemRegistry();

	@Override
	public void onInitialize() {
		CRAZYORES_LOGGER.info("Hello, CrazyOres!");

		itemRegistry.onInitializeRegistry();
	}
}