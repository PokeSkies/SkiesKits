package com.pokeskies.skieskits.storage.database.sql.providers

import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.storage.database.sql.SQLDatabase
import com.pokeskies.skieskits.utils.Utils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class MySQLProvider(config: MainConfig.Storage) : SQLDatabase(config) {
    override fun getConnectionURL(): String {
        if (config.username.isNotEmpty()) {
            if (config.password.isNotEmpty()) {
                return String.format(
                    "jdbc:mysql://%s:%s@%s:%d/%s",
                    URLEncoder.encode(config.username, StandardCharsets.UTF_8),
                    URLEncoder.encode(config.password, StandardCharsets.UTF_8),
                    config.host,
                    config.port,
                    config.database
                )
            }
            return String.format(
                "jdbc:mysql://%s@%s:%d/%s",
                URLEncoder.encode(config.username, StandardCharsets.UTF_8),
                config.host,
                config.port,
                config.database
            )
        }
        return String.format(
            "jdbc:mysql://%s:%d/%s",
            config.host,
            config.port,
            config.database
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