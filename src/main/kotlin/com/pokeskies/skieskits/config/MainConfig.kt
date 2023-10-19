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
        val password: String = ""
    ) {
        override fun toString(): String {
            return "Storage(type=$type, host='$host', port=$port," +
                    " database='$database', username='$username', password='$password')"
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
        val kitFailedRequirements: String = ""
    ) {
        override fun toString(): String {
            return "Messages(kit_not_found='$kitNotFound', kit_received='$kitReceived', kit_failed_uses='$kitFailedUses'," +
                    " kit_failed_cooldown='$kitFailedCooldown', kit_failed_requirements='$kitFailedRequirements')"
        }
    }

    override fun toString(): String {
        return "MainConfig(debug=$debug, economy=$economy, storage=$storage, messages=$messages)"
    }
}