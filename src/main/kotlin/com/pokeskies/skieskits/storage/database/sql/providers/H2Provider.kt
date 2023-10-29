package com.pokeskies.skieskits.storage.database.sql.providers

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.storage.database.sql.SQLDatabase
import com.pokeskies.skieskits.utils.Utils
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class H2Provider(config: MainConfig.Storage) : SQLDatabase(config) {
    override fun getConnectionURL(): String {
        return String.format(
                "jdbc:h2:%s;AUTO_SERVER=TRUE",
                File(SkiesKits.INSTANCE.configDir, "storage.db").toPath().toAbsolutePath()
            )
    }

    override fun createConnection(): Connection? {
        try {
            return DriverManager.getConnection(getConnectionURL())
        } catch (e: SQLException) {
            Utils.printError(e.message)
        }
        return null
    }
}