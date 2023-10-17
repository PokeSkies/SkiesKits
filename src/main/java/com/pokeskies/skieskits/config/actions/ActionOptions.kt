package com.pokeskies.skieskits.config.actions

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import net.minecraft.server.network.ServerPlayerEntity

class ActionOptions(
    val claimed: Map<String, Action> = emptyMap(),
    @SerializedName("on_cooldown")
    val onCooldown: Map<String, Action> = emptyMap(),
    @SerializedName("no_permission")
    val noPermission: Map<String, Action> = emptyMap(),
    @SerializedName("max_uses")
    val maxUses: Map<String, Action> = emptyMap(),
    @SerializedName("failed_requirements")
    val failedRequirements: Map<String, Action> = emptyMap()
) {
    fun executeClaimedActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in claimed) {
            if (action.checkRequirements(player)) {
                action.execute(player, kitId, kit, kitData)
                action.executeSuccessActions(player, kitId, kit, kitData)
            } else {
                action.executeDenyActions(player, kitId, kit, kitData)
            }
        }
    }

    fun executeCooldownActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in onCooldown) {
            if (action.checkRequirements(player)) {
                action.execute(player, kitId, kit, kitData)
                action.executeSuccessActions(player, kitId, kit, kitData)
            } else {
                action.executeDenyActions(player, kitId, kit, kitData)
            }
        }
    }

    fun executePermissionActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in noPermission) {
            if (action.checkRequirements(player)) {
                action.execute(player, kitId, kit, kitData)
                action.executeSuccessActions(player, kitId, kit, kitData)
            } else {
                action.executeDenyActions(player, kitId, kit, kitData)
            }
        }
    }

    fun executeUsesActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in maxUses) {
            if (action.checkRequirements(player)) {
                action.execute(player, kitId, kit, kitData)
                action.executeSuccessActions(player, kitId, kit, kitData)
            } else {
                action.executeDenyActions(player, kitId, kit, kitData)
            }
        }
    }

    fun executeRequirementsActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in failedRequirements) {
            if (action.checkRequirements(player)) {
                action.execute(player, kitId, kit, kitData)
                action.executeSuccessActions(player, kitId, kit, kitData)
            } else {
                action.executeDenyActions(player, kitId, kit, kitData)
            }
        }
    }

    override fun toString(): String {
        return "ActionOptions(claimed=$claimed, onCooldown=$onCooldown, noPermission=$noPermission, maxUses=$maxUses, failedRequirements=$failedRequirements)"
    }
}