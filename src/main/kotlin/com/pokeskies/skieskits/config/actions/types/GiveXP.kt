package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.level.ServerPlayer

class GiveXP(
    type: ActionType = ActionType.GIVE_XP,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val amount: Int = 0,
    private val level: Boolean = false
) : Action(type, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        if (level) {
            player.giveExperienceLevels(amount)
        } else {
            player.giveExperiencePoints(amount)
        }
    }

    override fun toString(): String {
        return "GiveXP(type=$type, requirements=$requirements, amount=$amount, level=$level)"
    }
}
