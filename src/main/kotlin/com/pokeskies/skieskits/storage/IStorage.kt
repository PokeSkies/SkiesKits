package com.pokeskies.skieskits.storage

import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.storage.database.MongoStorage
import com.pokeskies.skieskits.storage.database.sql.SQLStorage
import com.pokeskies.skieskits.storage.file.FileStorage
import java.util.*

interface IStorage {
    companion object {
        fun load(config: MainConfig.Storage): IStorage {
            return when (config.type) {
                StorageType.JSON -> FileStorage()
                StorageType.MONGO -> MongoStorage(config)
                StorageType.MYSQL, StorageType.SQLITE -> SQLStorage(config)
            }
        }
    }

    fun getUser(uuid: UUID): UserData

    fun saveUser(uuid: UUID, userData: UserData)

    fun close() {}
}