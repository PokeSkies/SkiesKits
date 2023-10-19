package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CommandConsole(
    type: ActionType = ActionType.COMMAND_CONSOLE,
    requirements: RequirementOptions? = RequirementOptions(),
    private val commands: List<String> = emptyList()
) : Action(type, requirements) {
    override fun execute(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        if (SkiesKits.INSTANCE.server?.commandManager == null) {
            Utils.error("There was an error while executing an action for player ${player.name}: Server was somehow null on command execution?")
            return
        }

        for (command in commands) {
            SkiesKits.INSTANCE.server!!.commandManager.executeWithPrefix(
                SkiesKits.INSTANCE.server!!.commandSource,
                parsePlaceholders(player, command, kitId, kit, kitData)
            )
        }
    }

    override fun toString(): String {
        return "CommandConsole(type=$type, requirements=$requirements, commands=$commands)"
    }
}