package com.pokeskies.skieskits.config.actions

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

abstract class Action(
    val type: ActionType,
    val requirements: RequirementOptions? = RequirementOptions()
) {
    abstract fun execute(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData)

    fun checkRequirements(player: ServerPlayerEntity): Boolean {
        if (requirements != null) {
            for (requirement in requirements.requirements) {
                if (!requirement.value.check(player)) {
                    return false
                }
            }
        }
        return true
    }

    fun executeDenyActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        if (requirements != null) {
            for ((id, action) in requirements.denyActions) {
                action.execute(player, kitId, kit, kitData)
            }
        }
    }

    fun executeSuccessActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        if (requirements != null) {
            for ((id, action) in requirements.successActions) {
                action.execute(player, kitId, kit, kitData)
            }
        }
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String, kitId: String, kit: Kit, kitData: KitData): String {
        return value
            .replace("%kit_name%", kit.getDisplayName(kitId))
            .replace("%player%", player.name.string)
            .replace("%kit_uses%", kitData.uses.toString())
            .replace("%kit_max_uses%", kit.maxUses.toString())
            .replace("%kit_cooldown%", Utils.getFormattedTime(kitData.getTimeRemaining(kit.cooldown)))
    }

    override fun toString(): String {
        return "Action(type=$type, requirements=$requirements)"
    }
}