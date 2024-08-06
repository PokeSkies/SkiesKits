package com.pokeskies.skieskits.storage

import com.google.gson.*
import com.pokeskies.skieskits.utils.Utils
import java.lang.reflect.Type

enum class StorageType(val identifier: String) {
    JSON("json"),
    MONGO("mongo"),
    MYSQL("mysql"),
    SQLITE("sqlite");

    companion object {
        fun valueOfAnyCase(identifier: String): StorageType? {
            for (type in entries) {
                if (identifier.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class StorageTypeAdaptor : JsonSerializer<StorageType>, JsonDeserializer<StorageType> {
        override fun serialize(src: StorageType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.identifier)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): StorageType {
            val storageType = StorageType.valueOfAnyCase(json.asString)

            if (storageType == null) {
                Utils.printError("Could not deserialize Storage Type '${json.asString}' using SQLite as backup!")
                return StorageType.SQLITE
            }

            return storageType
        }
    }
}
