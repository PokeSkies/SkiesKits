package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CurrencyDeposit(
    type: ActionType = ActionType.CURRENCY_DEPOSIT,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val currency: String = "",
    private val amount: Double = 0.0
) : Action(type, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")

        val service = SkiesKits.INSTANCE.economyService
        if (service == null) {
            Utils.printError("Currency Deposit Action was executed but no valid Economy Service could be found.")
            return
        }

        service.deposit(player, amount, currency)
    }

    override fun toString(): String {
        return "CurrencyDeposit(type=$type, requirements=$requirements, currency=$currency, amount=$amount)"
    }
}
