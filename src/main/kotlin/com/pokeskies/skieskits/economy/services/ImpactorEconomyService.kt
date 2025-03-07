package com.pokeskies.skieskits.economy.services

import com.pokeskies.skieskits.economy.IEconomyService
import com.pokeskies.skieskits.utils.Utils
import net.impactdev.impactor.api.economy.EconomyService
import net.impactdev.impactor.api.economy.accounts.Account
import net.impactdev.impactor.api.economy.currency.Currency
import net.kyori.adventure.key.Key
import net.minecraft.server.level.ServerPlayer
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CompletableFuture

class ImpactorEconomyService : IEconomyService {
    init {
        Utils.printInfo("Impactor Economy Service has been found and loaded for any Currency actions/requirements!")
    }

    override fun balance(player: ServerPlayer, currency: String) : Double {
        return getAccount(player.uuid, getCurrency(currency)).thenCompose(Account::balanceAsync).join().toDouble()
    }

    override fun withdraw(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        return getAccount(player.uuid, getCurrency(currency)).join()
            .withdrawAsync(BigDecimal(amount)).join().successful()
    }

    override fun deposit(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        return getAccount(player.uuid, getCurrency(currency)).join()
            .depositAsync(BigDecimal(amount)).join().successful()
    }

    override fun set(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        return getAccount(player.uuid, getCurrency(currency)).join()
            .setAsync(BigDecimal(amount)).join().successful()
    }

    private fun getAccount(uuid: UUID, currency: Currency): CompletableFuture<Account> {
        return EconomyService.instance().account(currency, uuid)
    }

    private fun getCurrency(id: String) : Currency {
        if (id.isEmpty()) {
            return EconomyService.instance().currencies().primary()
        }

        val currency: Optional<Currency> = EconomyService.instance().currencies().currency(Key.key(id))
        if (currency.isEmpty) {
            Utils.printError("Could not find a currency by the ID $id! Valid currencies: ${EconomyService.instance().currencies()}")
            return EconomyService.instance().currencies().primary()
        }

        return currency.get()
    }
}
