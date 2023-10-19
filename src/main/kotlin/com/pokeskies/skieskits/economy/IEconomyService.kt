package com.pokeskies.skieskits.economy

import com.pokeskies.skieskits.economy.services.ImpactorService
import com.pokeskies.skieskits.economy.services.PebblesService
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

interface IEconomyService {
    fun balance(player: ServerPlayerEntity, currency: String = "") : Double
    fun withdraw(player: ServerPlayerEntity, amount: Double, currency: String = "") : Boolean
    fun deposit(player: ServerPlayerEntity, amount: Double, currency: String = "") : Boolean
    fun set(player: ServerPlayerEntity, amount: Double, currency: String = "") : Boolean

    companion object {
        fun getEconomyService(economyType: EconomyType) : IEconomyService? {
            if (!economyType.isPresent()) return null

            return try {
                when (economyType) {
                    EconomyType.IMPACTOR -> ImpactorService()
                    EconomyType.PEBBLES -> PebblesService()
                }
            } catch (ex: Exception) {
                Utils.error("There was an exception while initializing the Economy Service: ${economyType}. Is it loaded?")
                ex.printStackTrace()
                null
            }
        }
    }
}