package com.pokeskies.skieskits.storage.database.sql.providers

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.MainConfig
import com.zaxxer.hikari.HikariConfig
import java.io.File

class SQLiteProvider(config: MainConfig.Storage) : HikariCPProvider(config) {
    override fun getConnectionURL(): String = String.format(
        "jdbc:sqlite:%s",
        File(SkiesKits.INSTANCE.configDir, "storage.db").toPath().toAbsolutePath()
    )


    override fun getDriverClassName(): String = "org.sqlite.JDBC"
    override fun getDriverName(): String = "sqlite"
    override fun configure(config: HikariConfig) {}
}