package com.pokeskies.skieskits.placeholders.services

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.placeholders.IPlaceholderService
import com.pokeskies.skieskits.utils.Utils
import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.minecraft.server.level.ServerPlayer

class MiniPlaceholdersService : IPlaceholderService {
    private val miniMessage = MiniMessage.builder()
        .tags(TagResolver.builder().build())
        .build()

    init {
        Utils.printInfo("MiniPlaceholders mod found! Enabling placeholder integration...")
    }

    override fun parsePlaceholders(player: ServerPlayer, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String {
        val resolver = TagResolver.resolver(
            MiniPlaceholders.getGlobalPlaceholders(),
            MiniPlaceholders.getAudiencePlaceholders(player)
        )

        val parsed = miniMessage.deserialize(text, resolver)
        return SkiesKits.INSTANCE.adventure?.asNative(parsed)?.string ?: text
    }
}
