package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MessageBroadcast(
    type: ActionType = ActionType.BROADCAST,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val message: List<String> = emptyList()
) : Action(type, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        if (SkiesKits.INSTANCE.adventure == null) {
            Utils.printError("There was an error while executing an action for player ${player.name}: Adventure was somehow null on message broadcast?")
            return
        }

        for (line in message) {
            SkiesKits.INSTANCE.adventure!!.all().sendMessage(Utils.deserializeText(
                Utils.parsePlaceholders(player, line, kitId, kit, kitData)
            ))
        }
    }

    override fun toString(): String {
        return "MessageBroadcast(type=$type, requirements=$requirements, message=$message)"
    }
}
