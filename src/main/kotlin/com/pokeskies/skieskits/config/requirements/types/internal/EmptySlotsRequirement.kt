package com.pokeskies.skieskits.config.requirements.types.internal

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class EmptySlotsRequirement(
    type: RequirementType = RequirementType.ITEM,
    comparison: ComparisonType = ComparisonType.EQUALS,
    val amount: Int = 0,
) : Requirement(type, comparison) {
    override fun passesRequirements(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData): Boolean {
        if (!checkComparison())
            return false

        var amountFound = 0

        for (itemStack in player.inventory.main) {
            if (itemStack.isEmpty) {
                amountFound++
            }
        }

        Utils.printDebug("Checking a ${type?.identifier} Requirement with empty slots found='$amountFound': $this")

        return when (comparison) {
            ComparisonType.EQUALS -> amountFound == amount
            ComparisonType.NOT_EQUALS -> amountFound != amount
            ComparisonType.GREATER_THAN -> amountFound > amount
            ComparisonType.LESS_THAN -> amountFound < amount
            ComparisonType.GREATER_THAN_OR_EQUALS -> amountFound >= amount
            ComparisonType.LESS_THAN_OR_EQUALS -> amountFound <= amount
        }
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.values().toList()
    }

    override fun toString(): String {
        return "EmptySlotRequirement(amount=$amount)"
    }
}
