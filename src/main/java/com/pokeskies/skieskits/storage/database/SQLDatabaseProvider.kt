package com.pokeskies.skieskits.storage.database

import java.sql.Connection
import java.sql.ResultSet

interface SQLDatabaseProvider {
    fun createConnection(): Connection?

    fun isConnected(): Boolean

    fun closeConnection()

    fun executeQuery(execution: String): ResultSet?

    fun executeUpdate(execution: String): Int

    fun execute(execution: String): Boolean
}