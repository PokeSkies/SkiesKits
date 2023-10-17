package com.pokeskies.skieskits.storage

import com.pokeskies.skieskits.economy.EconomyType

enum class StorageType(val identifier: String) {
    JSON("json"),
    MONGO("mongo");

    companion object {
        fun valueOfAnyCase(identifier: String): StorageType? {
            for (type in values()) {
                if (identifier.equals(type.identifier, true)) return type
            }
            return null
        }
    }
}