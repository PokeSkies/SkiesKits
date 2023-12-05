package com.pokeskies.skieskits.storage.database.sql

import com.google.gson.reflect.TypeToken
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.storage.IStorage
import com.pokeskies.skieskits.storage.StorageType
import com.pokeskies.skieskits.storage.database.sql.providers.MySQLProvider
import com.pokeskies.skieskits.storage.database.sql.providers.SQLiteProvider
import java.lang.reflect.Type
import java.sql.SQLException
import java.util.*


class SQLStorage(config: MainConfig.Storage) : IStorage {
    private val connectionProvider: ConnectionProvider = when (config.type) {
        StorageType.MYSQL -> MySQLProvider(config)
        StorageType.SQLITE -> SQLiteProvider(config)
        else -> throw IllegalStateException("Invalid storage type!")
    }
    private val type: Type = object : TypeToken<MutableMap<String, KitData>>() {}.type

    init {
        connectionProvider.init()
    }

    override fun getUser(uuid: UUID): UserData {
        var kits: MutableMap<String, KitData> = mutableMapOf()
        try {
            connectionProvider.createConnection().use {
                val statement = it.createStatement()
                val result = statement.executeQuery(String.format("SELECT * FROM userdata WHERE uuid='%s'", uuid.toString()))
                if (result != null && result.next()) {
                    kits = SkiesKits.INSTANCE.gson.fromJson(result.getString("kits"), type)
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return UserData(kits)
    }

    override fun saveUser(uuid: UUID, userData: UserData): Boolean {
        return try {
            connectionProvider.createConnection().use {
                val statement = it.createStatement()
                statement.execute(String.format("REPLACE INTO userdata (uuid, kits) VALUES ('%s', '%s')",
                    uuid.toString(),
                    SkiesKits.INSTANCE.gson.toJson(userData.kits)))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun close() {
        connectionProvider.shutdown()
    }
}