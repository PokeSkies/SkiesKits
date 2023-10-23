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
import com.pokeskies.skieskits.utils.Utils
import java.lang.reflect.Type
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*


class SQLStorage(config: MainConfig.Storage) : IStorage {
    private var database: SQLDatabase?
    private val type: Type = object : TypeToken<MutableMap<String, KitData>>() {}.type

    init {
        database = when(config.type) {
            StorageType.MYSQL -> MySQLProvider(config)
            StorageType.SQLITE -> SQLiteProvider(config)
            else -> null
        }

        if (database == null) {
            Utils.error("The database returned null while initializing! Please check the storage configuration options.")
        }
    }

    override fun getUser(uuid: UUID): UserData {
        if (database == null) {
            Utils.error("The database connection was not completed! Please check the storage configuration options.")
            return UserData()
        }

        var kits: MutableMap<String, KitData> = mutableMapOf()
        try {
            val result: ResultSet? = database!!.executeQuery(String.format("SELECT * FROM userdata WHERE uuid='%s'", uuid.toString()))
            if (result != null && result.next()) {
                kits = SkiesKits.INSTANCE.gson.fromJson(result.getString("kits"), type)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return UserData(kits)
    }

    override fun saveUser(uuid: UUID, userData: UserData) {
        if (database == null) {
            Utils.error("The database connection was not completed! Please check the storage configuration options.")
            return
        }

        try {
            database!!.execute(
                String.format(
                    "REPLACE INTO userdata (uuid, kits) VALUES ('%s', '%s')",
                    uuid.toString(),
                    SkiesKits.INSTANCE.gson.toJson(userData.kits)
                )
            )
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun close() {
        if (database != null) {
            database!!.closeConnection()
        }
    }
}