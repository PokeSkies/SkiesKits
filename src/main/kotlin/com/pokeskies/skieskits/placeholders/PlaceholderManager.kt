package com.pokeskies.skieskits.placeholders

import com.pokeskies.skiesguis.placeholders.services.DefaultPlaceholderService
import com.pokeskies.skiesguis.placeholders.services.ImpactorPlaceholderService
import com.pokeskies.skiesguis.placeholders.services.PlaceholderAPIService
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.placeholders.IPlaceholderService
import net.minecraft.server.network.ServerPlayerEntity

class PlaceholderManager {
    private val services: MutableList<IPlaceholderService> = mutableListOf()

    init {
        services.add(DefaultPlaceholderService())
        for (service in PlaceholderMod.values()) {
            if (service.isModPresent()) {
                services.add(getServiceForType(service))
            }
        }
    }

    fun parse(player: ServerPlayerEntity, text: String, kitId: String, kit: Kit, kitData: KitData): String {
        var returnValue = text
        for (service in services) {
            returnValue = service.parsePlaceholders(player, returnValue, kitId, kit, kitData)
        }
        return returnValue
    }

    private fun getServiceForType(placeholderMod: PlaceholderMod): IPlaceholderService {
        return when (placeholderMod) {
            PlaceholderMod.IMPACTOR -> ImpactorPlaceholderService()
            PlaceholderMod.PLACEHOLDERAPI -> PlaceholderAPIService()
        }
    }
}