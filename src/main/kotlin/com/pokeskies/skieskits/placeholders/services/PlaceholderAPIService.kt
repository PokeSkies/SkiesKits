package com.pokeskies.skieskits.placeholders.services

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.placeholders.IPlaceholderService
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class PlaceholderAPIService : IPlaceholderService {
    init {
        Utils.printInfo("PlaceholderAPI mod found! Enabling placeholder integration...")
    }
    override fun parsePlaceholders(player: ServerPlayer, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String {
        return Placeholders.parseText(Component.literal(text), PlaceholderContext.of(player)).string
    }
}
