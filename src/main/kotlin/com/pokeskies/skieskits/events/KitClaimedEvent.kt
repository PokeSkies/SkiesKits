package com.pokeskies.skieskits.events

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer

fun interface KitClaimedEvent {
    fun onKitClaimed(player: ServerPlayer, kitId: String)

    companion object {
        val EVENT: Event<KitClaimedEvent> = EventFactory.createArrayBacked(KitClaimedEvent::class.java) { listeners ->
            KitClaimedEvent { player, kitId ->
                listeners.forEach { it.onKitClaimed(player, kitId) }
            }
        }
    }
}
