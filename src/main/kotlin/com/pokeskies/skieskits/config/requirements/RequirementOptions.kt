package com.pokeskies.skieskits.config.requirements

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.data.KitData
import net.minecraft.server.level.ServerPlayer

class RequirementOptions(
    val requirements: Map<String, Requirement> = emptyMap(),
    @SerializedName("deny_actions")
    val denyActions: Map<String, Action> = emptyMap(),
    @SerializedName("success_actions")
    val successActions: Map<String, Action> = emptyMap()
) {
    fun executeDenyActions(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in denyActions) {
            action.attemptExecution(player, kitId, kit, kitData)
        }
    }

    fun executeSuccessActions(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in successActions) {
            action.attemptExecution(player, kitId, kit, kitData)
        }
    }

    override fun toString(): String {
        return "RequirementOptions(requirements=$requirements, denyActions=$denyActions, successActions=$successActions)"
    }
}
