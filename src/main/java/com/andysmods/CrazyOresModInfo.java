package com.andysmods;

import com.andysmods.api.ModInfo;

public record CrazyOresModInfo(String modId) implements ModInfo {
	@Override
	public String getModId() {
		return this.modId;
	}
}
