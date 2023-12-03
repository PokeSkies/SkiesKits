package com.pokeskies.skieskits.config

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.economy.EconomyType
import com.pokeskies.skieskits.storage.StorageType

class MainConfig(
    var debug: Boolean = false,
    var economy: EconomyType = EconomyType.IMPACTOR,
    var storage: Storage = Storage(),
    val messages: Messages = Messages()
) {

    class Storage(
        val type: StorageType = StorageType.JSON,
        val host: String = "",
        val port: Int = 3306,
        val database: String = "skieskits",
        val username: String = "root",
        val password: String = "",
        val properties: Map<String, String> = mapOf("useUnicode" to "true", "characterEncoding" to "utf8"),
        val poolSettings: StoragePoolSettings = StoragePoolSettings()
    ) {
        override fun toString(): String {
            return "Storage(type=$type, host='$host', port=$port," +
                    " database='$database', username='$username', password='$password')"
        }
    }

    class StoragePoolSettings(val maximumPoolSize: Int = 10,
                              val minimumIdle: Int = 10,
                              val keepaliveTime: Long = 0,
                              val connectionTimeout: Long = 30000,
                              val idleTimeout: Long = 600000,
                              val maxLifetime: Long = 1800000) {
        override fun toString(): String {
            return "StoragePoolSettings(maximumPoolSize=$maximumPoolSize, minimumIdle=$minimumIdle," +
                    " keepaliveTime=$keepaliveTime, connectionTimeout=$connectionTimeout," +
                    " idleTimeout=$idleTimeout, maxLifetime=$maxLifetime)"
        }
    }

    class Messages(
        @SerializedName("kit_not_found")
        val kitNotFound: String = "",
        @SerializedName("kit_received")
        val kitReceived: String = "",
        @SerializedName("kit_failed_uses")
        val kitFailedUses: String = "",
        @SerializedName("kit_failed_cooldown")
        val kitFailedCooldown: String = "",
        @SerializedName("kit_failed_requirements")
        val kitFailedRequirements: String = "",
        @SerializedName("kit_no_permission")
        val kitNoPermission: String = ""
    ) {
        override fun toString(): String {
            return "Messages(kit_not_found='$kitNotFound', kit_received='$kitReceived', kit_failed_uses='$kitFailedUses'," +
                    " kit_failed_cooldown='$kitFailedCooldown', kit_failed_requirements='$kitFailedRequirements'," +
                    " kit_no_permission='$kitNoPermission')"
        }
    }

    override fun toString(): String {
        return "MainConfig(debug=$debug, economy=$economy, storage=$storage, messages=$messages)"
    }
}