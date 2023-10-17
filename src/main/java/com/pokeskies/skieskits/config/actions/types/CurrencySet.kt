package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CurrencySet(
    type: ActionType = ActionType.CURRENCY_SET,
    requirements: RequirementOptions? = RequirementOptions(),
    private val currency: String = "",
    private val amount: Double = 0.0
) : Action(type, requirements) {
    override fun execute(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")

        val service = SkiesKits.INSTANCE.economyService
        if (service == null) {
            Utils.error("Currency Set Action was executed but no valid Economy Service could be found.")
            return
        }

        service.set(player, amount, currency)
    }

    override fun toString(): String {
        return "CurrencySet(type=$type, requirements=$requirements, currency=$currency, amount=$amount)"
    }
}