package com.pokeskies.skieskits.utils

import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.chat.Component as NativeComponent
import java.lang.reflect.Type

object Utils {
    val miniMessage: MiniMessage = MiniMessage.miniMessage()

    fun parsePlaceholders(player: ServerPlayer, text: String?, kitId: String? = null, kit: Kit? = null, kitData: KitData? = null): String {
        if (text == null) return ""
        return SkiesKits.INSTANCE.placeholderManager.parse(player, text, kitId, kit, kitData)
    }

    fun deserializeText(text: String): Component {
        return miniMessage.deserialize(text)
    }

    fun deserializeNativeText(text: String): NativeComponent {
        val adventure = SkiesKits.INSTANCE.adventure
        return if (adventure != null) {
            adventure.asNative(deserializeText(text))
        } else {
            NativeComponent.literal(text)
        }
    }

    fun printDebug(message: String?, bypassCheck: Boolean = false) {
        if (bypassCheck || ConfigManager.CONFIG.debug)
            SkiesKits.LOGGER.info("[SkiesKits] DEBUG: $message")
    }

    fun printError(message: String?) {
        SkiesKits.LOGGER.error("[SkiesKits] ERROR: $message")
    }

    fun printInfo(message: String?) {
        SkiesKits.LOGGER.info("[SkiesKits] $message")
    }

    fun getFormattedTime(time: Long): String {
        if (time <= 0) return "0s"
        val timeFormatted: MutableList<String> = ArrayList()
        val days = time / 86400
        val hours = time % 86400 / 3600
        val minutes = time % 86400 % 3600 / 60
        val seconds = time % 86400 % 3600 % 60
        if (days > 0) {
            timeFormatted.add(days.toString() + "d")
        }
        if (hours > 0) {
            timeFormatted.add(hours.toString() + "h")
        }
        if (minutes > 0) {
            timeFormatted.add(minutes.toString() + "m")
        }
        if (seconds > 0) {
            timeFormatted.add(seconds.toString() + "s")
        }
        return java.lang.String.join(" ", timeFormatted)
    }

    // Thank you to Patbox for these wonderful serializers =)
    data class RegistrySerializer<T : Any>(val registry: Registry<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T? {
            val parsed = if (json.isJsonPrimitive) registry.getOptional(Identifier.parse(json.asString)).orElse(null) else null
            if (parsed == null)
                printError("There was an error while deserializing a Registry Type: $registry")
            return parsed
        }

        override fun serialize(src: T, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(registry.getKey(src).toString())
        }
    }

    data class CodecSerializer<T>(val codec: Codec<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T? {
            return try {
                codec.decode(JsonOps.INSTANCE, json).orThrow.first
            } catch (_: Throwable) {
                printError("There was an error while deserializing a Codec: $codec")
                null
            }
        }

        override fun serialize(src: T?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return try {
                if (src != null)
                    codec.encodeStart(JsonOps.INSTANCE, src).orThrow
                else
                    JsonNull.INSTANCE
            } catch (_: Throwable) {
                printError("There was an error while serializing a Codec: $codec")
                JsonNull.INSTANCE
            }
        }
    }
}
