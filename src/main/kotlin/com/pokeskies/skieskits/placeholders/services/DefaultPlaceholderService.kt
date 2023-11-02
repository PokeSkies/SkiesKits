package com.pokeskies.skieskits.placeholders.services

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.placeholders.IPlaceholderService
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class DefaultPlaceholderService : IPlaceholderService {
    override fun parsePlaceholders(player: ServerPlayerEntity, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String {
        var returnText = text
            .replace("%player%", player.name.string)

        if (kitId != null && kit != null && kitData != null) {
            returnText = returnText
                .replace("%kit_name%", kit.getDisplayName(kitId))
                .replace("%kit_uses%", kitData.uses.toString())
                .replace("%kit_max_uses%", kit.maxUses.toString())
                .replace("%kit_cooldown%", Utils.getFormattedTime(kit.cooldown.toLong()))
                .replace("%kit_time_remaining%", Utils.getFormattedTime(kitData.getTimeRemaining(kit.cooldown)))
        }

        return returnText
    }
}