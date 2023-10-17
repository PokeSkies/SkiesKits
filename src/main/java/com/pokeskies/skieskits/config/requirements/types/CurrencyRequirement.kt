package com.pokeskies.skieskits.config.requirements.types

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CurrencyRequirement(
    type: RequirementType = RequirementType.CURRENCY,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val currency: String = "",
    private val amount: Double = 0.0
) : Requirement(type, comparison) {
    override fun check(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        val service = SkiesKits.INSTANCE.economyService
        if (service == null) {
            Utils.error("Currency Requirement was checked but no valid Economy Service could be found.")
            return false
        }

        val balance = service.balance(player, currency)

        return when (comparison) {
            ComparisonType.EQUALS -> balance == amount
            ComparisonType.NOT_EQUALS -> balance != amount
            ComparisonType.GREATER_THAN -> balance > amount
            ComparisonType.LESS_THAN -> balance < amount
            ComparisonType.GREATER_THAN_OR_EQUALS -> balance >= amount
            ComparisonType.LESS_THAN_OR_EQUALS -> balance <= amount
        }
    }

    override fun getAllowedComparisons(): List<ComparisonType> {
        return ComparisonType.values().toList()
    }

    override fun toString(): String {
        return "CurrencyRequirement(currency='$currency', amount=$amount)"
    }
}