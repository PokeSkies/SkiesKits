package com.pokeskies.skieskits.storage.database.sql.providers

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.MainConfig
import com.zaxxer.hikari.HikariConfig
import java.io.File

class H2Provider(config: MainConfig.Storage) : HikariCPProvider(config) {

    override fun getConnectionURL(): String = String.format(
        "jdbc:h2:%s;AUTO_SERVER=TRUE",
        File(SkiesKits.INSTANCE.configDir, "storage.db").toPath().toAbsolutePath()
    )

    override fun getDriverClassName(): String = "org.h2.Driver"
    override fun getDriverName(): String = "h2"
    override fun configure(config: HikariConfig) {}


}