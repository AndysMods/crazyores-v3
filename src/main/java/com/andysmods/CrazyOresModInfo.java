package com.andysmods;

import com.andysmods.api.IModInfo;

public record CrazyOresModInfo(String modId) implements IModInfo {
	@Override
	public String getModId() {
		return this.modId;
	}
}
