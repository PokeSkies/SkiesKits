package com.pokeskies.skieskits.placeholders

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import net.minecraft.server.network.ServerPlayerEntity

interface IPlaceholderService {
    fun parsePlaceholders(player: ServerPlayerEntity, text: String, kitId: String, kit: Kit, kitData: KitData): String
}