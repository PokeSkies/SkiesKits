package com.pokeskies.skieskits.config.actions.types

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CommandPlayer(
    type: ActionType = ActionType.COMMAND_PLAYER,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val commands: List<String> = emptyList(),
    @SerializedName("permission_level")
    private val permissionLevel: Int? = null
) : Action(type, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        if (SkiesKits.INSTANCE.server?.commandManager == null) {
            Utils.printError("There was an error while executing an action for player ${player.name}: Server was somehow null on command execution?")
            return
        }

        var source = player.commandSource

        if (permissionLevel != null) {
            source = source.withLevel(permissionLevel)
        }

        for (command in commands) {
            SkiesKits.INSTANCE.server?.commandManager?.executeWithPrefix(
                source,
                Utils.parsePlaceholders(player, command, kitId, kit, kitData)
            )
        }
    }

    override fun toString(): String {
        return "CommandPlayer(type=$type, requirements=$requirements, commands=$commands)"
    }
}
