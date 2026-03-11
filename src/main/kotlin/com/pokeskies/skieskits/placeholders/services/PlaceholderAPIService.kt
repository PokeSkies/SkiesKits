package com.pokeskies.skieskits.placeholders.services

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.placeholders.IPlaceholderService
import com.pokeskies.skieskits.placeholders.PlayerPlaceholder
import com.pokeskies.skieskits.placeholders.ServerPlaceholder
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.PlaceholderResult
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

class PlaceholderAPIService : IPlaceholderService {
    init {
        Utils.printInfo("PlaceholderAPI mod found! Enabling placeholder integration...")
    }

    override fun registerPlayer(placeholder: PlayerPlaceholder) {
        placeholder.id().forEach { id ->
            Placeholders.register(ResourceLocation.fromNamespaceAndPath("skieskits", id)) { ctx, arg ->
                val player = ctx.player ?: return@register PlaceholderResult.invalid("NO PLAYER")
                val args = arg?.split(":") ?: emptyList()
                val result = placeholder.handle(player, args)
                return@register if (result.isSuccessful) {
                    PlaceholderResult.value(SkiesKits.INSTANCE.adventure.toNative(result.asComponent()))
                } else {
                    PlaceholderResult.invalid(result.string)
                }
            }
        }
    }

    override fun registerServer(placeholder: ServerPlaceholder) {
        placeholder.id().forEach { id ->
            Placeholders.register(ResourceLocation.fromNamespaceAndPath("skieskits", id)) { _, arg ->
                val args = arg?.split(":") ?: emptyList()
                val result = placeholder.handle(args)
                return@register if (result.isSuccessful) {
                    PlaceholderResult.value(SkiesKits.INSTANCE.adventure.toNative(result.asComponent()))
                } else {
                    PlaceholderResult.invalid(result.string)
                }
            }
        }
    }

    override fun finalizeRegister() {

    }

    override fun parse(player: ServerPlayer, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String {
        return Placeholders.parseText(Component.literal(text),
            PlaceholderContext.of(player)
        ).string
    }
}
