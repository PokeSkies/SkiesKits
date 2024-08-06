package com.pokeskies.skieskits.placeholders

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import net.minecraft.server.level.ServerPlayer

interface IPlaceholderService {
    fun parsePlaceholders(player: ServerPlayer, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String
}
