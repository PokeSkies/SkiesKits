package com.pokeskies.skieskits.storage.database.sqlproviders

import com.pokeskies.skieskits.storage.database.SQLDatabaseProvider
import java.sql.Connection
import java.sql.ResultSet

class H2Provider : SQLDatabaseProvider {
    override fun createConnection(): Connection? {
        TODO("Not yet implemented")
    }

    override fun isConnected(): Boolean {
        TODO("Not yet implemented")
    }

    override fun closeConnection() {
        TODO("Not yet implemented")
    }

    override fun executeQuery(execution: String): ResultSet? {
        TODO("Not yet implemented")
    }

    override fun executeUpdate(execution: String): Int {
        TODO("Not yet implemented")
    }

    override fun execute(execution: String): Boolean {
        TODO("Not yet implemented")
    }
}