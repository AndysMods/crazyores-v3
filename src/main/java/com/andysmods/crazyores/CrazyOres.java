package com.andysmods.crazyores;

import com.andysmods.CrazyOresModInfo;
import com.andysmods.crazyores.registry.BlockRegistry;
import com.andysmods.crazyores.registry.GroupRegistry;
import com.andysmods.crazyores.registry.behavior.CauldronBehaviorRegistry;
import com.andysmods.crazyores.registry.ItemRegistry;
import com.andysmods.crazyores.registry.behavior.DispenserBehaviourRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrazyOres implements ModInitializer {

	public static final CrazyOresModInfo MOD_INFO = new CrazyOresModInfo("crazyores");
	public static final Logger CRAZYORES_LOGGER = LoggerFactory.getLogger(MOD_INFO.modId());

	private static final BlockRegistry blockRegistry = new BlockRegistry();
	private static final ItemRegistry itemRegistry = new ItemRegistry();
	private static final GroupRegistry groupRegistry = new GroupRegistry();

	private static final CauldronBehaviorRegistry cauldronBehaviorRegistry = new CauldronBehaviorRegistry();
	private static final DispenserBehaviourRegistry dispenserBehaviorRegistry = new DispenserBehaviourRegistry();

	@Override
	public void onInitialize() {
		CRAZYORES_LOGGER.info("Hello, CrazyOres!");

		blockRegistry.initializeRegistry();
		itemRegistry.initializeRegistry();
		groupRegistry.initializeRegistry();

		cauldronBehaviorRegistry.initializeRegistry();
		dispenserBehaviorRegistry.initializeRegistry();
	}
}