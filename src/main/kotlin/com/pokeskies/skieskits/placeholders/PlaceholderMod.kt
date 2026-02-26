package com.pokeskies.skieskits.placeholders

import net.fabricmc.loader.api.FabricLoader

enum class PlaceholderMod(val modId: String) {
    PLACEHOLDERAPI("placeholder-api"),
    MINIPLACEHOLDERS("miniplaceholders");

    fun isModPresent() : Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }
}
