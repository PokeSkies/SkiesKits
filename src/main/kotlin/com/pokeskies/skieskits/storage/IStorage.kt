package com.pokeskies.skieskits.storage

import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.storage.database.MongoStorage
import com.pokeskies.skieskits.storage.database.sql.SQLStorage
import com.pokeskies.skieskits.storage.file.FileStorage
import java.util.*
import java.util.concurrent.CompletableFuture

interface IStorage {
    companion object {
        fun load(config: MainConfig.Storage): IStorage {
            val backend = when (config.type) {
                StorageType.JSON -> FileStorage()
                StorageType.MONGO -> MongoStorage(config)
                StorageType.MYSQL, StorageType.SQLITE -> SQLStorage(config)
            }
            return CachedStorage(backend)
        }
    }

    fun getUser(uuid: UUID): UserData
    fun saveUser(uuid: UUID, userData: UserData): CompletableFuture<Boolean>
    fun saveAll(users: Map<UUID, UserData>): CompletableFuture<Void> {
        val futures = users.map { (uuid, userData) -> saveUser(uuid, userData) }
        return CompletableFuture.allOf(*futures.toTypedArray())
    }
    fun close() {}
}
