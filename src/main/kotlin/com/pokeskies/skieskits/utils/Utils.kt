package com.pokeskies.skieskits.utils

import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.reflect.Type
import java.util.function.Function


object Utils {
    val miniMessage: MiniMessage = MiniMessage.miniMessage()

    fun parsePlaceholders(player: ServerPlayerEntity, text: String, kitId: String?, kit: Kit?, kitData: KitData?): String {
        return SkiesKits.INSTANCE.placeholderManager.parse(player, text, kitId, kit, kitData)
    }

    fun deserializeText(text: String): Text {
        return SkiesKits.INSTANCE.adventure!!.toNative(miniMessage.deserialize(text))
    }

    fun printDebug(message: String?, bypassCheck: Boolean = false) {
        if (bypassCheck || SkiesKits.INSTANCE.configManager.config.debug)
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
    data class RegistrySerializer<T>(val registry: Registry<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T? {
            var parsed = if (json.isJsonPrimitive) registry.get(Identifier.tryParse(json.asString)) else null
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
                codec.decode(JsonOps.INSTANCE, json).getOrThrow(false) { }.first
            } catch (e: Throwable) {
                printError("There was an error while deserializing a Codec: $codec")
                null
            }
        }

        override fun serialize(src: T?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return try {
                if (src != null)
                    codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false) { }
                else
                    JsonNull.INSTANCE
            } catch (e: Throwable) {
                printError("There was an error while serializing a Codec: $codec")
                JsonNull.INSTANCE
            }
        }
    }
}

fun <A, B> Codec<A>.recordCodec(id: String, getter: Function<B, A>): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).forGetter(getter)
}

fun <A, B> Codec<A>.optionalRecordCodec(id: String, getter: Function<B, A>, default: A): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).orElse(default).forGetter(getter)
}