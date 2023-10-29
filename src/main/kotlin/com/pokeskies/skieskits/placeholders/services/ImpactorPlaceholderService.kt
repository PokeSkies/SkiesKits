package com.pokeskies.skiesguis.placeholders.services

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.placeholders.IPlaceholderService
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.impactdev.impactor.api.platform.players.PlatformPlayer
import net.impactdev.impactor.api.platform.sources.PlatformSource
import net.impactdev.impactor.api.utility.Context
import net.impactdev.impactor.core.text.processors.MiniMessageProcessor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.minecraft.server.network.ServerPlayerEntity

/*
    This class will parse Impactor based placeholders if the mod is present. Out of the box, Impactor will
    process color codes too, which breaks the compatibility with also parsing placeholders with other mods.
    A custom MiniMessageProcessor is created with an empty TagResolver list, that way it will not process
    any color codes and will just pass a string back with only Impactor placeholders processed.
 */
class ImpactorPlaceholderService : IPlaceholderService {
    private val processor = MiniMessageProcessor(
        MiniMessage.builder()
            .tags(TagResolver.builder().build())
            .build()
    )

    init {
        Utils.printInfo("Impactor mod found! Enabling placeholder integration...")
    }

    override fun parsePlaceholders(player: ServerPlayerEntity, text: String, kitId: String, kit: Kit, kitData: KitData): String {
        val platformPlayer = PlatformPlayer.getOrCreate(player.uuid)
        return SkiesKits.INSTANCE.adventure!!.toNative(
            processor.parse(platformPlayer, text, Context().append(PlatformSource::class.java, platformPlayer))
        ).string
    }
}