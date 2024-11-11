package com.pokeskies.skieskits.config.requirements.types.extensions.plan

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.PlanExtensionHelper
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.level.ServerPlayer

class PlanPlaytimeRequirement(
    type: RequirementType = RequirementType.PERMISSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val time: Long = 0
) : Requirement(type, comparison) {
    override fun passesRequirements(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData): Boolean {
        if (!checkComparison())
            return false

        val playtime = PlanExtensionHelper.getPlaytime(player.uuid)

        Utils.printDebug("Checking a ${type?.identifier} Requirement with playtime='$playtime': $this")

        return when (comparison) {
            ComparisonType.EQUALS -> playtime == time
            ComparisonType.NOT_EQUALS -> playtime != time
            ComparisonType.GREATER_THAN -> playtime > time
            ComparisonType.LESS_THAN -> playtime < time
            ComparisonType.GREATER_THAN_OR_EQUALS -> playtime >= time
            ComparisonType.LESS_THAN_OR_EQUALS -> playtime <= time
        }
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.values().toList()
    }

    override fun toString(): String {
        return "PlanPlaytimeRequirement(comparison=$comparison, time=$time)"
    }

}
