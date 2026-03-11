package com.pokeskies.skieskits.placeholders

import com.pokeskies.skieskits.placeholders.services.ImpactorPlaceholderService
import com.pokeskies.skieskits.placeholders.services.MiniPlaceholdersService
import com.pokeskies.skieskits.placeholders.services.PlaceholderAPIService
import net.fabricmc.loader.api.FabricLoader

enum class PlaceholderServiceType(val id: String, val clazz: Class<out IPlaceholderService>) {
    MINIPLACEHOLDERS("miniplaceholders", MiniPlaceholdersService::class.java),
    PLACEHOLDERAPI("placeholder-api", PlaceholderAPIService::class.java),
    IMPACTOR("impactor", ImpactorPlaceholderService::class.java);

    fun isModPresent() : Boolean {
        return FabricLoader.getInstance().isModLoaded(id)
    }
}
