package com.pokeskies.skieskits.config.requirements

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.data.KitData
import net.minecraft.server.network.ServerPlayerEntity

class RequirementOptions(
    val requirements: Map<String, Requirement> = emptyMap(),
    @SerializedName("deny_actions")
    val denyActions: Map<String, Action> = emptyMap(),
    @SerializedName("success_actions")
    val successActions: Map<String, Action> = emptyMap()
) {
    fun executeDenyActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in denyActions) {
            action.execute(player, kitId, kit, kitData)
        }
    }

    fun executeSuccessActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in successActions) {
            action.execute(player, kitId, kit, kitData)
        }
    }

    override fun toString(): String {
        return "RequirementOptions(requirements=$requirements, denyActions=$denyActions, successActions=$successActions)"
    }
}