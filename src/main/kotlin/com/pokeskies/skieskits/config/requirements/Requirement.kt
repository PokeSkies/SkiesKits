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
    abstract fun check(player: ServerPlayerEntity): Boolean

    open fun getAllowedComparisons(): List<ComparisonType> {
        return emptyList()
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }

    fun checkComparison(): Boolean {
        if (!getAllowedComparisons().contains(comparison)) {
            Utils.error("Error while executing a Requirement check! Comparison ${comparison.identifier} is not allowed: ${getAllowedComparisons().map { it.identifier }}")
            return false
        }
        return true
    }

    fun executeSuccessActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in successActions) {
            action.execute(player, kitId, kit, kitData)
        }
    }

    fun executeDenyActions(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        for ((id, action) in denyActions) {
            action.execute(player, kitId, kit, kitData)
        }
    }

    override fun toString(): String {
        return "Requirement(type=$type, comparison=$comparison, denyActions=$denyActions, successActions=$successActions)"
    }
}