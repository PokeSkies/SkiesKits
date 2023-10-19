package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class GiveXP(
    type: ActionType = ActionType.GIVE_XP,
    requirements: RequirementOptions? = RequirementOptions(),
    private val amount: Int = 0,
    private val level: Boolean = false
) : Action(type, requirements) {
    override fun execute(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        if (level) {
            player.addExperienceLevels(amount)
        } else {
            player.addExperience(amount)
        }
    }

    override fun toString(): String {
        return "GiveXP(type=$type, requirements=$requirements, amount=$amount, level=$level)"
    }
}