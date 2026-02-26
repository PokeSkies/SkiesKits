package com.pokeskies.skieskits.placeholders

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.placeholders.services.DefaultPlaceholderService
import com.pokeskies.skieskits.placeholders.services.MiniPlaceholdersService
import com.pokeskies.skieskits.placeholders.services.PlaceholderAPIService
import net.minecraft.server.level.ServerPlayer

class PlaceholderManager {
    private val services: MutableList<IPlaceholderService> = mutableListOf()

    init {
        services.add(DefaultPlaceholderService())
        for (service in PlaceholderMod.entries) {
            if (service.isModPresent()) {
                services.add(getServiceForType(service))
            }
        }
    }

    fun parse(player: ServerPlayer, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String {
        var returnValue = text
        for (service in services) {
            returnValue = service.parsePlaceholders(player, returnValue, kitId, kit, kitData)
        }
        return returnValue
    }

    private fun getServiceForType(placeholderMod: PlaceholderMod): IPlaceholderService {
        return when (placeholderMod) {
            PlaceholderMod.PLACEHOLDERAPI -> PlaceholderAPIService()
            PlaceholderMod.MINIPLACEHOLDERS -> MiniPlaceholdersService()
        }
    }
}
