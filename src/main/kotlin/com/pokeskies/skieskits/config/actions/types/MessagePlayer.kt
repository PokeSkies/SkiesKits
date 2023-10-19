package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class MessagePlayer(
    type: ActionType = ActionType.MESSAGE,
    requirements: RequirementOptions? = RequirementOptions(),
    private val message: List<String> = emptyList()
) : Action(type, requirements) {
    override fun execute(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        for (line in message) {
            player.sendMessage(Utils.deseralizeText(parsePlaceholders(player, line, kitId, kit, kitData)))
        }
    }

    override fun toString(): String {
        return "MessagePlayer(type=$type, requirements=$requirements, message=$message)"
    }
}