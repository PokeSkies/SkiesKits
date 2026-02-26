package com.pokeskies.skieskits.config.actions.types

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.permissions.LevelBasedPermissionSet
import net.minecraft.server.permissions.PermissionLevel

class CommandPlayer(
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val commands: List<String> = emptyList(),
    @SerializedName("permission_level")
    private val permissionLevel: Int? = null
) : Action(ActionType.COMMAND_PLAYER, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String?, kit: Kit?, kitData: KitData?, gui: SimpleGui?, commandSourceOverride: CommandSourceStack?) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")

        var source = commandSourceOverride ?: player.createCommandSourceStack()

        if (permissionLevel != null) {
            val level = PermissionLevel.byId(permissionLevel.coerceIn(0, 4))
            source = source.withPermission(LevelBasedPermissionSet.forLevel(level))
        }

        for (command in commands) {
            SkiesKits.INSTANCE.server.commands?.performPrefixedCommand(
                source,
                Utils.parsePlaceholders(player, command, kitId, kit, kitData)
            )
        }
    }

    override fun toString(): String {
        return "CommandPlayer(type=$type, requirements=$requirements, commands=$commands)"
    }
}


