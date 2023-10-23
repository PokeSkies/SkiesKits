package com.pokeskies.skieskits.storage.database.sql

import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.utils.Utils
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

abstract class SQLDatabase(val config: MainConfig.Storage) {
    private var connection: Connection? = null

    companion object {
        private const val USERDATA_INITIALIZE =
            "CREATE TABLE IF NOT EXISTS userdata (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "kits TEXT NOT NULL, " +
                    "PRIMARY KEY (uuid)" +
                    ")"
    }

    init {
        connection = createConnection()
        println("3 $connection")
        if (!isConnected()) {
            Utils.error("An error has occurred while connecting to the database! Please check the configuration")
        } else {
            execute(USERDATA_INITIALIZE)
        }
    }

    abstract fun getConnectionURL(): String

    abstract fun createConnection(): Connection?

    private fun isConnected(): Boolean {
        return try {
            connection != null && !connection!!.isClosed
        } catch (e: SQLException) {
            false
        }
    }

    fun closeConnection() {
        try {
            if (isConnected()) connection!!.close()
        } catch (e: SQLException) {
            Utils.error("Error while shutting down database connection!")
        }
    }

    fun executeQuery(execution: String): ResultSet? {
        if (!isConnected()) return null

        println("Executing Query $execution using $connection")

        return try {
            val statement = connection!!.createStatement()
            statement.executeQuery(execution)
        } catch (e: SQLException) {
            Utils.error("There was an error executing query statement: ${e.printStackTrace()}")
            null
        }
    }

    fun executeUpdate(execution: String): Int {
        if (!isConnected()) return -1

        println("Executing Update $execution using $connection")

        return try {
            val statement = connection!!.createStatement()
            statement.executeUpdate(execution)
        } catch (e: SQLException) {
            Utils.error("There was an error executing update statement: ${e.printStackTrace()}")
            -1
        }
    }

    fun execute(execution: String): Boolean {
        if (!isConnected()) return false

        println("Executing $execution using $connection")

        return try {
            val statement = connection!!.createStatement()
            statement.execute(execution)
            statement.close()
            true
        } catch (e: SQLException) {
            Utils.error("There was an error executing statement: ${e.printStackTrace()}")
            false
        }
    }
}