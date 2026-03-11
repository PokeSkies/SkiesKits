package com.pokeskies.skieskits.placeholders

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import net.minecraft.server.level.ServerPlayer

interface IPlaceholderService {
    fun registerPlayer(placeholder: PlayerPlaceholder)
    fun registerServer(placeholder: ServerPlaceholder)
    fun finalizeRegister()
    fun parse(player: ServerPlayer, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String
}
