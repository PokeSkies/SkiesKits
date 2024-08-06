package com.pokeskies.skieskits.config.actions

import ca.landonjw.gooeylibs2.api.tasks.Task
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.level.ServerPlayer
import kotlin.random.Random

abstract class Action(
    val type: ActionType,
    val delay: Long = 0,
    val chance: Double = 0.0,
    val requirements: RequirementOptions? = RequirementOptions()
) {
    // Will do a chance check and then apply any delay
    open fun attemptExecution(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        if (chance > 0.0 && chance < 1.0) {
            val roll = Random.nextFloat()
            Utils.printDebug("Attempting chance roll for $type Action. Result is: $roll <= $chance = ${roll <= chance}.")
            if (roll > chance) {
                Utils.printDebug("Failed chance roll for $type Action.")
                return
            }
        }

        if (delay <= 0) {
            executeAction(player, kitId, kit, kitData)
            return
        }
        Utils.printDebug("Delay found for $type Action. Waiting $delay ticks before execution.")

        Task.builder()
            .execute { task -> executeAction(player, kitId, kit, kitData) }
            .delay(delay)
            .build()
    }

    abstract fun executeAction(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData)

    fun checkRequirements(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData): Boolean {
        if (requirements != null) {
            for (requirement in requirements.requirements) {
                if (!requirement.value.checkRequirements(player, kitId, kit, kitData)) {
                    return false
                }
            }
        }
        return true
    }

    fun executeDenyActions(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        if (requirements != null) {
            for ((id, action) in requirements.denyActions) {
                action.attemptExecution(player, kitId, kit, kitData)
            }
        }
    }

    fun executeSuccessActions(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        if (requirements != null) {
            for ((id, action) in requirements.successActions) {
                action.attemptExecution(player, kitId, kit, kitData)
            }
        }
    }

    override fun toString(): String {
        return "Action(type=$type, requirements=$requirements)"
    }
}
