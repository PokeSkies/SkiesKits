package com.pokeskies.skieskits.config.requirements.types.internal

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.data.KitData
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.network.ServerPlayerEntity

class PermissionRequirement(
    type: RequirementType = RequirementType.PERMISSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    denyActions: Map<String, Action> = emptyMap(),
    successActions: Map<String, Action> = emptyMap(),
    private val permission: String = ""
) : Requirement(type, comparison, denyActions, successActions) {
    override fun passesRequirements(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData): Boolean {
        if (!checkComparison())
            return false

        if (permission.isNotEmpty()) {
            val value = Permissions.check(player, permission)
            return if (comparison == ComparisonType.NOT_EQUALS) !value else value
        }

        return true
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }

    override fun toString(): String {
        return "PermissionRequirement(permission='$permission')"
    }
}
