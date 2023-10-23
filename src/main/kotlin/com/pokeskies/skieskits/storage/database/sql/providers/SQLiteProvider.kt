package com.pokeskies.skieskits.storage.database.sql.providers

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.storage.database.sql.SQLDatabase
import com.pokeskies.skieskits.utils.Utils
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SQLiteProvider(config: MainConfig.Storage) : SQLDatabase(config) {
    override fun getConnectionURL(): String {
        return String.format(
                "jdbc:sqlite:%s",
                File(SkiesKits.INSTANCE.configDir, "storage.db").toPath().toAbsolutePath()
            )
    }

    override fun createConnection(): Connection? {
        try {
            return DriverManager.getConnection(getConnectionURL())
        } catch (e: SQLException) {
            Utils.error(e.message)
        }
        return null
    }
}