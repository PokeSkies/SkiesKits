package com.pokeskies.skieskits.config.requirements

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

abstract class Requirement(
    val type: RequirementType? = null,
    val comparison: ComparisonType = ComparisonType.EQUALS,
    @SerializedName("deny_actions")
    val denyActions: Map<String, Action> = emptyMap(),
    @SerializedName("success_actions")
    val successActions: Map<String, Action> = emptyMap()
) {
    abstract fun passesRequirements(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData): Boolean

    open fun allowedComparisons(): List<ComparisonType> {
        return emptyList()
    }

    fun checkComparison(): Boolean {
        if (!allowedComparisons().contains(comparison)) {
            Utils.printError("Error while executing a Requirement check! Comparison ${comparison.identifier} is not allowed: ${allowedComparisons().map { it.identifier }}")
            return false
        }
        return true
    }

    fun executeSuccessActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in successActions) {
            action.attemptExecution(player, kitId, kit, kitData)
        }
    }

    fun executeDenyActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in denyActions) {
            action.attemptExecution(player, kitId, kit, kitData)
        }
    }

    override fun toString(): String {
        return "Requirement(type=$type, comparison=$comparison, denyActions=$denyActions, successActions=$successActions)"
    }
}
