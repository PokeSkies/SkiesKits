package com.pokeskies.skieskits.placeholders.type.player

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.placeholders.GenericResult
import com.pokeskies.skieskits.placeholders.PlayerPlaceholder
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.level.ServerPlayer

class IsAvailablePlaceholder : PlayerPlaceholder {
    override fun handle(player: ServerPlayer, args: List<String>): GenericResult {
        if (args.isEmpty()) {
            return GenericResult.invalid("No Kit Specified")
        }

        val kitId = args[0]
        val kit = ConfigManager.KITS[kitId] ?: return GenericResult.invalid("Kit Not Found")

        if (SkiesKits.INSTANCE.storage == null) {
            Utils.printError("Returned player data is null! Cannot claim kit $kitId for player ${player.name.string}!")
            return GenericResult.invalid("Storage Error")
        }

        val userdata = SkiesKits.INSTANCE.storage?.getUser(player.uuid)
        if (userdata == null) {
            Utils.printError("Userdata for ${player.uuid} not found when processing placeholder. This should not happen.")
            return GenericResult.invalid("Storage Error")
        }

        val kitData = if (userdata.kits.containsKey(kitId)) userdata.kits[kitId]!! else KitData()

        return GenericResult.valid(kitData.canClaimUses(kit.maxUses) && kitData.canClaimCooldown(kit.cooldown))
    }

    override fun id(): List<String> = listOf("is_available")
}
