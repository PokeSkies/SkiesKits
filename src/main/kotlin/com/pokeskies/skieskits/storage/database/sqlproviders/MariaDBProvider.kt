package com.pokeskies.skieskits.storage.database.sqlproviders

import com.pokeskies.skieskits.storage.database.SQLDatabaseProvider
import com.pokeskies.skieskits.utils.Utils.error
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

class MariaDBProvider(
    val host: String? = null,
    val database: String? = null,
    val username: String? = null,
    val password: String? = null,
    val port: Int = 0
) : SQLDatabaseProvider {
    private var connection: Connection? = null

    private val STATEMENT_INITIALIZE = "CREATE TABLE IF NOT EXISTS database (" +
            "uuid VARCHAR(36) NOT NULL," +
            "PRIMARY KEY (uuid)" +
            ")"

    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        val connection = createConnection()
        if (connection == null) {
            error("An error has occured while connecting to the database! Please check the configuration")
        } else {
            execute(STATEMENT_INITIALIZE)
        }
    }

    private fun getJdbc(): String {
        return String.format(
            "jdbc:mysql://%s:%s@%s:%d/%s",
            URLEncoder.encode(username, StandardCharsets.UTF_8),
            URLEncoder.encode(password, StandardCharsets.UTF_8),
            host,
            port,
            database
        )
    }

    override fun createConnection(): Connection? {
        try {
            if (connection == null || connection!!.isClosed) {
                connection = DriverManager.getConnection(getJdbc())
            }
        } catch (e: SQLException) {
            error(e.message)
        }
        return connection
    }

    override fun isConnected(): Boolean {
        return try {
            connection != null && !connection!!.isClosed
        } catch (e: SQLException) {
            false
        }
    }

    override fun closeConnection() {
        try {
            if (connection != null && !connection!!.isClosed) connection!!.close()
        } catch (e: SQLException) {
            error("Error while shutting down database connection!")
        }
    }

    override fun executeQuery(execution: String): ResultSet? {
        return try {
            val statement = connection!!.createStatement()
            statement.executeQuery(execution)
        } catch (e: SQLException) {
            null
        }
    }

    override fun executeUpdate(execution: String): Int {
        return try {
            val statement = connection!!.createStatement()
            statement.executeUpdate(execution)
        } catch (e: SQLException) {
            -1
        }
    }

    override fun execute(execution: String): Boolean {
        return try {
            val statement = connection!!.createStatement()
            statement.execute(execution)
            statement.close()
            true
        } catch (e: SQLException) {
            false
        }
    }
}